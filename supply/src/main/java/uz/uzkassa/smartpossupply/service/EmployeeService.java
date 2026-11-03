package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.constants.MessageConstants;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.dto.auth.ChangePasswordDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.billingpermission.BillingCompanyItem;
import uz.uzkassa.smartpossupply.dto.branch.BranchCoordinateDTO;
import uz.uzkassa.smartpossupply.dto.branch.CommonStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeCountByRoleDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeDetailDTO;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeListDTO;
import uz.uzkassa.smartpossupply.dto.employee.marketplace.MarketplaceEmployeeDTO;
import uz.uzkassa.smartpossupply.dto.employee.marketplace.MarketplaceEmployeeListDTO;
import uz.uzkassa.smartpossupply.dto.employee.mobile.EmployeeListMobileDTO;
import uz.uzkassa.smartpossupply.dto.factura.CompanyCheckDTO;
import uz.uzkassa.smartpossupply.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.smartpossupply.dto.user.FullNameDTO;
import uz.uzkassa.smartpossupply.dto.user.OwnerDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.filters.UserFilter;
import uz.uzkassa.smartpossupply.integration.SoliqClient;
import uz.uzkassa.smartpossupply.mappers.UserMapper;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqPermissionProducer;
import uz.uzkassa.smartpossupply.repository.RolePermissionRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
//@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EmployeeService extends BaseService {

    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    SoliqClient soliqClient;
    RolePermissionRepository rolePermissionRepository;
    PermissionService permissionService;
    RabbitMqPermissionProducer rabbitMqPermissionProducer;
    CacheService cacheService;
    ValidationContext validationContext;

    @Transactional
    public Long updateOwner(Long id, OwnerDTO ownerDTO) {
        return userRepository.findById(id).map(owner -> {
            Company company = getCurrentCompanyFromDB();
            if (!owner.getId().equals(company.getOwnerId())) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.OWNER_DOES_NOT_MATCH, new Object[]{owner.getName()})).get();
            }
            owner.setFirstName(ownerDTO.getFirstName());
            owner.setLastName(ownerDTO.getLastName());
            owner.setPatronymic(ownerDTO.getPatronymic());
            userRepository.save(owner);
            cacheService.evictUser(owner.getLogin());
            return owner.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    public OwnerDTO getOwner() {
        Company company = companyRepository.findByIdWithOwner(getCurrentCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", getCurrentCompanyId()));
        OwnerDTO ownerDTO = new OwnerDTO();
        if (company.getOwner() == null) {
            return ownerDTO;
        }
        ownerDTO.setId(company.getOwnerId());
        ownerDTO.setFirstName(company.getOwner().getFirstName());
        ownerDTO.setLastName(company.getOwner().getLastName());
        ownerDTO.setPatronymic(company.getOwner().getPatronymic());
        ownerDTO.setPhone(company.getOwner().getLogin());
        return ownerDTO;
    }

    @Transactional
    public Long create(EmployeeDTO employeeDTO) {
        if (StringUtils.isNotEmpty(employeeDTO.getLogin())) {
            if (userRepository.existsUserByLoginAndDeletedIsFalse(employeeDTO.getLogin())) {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                                new Object[]{
                                        localizationService.localize(User.class.getSimpleName()),
                                        localizationService.localize(MessageConstants.LOGIN),
                                        employeeDTO.getLogin(),
                                }
                        )).get();
            }
        }

        Long companyId = Optional.ofNullable(employeeDTO.getCompanyId()).orElse(getCurrentCompanyId());
        User employee = new User();
        employee.setTin(employeeDTO.getTin());
        employee.setPinfl(employeeDTO.getPinfl());
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setPatronymic(employeeDTO.getPatronymic());
        employee.setLogin((Optional.ofNullable(employeeDTO.getLogin()).orElse("99800" + RandomStringUtils.randomNumeric(7))).toLowerCase());
        employee.setPhone(employee.getLogin());
        employee.setPassword(passwordEncoder.encode(Optional.ofNullable(employeeDTO.getPassword()).orElse(SecurityUtils.generateActivationKey())));
        employee.setRole(employeeDTO.getRole());
        employee.setWarehouseId(employeeDTO.getWarehouseId());
        employee.setCompanyId(companyId);
        employee.setActivationKey(SecurityUtils.generateActivationKey());
        employee.setStatus(UserStatus.ACTIVE);
        employee.setSupervisorId(employeeDTO.getSupervisorId());
        if (CollectionUtils.isNotEmpty(employeeDTO.getBranchIds())) {
            employee.setBranchId(employeeDTO.getBranchIds().getFirst());
            employee.setBranches(new HashSet<>(branchRepository.findAllByIdInAndDeletedIsFalse(employeeDTO.getBranchIds())));
        }
        if (Role.ROLE_DIRECTOR.equals(employeeDTO.getRole()) || Role.ROLE_MAIN_ACCOUNTANT.equals(employeeDTO.getRole())) {
            userRepository.findFirstByCompanyIdAndRole(companyId, employeeDTO.getRole()).ifPresent(user -> {
                user.setStatus(UserStatus.BLOCKED);
                cacheService.clearByEntityType(user.getEntityType());
            });
        }
        userRepository.save(employee);
        cacheService.evictUser(employee.getLogin());

        rabbitMqPermissionProducer.save(
                new UserRolePermissionPayload(employee.getId(), rolePermissionRepository.findAllPermissionIdsByRole(employeeDTO.getRole()))
        );
        return employee.getId();
    }

    @Transactional
    public Long createMarketplaceEmployee(MarketplaceEmployeeDTO marketplaceEmployeeDTO) {
        validationContext.validate(marketplaceEmployeeDTO);
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName(marketplaceEmployeeDTO.getFirstName());
        employeeDTO.setLastName(marketplaceEmployeeDTO.getLastName());
        employeeDTO.setPatronymic(marketplaceEmployeeDTO.getPatronymic());
        employeeDTO.setLogin(marketplaceEmployeeDTO.getLogin());
        employeeDTO.setPassword(marketplaceEmployeeDTO.getPassword());
        employeeDTO.setCompanyId(marketplaceEmployeeDTO.getCompanyId());
        employeeDTO.setRole(Role.ROLE_VIEWER);

        return this.create(employeeDTO);
    }

    public EmployeeDetailDTO get(Long id) {
        return userRepository.getDetailForCabinet(id).map(employee -> {
            EmployeeDetailDTO employeeDetailDTO = new EmployeeDetailDTO();
            employeeDetailDTO.setId(employee.getId());
            employeeDetailDTO.setTin(employee.getTin());
            employeeDetailDTO.setPinfl(employee.getPinfl());
            employeeDetailDTO.setFirstName(employee.getFirstName());
            employeeDetailDTO.setLastName(employee.getLastName());
            employeeDetailDTO.setPatronymic(employee.getPatronymic());
            employeeDetailDTO.setLogin(employee.getLogin());
            employeeDetailDTO.setRole(employee.getRole());
            employeeDetailDTO.setStatus(employee.getStatus());
            employeeDetailDTO.setBranches(employee.getBranches().stream().map(branch -> {
                BranchCoordinateDTO branchCoordinateDTO = new BranchCoordinateDTO();
                branchCoordinateDTO.setId(branch.getId());
                branchCoordinateDTO.setName(branch.getName());
                branchCoordinateDTO.setLatitude(Optional.ofNullable(branch.getAddress()).map(Address::getLatitude).orElse(null));
                branchCoordinateDTO.setLongitude(Optional.ofNullable(branch.getAddress()).map(Address::getLongitude).orElse(null));
                return branchCoordinateDTO;
            }).toList());
            if (employee.getSupervisorId() != null) {
                employeeDetailDTO.setSupervisor(employee.getSupervisor().toCommonDTO());
            }
            if (employee.getWarehouseId() != null) {
                employeeDetailDTO.setWarehouse(employee.getWarehouse().toCommonDTO());
            }
            return employeeDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", id));

    }

    @Transactional
    public Long update(Long id, EmployeeDTO employeeDTO) {
        return userRepository.findById(id).map(employee -> {
            if (employeeDTO.getRole() != null && Role.getBranchRequiredRoles().contains(employeeDTO.getRole())) {
                if (CollectionUtils.isEmpty(employeeDTO.getBranchIds())) {
                    throw validationService.badRequestExceptionThrow("Требуется филиал").get();
                }
            }
            if (employeeDTO.getRole() != null && Role.getWarehouseRequiredRoles().contains(employeeDTO.getRole())) {
                if (employeeDTO.getWarehouseId() == null) {
                    throw validationService.badRequestExceptionThrow("Требуется склад").get();
                }
            }
            if (StringUtils.isNotEmpty(employeeDTO.getLogin()) && !employeeDTO.getLogin().equals(employee.getLogin())) {
                if (userRepository.existsUserByLoginAndIdNot(employeeDTO.getLogin(), employee.getId())) {
                    throw validationService.conflictExceptionThrow(
                            localizationService.localize(
                                    MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                                    new Object[]{
                                            localizationService.localize(User.class.getSimpleName()),
                                            localizationService.localize(MessageConstants.LOGIN),
                                            employeeDTO.getLogin(),
                                    }
                            )
                    ).get();
                }
                employee.setLogin(employeeDTO.getLogin());
            }

            employee.setTin(employeeDTO.getTin());
            employee.setPinfl(employeeDTO.getPinfl());
            employee.setFirstName(employeeDTO.getFirstName());
            employee.setLastName(employeeDTO.getLastName());
            employee.setPatronymic(employeeDTO.getPatronymic());

            if (employeeDTO.getRole() != null && !employee.getRole().equals(employeeDTO.getRole())) {
                employee.setRole(employeeDTO.getRole());
                permissionService.deleteUserPermissionsByUserId(employee.getId());
                rabbitMqPermissionProducer.save(
                        new UserRolePermissionPayload(
                                employee.getId(),
                                rolePermissionRepository.findAllPermissionIdsByRole(employeeDTO.getRole())
                        )
                );
            }
            if (employeeDTO.getPassword() != null) {
                employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
            }
            employee.setSupervisorId(employeeDTO.getSupervisorId());
            employee.setWarehouseId(employeeDTO.getWarehouseId());
            if (CollectionUtils.isNotEmpty(employeeDTO.getBranchIds())) {
                employee.setBranchId(employeeDTO.getBranchIds().getFirst());
                employee.getBranches().clear();
                employee.setBranches(new HashSet<>(branchRepository.findAllByIdInAndDeletedIsFalse(employeeDTO.getBranchIds())));
            } else {
                employee.setBranchId(null);
                employee.setBranches(new HashSet<>());
            }
            userRepository.save(employee);
            cacheService.evictUser(employee.getLogin());
            return employee.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, UserStatus status) {
        userRepository.findById(id).ifPresentOrElse(employee -> {
            employee.setStatus(status);
            userRepository.save(employee);
            cacheService.evictUser(employee.getLogin());
        }, () -> validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", id).get());
    }

    public Page<EmployeeListDTO> getList(UserFilter filter) {
        ResultList<User> resultList = userRepository.getResultList(filter);
        List<EmployeeListDTO> result = resultList
                .getList()
                .stream()
                .map(employee -> {
                    EmployeeListDTO employeeListDTO = new EmployeeListDTO();
                    employeeListDTO.setId(employee.getId());
                    employeeListDTO.setFirstName(employee.getFirstName());
                    employeeListDTO.setLastName(employee.getLastName());
                    employeeListDTO.setPatronymic(employee.getPatronymic());
                    employeeListDTO.setLogin(employee.getLogin());
                    employeeListDTO.setStatus(employee.getStatus());
                    employeeListDTO.setRole(employee.getRole());
                    if (employee.getBranchId() != null) {
                        employeeListDTO.setBranches(List.of(employee.getBranch().toCommonDTO()));
                    }
                    if (employee.getWarehouseId() != null) {
                        employeeListDTO.setWarehouse(employee.getWarehouse().toCommonDTO());
                    }
                    return employeeListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<EmployeeListMobileDTO> getListForMobile(UserFilter filter) {
        ResultList<User> resultList = userRepository.getResultList(filter);
        List<EmployeeListMobileDTO> result = resultList
                .getList()
                .stream()
                .map(employee -> {
                    EmployeeListMobileDTO employeeListDTO = new EmployeeListMobileDTO();
                    employeeListDTO.setId(employee.getId());
                    employeeListDTO.setFirstName(employee.getFirstName());
                    employeeListDTO.setLastName(employee.getLastName());
                    employeeListDTO.setPatronymic(employee.getPatronymic());
                    employeeListDTO.setLogin(employee.getLogin());
                    employeeListDTO.setStatus(employee.getStatus());
                    employeeListDTO.setRole(employee.getRole());

                    employeeListDTO.setBranches(employee.getBranches().stream().map(branch -> {
                        BranchCoordinateDTO branchCoordinateDTO = new BranchCoordinateDTO();
                        branchCoordinateDTO.setId(branch.getId());
                        branchCoordinateDTO.setName(branch.getName());
                        branchCoordinateDTO.setLatitude(Optional.ofNullable(branch.getAddress()).map(Address::getLatitude).orElse(null));
                        branchCoordinateDTO.setLongitude(Optional.ofNullable(branch.getAddress()).map(Address::getLongitude).orElse(null));
                        return branchCoordinateDTO;
                    }).toList());
                    if (employee.getWarehouseId() != null) {
                        employeeListDTO.setWarehouse(employee.getWarehouse().toCommonDTO());
                    }
                    return employeeListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<OwnerDTO> lookUp(UserFilter filter) {
        return userRepository.lookUp(filter).stream().map(userMapper::toOwnerDTO).toList();
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordDTO changePasswordDTO) {
        userRepository.findById(id).map(employee -> {
            String currentEncryptedPassword = employee.getPassword();
            if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), currentEncryptedPassword)) {
                throw validationService.badRequestExceptionThrow(
                        localizationService.localize(MessageConstants.ITEM_INVALID, new Object[]{localizationService.localize(MessageConstants.PASSWORD)}),
                        "Incorrect password").get();
            }
            employee.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
            userRepository.save(employee);
            cacheService.evictUser(employee.getLogin());
            return employee;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    public EmployeeDTO checkByTin(String tin) {
        CompanyCheckDTO companyCheckDTO = soliqClient.checkTin(tin);
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setTin(companyCheckDTO.getTin());
        employeeDTO.setPinfl(companyCheckDTO.getPersonalNum());
        if (StringUtils.isNotEmpty(companyCheckDTO.getName())) {
            String[] names = companyCheckDTO.getName().split(" ");
            if (names.length >= 1) {
                employeeDTO.setLastName(names[0]);
            }
            if (names.length >= 2) {
                employeeDTO.setFirstName(names[1]);
            }
            if (names.length >= 3) {
                employeeDTO.setPatronymic(names[2]);
            }
            if (names.length >= 4) {
                employeeDTO.setPatronymic(employeeDTO.getPatronymic() + " " + names[3]);
            }
        }
        return employeeDTO;
    }

    public CommonStatisticsDTO getStatistics(UserFilter filter) {
        return userRepository.getStatistics(filter);
    }

    @Transactional
    public void assignSupervisor(Long supervisorId, List<Long> managerIds) {
        userRepository.assignSupervisor(supervisorId, managerIds);
    }

    public List<CommonDTO> getExpeditors(UserFilter filter) {
        filter.setRole(Role.ROLE_EXPEDITOR.getCode());
        return userRepository.lookUp(filter).stream().map(User::toCommonDTO).toList();
    }

    public List<EmployeeCountByRoleDTO> getCountByRole(String tin, Long companyId) {
        return userRepository.getCountByRole(tin, companyId);
    }

    public List<BillingCompanyItem> getCompanyItems(String tin, Long companyId) {
        return userRepository.getCompanyItems(tin, companyId);
    }

    public List<MarketplaceEmployeeListDTO> getForMarketplace(Long companyId) {
        return userRepository.findAllByCompanyIdAndDeletedIsFalse(companyId)
                .stream()
                .map(user -> {
                    MarketplaceEmployeeListDTO marketplaceEmployeeListDTO = new MarketplaceEmployeeListDTO();
                    marketplaceEmployeeListDTO.setId(user.getId());
                    marketplaceEmployeeListDTO.setPhone(user.getPhone());
                    marketplaceEmployeeListDTO.setRole(user.getRole());
                    marketplaceEmployeeListDTO.setStatus(user.getStatus());
                    marketplaceEmployeeListDTO.setName(new FullNameDTO(user.getName(), user.getLastName(), user.getPatronymic()));
                    return marketplaceEmployeeListDTO;
                }).toList();
    }
}
