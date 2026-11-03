package uz.uzkassa.smartpos.service;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device;
import uz.uzkassa.smartpos.domain.postgres.terminal.Device_;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.CommonUserDTO;
import uz.uzkassa.smartpos.dto.FullNameDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.company.DevicesByTypeDto;
import uz.uzkassa.smartpos.dto.device.DeviceDto;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.filter.DeviceFilter;
import uz.uzkassa.smartpos.integration.cto.CtoBranchDTO;
import uz.uzkassa.smartpos.integration.cto.CtoCompanyDTO;
import uz.uzkassa.smartpos.integration.cto.CtoResultDTO;
import uz.uzkassa.smartpos.mappers.DeviceMapper;
import uz.uzkassa.smartpos.repository.postgres.common.ActivityTypeRepository;
import uz.uzkassa.smartpos.repository.postgres.common.CityRepository;
import uz.uzkassa.smartpos.repository.postgres.common.RegionRepository;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.repository.postgres.user.AuthorityRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.social.TelegramBotApi;
import uz.uzkassa.smartpos.utils.RandomUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uz.uzkassa.smartpos.constants.Constants.COMMON_SMS;

@Service
@RequiredArgsConstructor
public class DeviceService extends SmartposService {
    private final DeviceRepository deviceRepository;
    private final RegionRepository regionRepository;
    private final CityRepository cityRepository;
    private final ActivityTypeRepository activityTypeRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final EtcSmsService etcSmsService;
    private final TelegramBotApi telegramBotApi;
    private final DeviceMapper deviceMapper;

    public Page<DeviceDto> findAllByFilter(DeviceFilter filter) {
        if (!isOperator() && !isAdmin()) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIds()));
        return deviceRepository.findAll((Specification<Device>) (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (filter.getCompanyId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Device_.companyId), filter.getCompanyId()));
            }
            if (filter.getBranchId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Device_.branchId), filter.getBranchId()));
            }
            if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
                predicates.add(root.get(Device_.branchId).in(filter.getBranchIds()));
            }
            if (filter.getTypes() != null) {
                predicates.add(root.get(Device_.type).in(filter.getTypes()));
            } else {
                predicates.add(criteriaBuilder.notEqual(root.get(Device_.type), DeviceType.FISCAL));
            }
            if (StringUtils.isNotBlank(filter.getSearch())) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(Device_.serialNumber)), filter.getSearchForQuery())
                ));
            }
            query.orderBy(criteriaBuilder.desc(root.get(Device_.id)));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, filter.getPageable()).map(deviceMapper::toDto);
    }

    public DevicesByTypeDto getCompanyDevices() {
        List<Device> devices = deviceRepository.findAllByCompanyId(getCurrentCompanyId());
        DevicesByTypeDto dto = new DevicesByTypeDto();
        Map<DeviceType, List<String>> deviceMap = devices.stream().collect(Collectors.groupingBy(Device::getType,
                Collectors.mapping(Device::getSerialNumber, Collectors.toList())));
        dto.setDevices(deviceMap);
        return dto;
    }

    public DevicesByTypeDto getBranchDevices() {
        List<Device> devices = deviceRepository.findAllByBranchId(getCurrentBranchId());
        DevicesByTypeDto dto = new DevicesByTypeDto();
        Map<DeviceType, List<String>> deviceMap = devices.stream().collect(Collectors.groupingBy(Device::getType,
                Collectors.mapping(Device::getSerialNumber, Collectors.toList())));
        dto.setDevices(deviceMap);
        return dto;
    }

    @Transactional
    public void removeDeviceFromBranch(String serialNumber) {
        List<Device> devices = deviceRepository.findAllBySerialNumber(serialNumber);
        deviceRepository.deleteAll(devices);
    }

    @Transactional
    public void syncDevice(@Valid CtoResultDTO ctoResultDTO) {
        if (ctoResultDTO.getCustomer() == null || ctoResultDTO.getCustomerBranch() == null || ctoResultDTO.getCustomer().getOwner() == null) {
            return;
        }
        CtoCompanyDTO customer = ctoResultDTO.getCustomer();
        Company company = createOrUpdateCompany(customer);

        CtoBranchDTO customerBranch = ctoResultDTO.getCustomerBranch();
        Long branchId = createOrUpdateBranch(customerBranch, company.getId());

        var managerDTO = ctoResultDTO.getCustomer().getManager();
        Long managerId = createOrUpdateManager(managerDTO);

        FullNameDTO ownerDto = ctoResultDTO.getCustomer().getOwner();
        Long ownerId = createOrUpdateOwner(ownerDto, company, branchId);

        company.setOwnerId(ownerId);
        company.setManagerId(managerId);
        companyRepository.save(company);

        createOrUpdateDevices(ctoResultDTO, branchId, company, ctoResultDTO.getDeviceType());
    }

    private void createOrUpdateDevices(CtoResultDTO ctoResultDTO, Long branchId, Company company, DeviceType deviceType) {
        Device device = deviceRepository.findFirstBySerialNumberAndType(ctoResultDTO.getDeviceSerial(), deviceType).orElseGet(Device::new);
        device.setSerialNumber(ctoResultDTO.getDeviceSerial());
        device.setBranchId(branchId);
        device.setCompanyId(company.getId());
        device.setType(ctoResultDTO.getDeviceType());
        deviceRepository.save(device);
    }

    private Long createOrUpdateOwner(FullNameDTO ownerDto, Company company, Long branchId) {
        User owner = userRepository.findOneByLogin(ownerDto.getPhone())
                .orElseGet(User::new);
        owner.setActivated(true);
        owner.setDeleted(false);
        owner.setOwner(true);
        owner.setLogin(ownerDto.getPhone());
        owner.setFirstName(ownerDto.getFirstName());
        owner.setLastName(ownerDto.getLastName());
        owner.setPatronymic(ownerDto.getPatronymic());
        if (owner.getId() == null) {
            final String key = RandomUtil.generateNumericKey();
            owner.setPassword(passwordEncoder.encode(key));
            etcSmsService.sendSms(new EtcRequestDto(owner.getLogin(), COMMON_SMS));
        } else if (!owner.isOwner()) {
            final String message = StringUtils.join("ВЛАДЕЛЕЦ ИЗМЕНЕНА \n <b>НОМЕР</b>=<code>", owner.getLogin(), "</code> \n <b>ОБНОВИЛ</b>=<code>", SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT), "</code>");
            telegramBotApi.sendMessageToAdminChannel(message);
        }
        owner.getAuthorities().clear();
        owner.getAuthorities().addAll(Set.of(authorityRepository.getReferenceById(AuthoritiesConstants.USER), authorityRepository.getReferenceById(AuthoritiesConstants.OWNER)));
        owner.setCompanyId(company.getId());
        owner.setBranchId(branchId);
        clearLoginCaches(ownerDto.getPhone());
        userRepository.save(owner);
        return owner.getId();
    }

    private Long createOrUpdateManager(CommonUserDTO managerDTO) {
        if (managerDTO != null) {
            User manager = userRepository.findOneByLogin(managerDTO.getLogin()).orElseGet(() -> {
                User user = new User();
                user.setLogin(managerDTO.getLogin());
                user.setPatronymic(managerDTO.getPatronymic());
                user.setFirstName(managerDTO.getFirstName());
                user.setLastName(managerDTO.getLastName());
                user.setBranchId(getMainBranchIdForAdminCompany());
                user.setCompanyId(getAdminCompanyId());
                user.setActivated(true);
                user.setDeleted(false);
                user.getAuthorities().addAll(Set.of(
                        authorityRepository.getReferenceById(AuthoritiesConstants.SALES_MANAGER),
                        authorityRepository.getReferenceById(AuthoritiesConstants.SUPPORT),
                        authorityRepository.getReferenceById(AuthoritiesConstants.SUPPORT_MANAGER))
                );
                final String s = RandomUtil.generateNumericKey();
                user.setPassword(passwordEncoder.encode(s));
                clearLoginCaches(managerDTO.getLogin());
                userRepository.save(user);
                return user;
            });
            return manager.getId();
        }
        return null;
    }

    private Long createOrUpdateBranch(CtoBranchDTO customerBranch, Long companyId) {
        Optional<Branch> optionalBranch = branchRepository.findFirstByCtoId(customerBranch.getId());
        if (optionalBranch.isEmpty() && customerBranch.getOfdId() != null) {
            optionalBranch = branchRepository.findFirstByOfdId(customerBranch.getOfdId());
        }
        if (optionalBranch.isEmpty()) {
            optionalBranch = branchRepository.findFirstByCompanyIdAndName(companyId, customerBranch.getName());
        }
        Branch branch = optionalBranch.orElseGet(Branch::new);
        branch.setDeleted(false);
        branch.setFiscal(true);
        branch.setCtoId(customerBranch.getId());
        if (customerBranch.getName() != null) {
            branch.setName(customerBranch.getName());
        }
        if (customerBranch.getActivityTypeCode() != null && customerBranch.getActivityTypeParentCode() != null) {
            branch.setActivityTypeId(activityTypeRepository.findOneByCodeAndParent(customerBranch.getActivityTypeCode(), customerBranch.getActivityTypeParentCode()).orElseThrow(notFound(localizationService.getMessage("sphere.activity.missing"))).getId());
        }
        if (customerBranch.getAddress() != null) {
            if (customerBranch.getAddress().getRegionCode() != null) {
                branch.setRegionId(regionRepository.findOneByCode(customerBranch.getAddress().getRegionCode())
                        .orElseThrow(notFound(localizationService.getMessage("region"))).getId());
                if (customerBranch.getAddress().getDistrictCode() != null) {
                    branch.setCityId(cityRepository.findOneByCodeAndRegionCode(customerBranch.getAddress().getDistrictCode(), customerBranch.getAddress().getRegionCode())
                            .orElseThrow(notFound(localizationService.getMessage("city"))).getId());
                }
            }
            branch.setAddress(customerBranch.getAddress().getAddressStr());
        }
        if (customerBranch.getLatitude() != null) {
            branch.setLatitude(customerBranch.getLatitude());
        }
        if (customerBranch.getLongitude() != null) {
            branch.setLongitude(customerBranch.getLongitude());
        }
        if (customerBranch.getOfdId() != null) {
            branch.setOfdId(customerBranch.getOfdId());
        }
        branch.setCompanyId(companyId);
        branchRepository.save(branch);
        return branch.getId();
    }

    private Company createOrUpdateCompany(CtoCompanyDTO customer) {
        List<String> tins = Stream.of(customer.getTin(), customer.getOldTin()).filter(StringUtils::isNotBlank).toList();
        var companies = companyRepository.findByCtoIdOrInnIn(customer.getId(), tins);
        if (CollectionUtils.size(companies) > 1) {
            throw new BadRequestException(localizationService.getMessage("company.more.than.one"));
        }
        Company company = companies.size() == 1 ? companies.getFirst() : new Company();
        company.setBillingType(customer.getAppType());
        company.setFiscal(true);
        company.setSyncDate(LocalDateTime.now());
        company.setCtoId(customer.getId());
        if (customer.getName() != null) {
            company.setName(customer.getName());
        }
        if (customer.getTin() != null) {
            company.setInn(customer.getTin());
        }
        if (customer.getOldTin() != null) {
            company.setOldInn(customer.getOldTin());
        }
        company.setNdsPercent(customer.getVat());
        if (customer.getAddress() != null) {
            if (customer.getAddress().getRegionCode() != null) {
                company.setRegionId(regionRepository.findOneByCode(customer.getAddress().getRegionCode()).orElseThrow(notFound(localizationService.getMessage("region"))).getId());
                if (customer.getAddress().getDistrictCode() != null) {
                    company.setCityId(cityRepository.findOneByCodeAndRegionCode(customer.getAddress().getDistrictCode(), customer.getAddress().getRegionCode())
                            .orElseThrow(notFound(localizationService.getMessage("city")))
                            .getId());
                }
                company.setAddress(customer.getAddress().getAddressStr());
            }
        }
        if (customer.getBusinessType() != null) {
            company.setBusinessType(customer.getBusinessType());
        }
        if (customer.getActivityTypeCode() != null && customer.getActivityTypeParentCode() != null) {
            activityTypeRepository.findOneByCodeAndParent(customer.getActivityTypeCode(), customer.getActivityTypeParentCode())
                    .ifPresent(activityType -> company.getActivityTypes().add(activityType));
        }
        companyRepository.save(company);
        return company;
    }

    public List<CommonDTO> lookup(DeviceFilter filter) {
        return deviceRepository.lookup(filter);
    }
}
