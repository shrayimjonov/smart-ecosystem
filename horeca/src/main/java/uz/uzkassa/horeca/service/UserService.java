package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.constants.CacheConstants;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.employee.OwnerDTO;
import uz.uzkassa.horeca.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.horeca.dto.sms.SmsRequestDTO;
import uz.uzkassa.horeca.dto.user.UserDTO;
import uz.uzkassa.horeca.dto.user.admin.UserDetailAdminDTO;
import uz.uzkassa.horeca.dto.user.admin.UserListAdminDTO;
import uz.uzkassa.horeca.dto.user.cabinet.UserDetailCabinetDTO;
import uz.uzkassa.horeca.dto.user.cabinet.UserListCabinetDTO;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.enums.UserStatus;
import uz.uzkassa.horeca.filters.UserFilter;
import uz.uzkassa.horeca.rabbitmq.producer.RabbitMqPermissionProducer;
import uz.uzkassa.horeca.repository.RolePermissionRepository;
import uz.uzkassa.horeca.utils.SecurityUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class UserService extends BaseService {

    PasswordEncoder passwordEncoder;
    RolePermissionRepository rolePermissionRepository;
    PermissionService permissionService;
    RabbitMqPermissionProducer rabbitMqPermissionProducer;
    SmsService smsService;

    @Transactional
    public Long create(UserDTO userDTO) {
        User user = null;
        if (StringUtils.isNotEmpty(userDTO.getLogin())) {
            user = userRepository.findFirstByLoginAndDeletedIsFalse(userDTO.getLogin()).orElse(null);
        }
        if (StringUtils.isNotBlank(userDTO.getTin())) {
            user = userRepository.findByTinAndDeletedIsFalse(userDTO.getTin()).orElse(null);
        }
        if (StringUtils.isNotBlank(userDTO.getPinfl())) {
            user = userRepository.findByPinflAndDeletedIsFalse(userDTO.getPinfl()).orElse(null);
        }
        if (user == null) {
            user = new User();
        }
        user.setTin(userDTO.getTin());
        user.setPinfl(userDTO.getPinfl());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPatronymic(userDTO.getPatronymic());
        user.setLogin(userDTO.getLogin());
        user.setPhone(userDTO.getPhone());
        String password = null;
        if (user.getId()==null) {
            password = Optional.ofNullable(userDTO.getPassword()).orElse(SecurityUtils.generatePassword());
            user.setPassword(passwordEncoder.encode(password));
        }

        user.setRole(Role.getByCode(userDTO.getRole()));
        user.setBranchId(userDTO.getBranchId());
        user.setCompanyId(getCurrentCompanyId(userDTO.getCompanyId()));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        if (StringUtils.isNotEmpty(password)) {
            smsService.send(new SmsRequestDTO(Optional.ofNullable(userDTO.getLogin()).orElse(userDTO.getPhone()), password));
        }

        rabbitMqPermissionProducer.sendToQueue(
                new UserRolePermissionPayload(
                        user.getId(),
                        userDTO.getRole(),
                        rolePermissionRepository.findAllPermissionCodesByRole(Role.getByCode(userDTO.getRole()))
                )
        );
        this.clearCache(user);
        return user.getId();
    }

    @Transactional
    public Long update(Long id, UserDTO userDTO) {
        return userRepository.findById(id).map(user -> {
            if (StringUtils.isNotEmpty(userDTO.getLogin()) && !userDTO.getLogin().equals(user.getLogin())) {
                if (userRepository.existsUserByLoginAndDeletedIsFalseAndIdNot(userDTO.getLogin(), user.getId())) {
                    throw conflictExceptionThrow(
                            localizationService.localize(
                                    "entity.already.exist.by.param",
                                    new Object[]{
                                            localizationService.localize(User.class.getSimpleName()),
                                            localizationService.localize("login"),
                                            userDTO.getLogin(),
                                    }
                            )
                    )
                            .get();
                }
                user.setLogin(userDTO.getLogin());
            }
            if (StringUtils.isNotEmpty(userDTO.getTin()) && !userDTO.getTin().equals(user.getTin())) {
                if (userRepository.existsUserByTinAndDeletedIsFalseAndIdNot(userDTO.getTin(), user.getId())) {
                    throw conflictExceptionThrow(
                            localizationService.localize(
                                    "entity.already.exist.by.param",
                                    new Object[]{
                                            localizationService.localize(User.class.getSimpleName()),
                                            localizationService.localize("inn"),
                                            userDTO.getTin(),
                                    }
                            )
                    )
                            .get();
                }
                user.setTin(userDTO.getTin());
            }
            if (StringUtils.isNotEmpty(userDTO.getPinfl()) && !userDTO.getPinfl().equals(user.getPinfl())) {
                if (userRepository.existsUserByPinflAndDeletedIsFalseAndIdNot(userDTO.getPinfl(), user.getId())) {
                    throw conflictExceptionThrow(
                            localizationService.localize(
                                    "entity.already.exist.by.param",
                                    new Object[]{
                                            localizationService.localize(User.class.getSimpleName()),
                                            localizationService.localize("pinfl"),
                                            userDTO.getPinfl(),
                                    }
                            )
                    )
                            .get();
                }
                user.setPinfl(userDTO.getPinfl());
            }
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setPatronymic(userDTO.getPatronymic());

            user.setBranchId(userDTO.getBranchId());

            if (StringUtils.isNotBlank(userDTO.getRole()) && !user.getRole().equals(Role.getByCode(userDTO.getRole()))) {
                permissionService.deleteUserPermissionsByUserId(user.getId());
                rabbitMqPermissionProducer.sendToQueue(
                        new UserRolePermissionPayload(
                                user.getId(),
                                userDTO.getRole(),
                                rolePermissionRepository.findAllPermissionCodesByRole(Role.getByCode(userDTO.getRole()))
                        )
                );
            }
            if (userDTO.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
            userRepository.save(user);
            clearCache(user);
            return user.getId();
        }).orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    public UserDetailCabinetDTO getForCabinet(Long id) {
        return userRepository.findById(id).map(user -> {
            UserDetailCabinetDTO userDetailDTO = new UserDetailCabinetDTO();
            userDetailDTO.setId(id);
            userDetailDTO.setFirstName(user.getFirstName());
            userDetailDTO.setLastName(user.getLastName());
            userDetailDTO.setPatronymic(user.getPatronymic());
            userDetailDTO.setTin(user.getTin());
            userDetailDTO.setPinfl(user.getPinfl());
            userDetailDTO.setLogin(user.getLogin());
            userDetailDTO.setStatus(user.getStatus());
            userDetailDTO.setBranch(user.getBranch().toCommonDTO());
            userDetailDTO.setRole(user.getRole());
            return userDetailDTO;
        }).orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    public UserDetailAdminDTO getForAdmin(Long id) {
        return userRepository.findById(id).map(user -> {
            UserDetailAdminDTO userDetailDTO = new UserDetailAdminDTO();
            userDetailDTO.setId(id);
            userDetailDTO.setFirstName(user.getFirstName());
            userDetailDTO.setLastName(user.getLastName());
            userDetailDTO.setPatronymic(user.getPatronymic());
            userDetailDTO.setTin(user.getTin());
            userDetailDTO.setPinfl(user.getPinfl());
            userDetailDTO.setLogin(user.getLogin());
            userDetailDTO.setStatus(user.getStatus());
            userDetailDTO.setRole(user.getRole());
            if (user.getBranchId() != null) {
                userDetailDTO.setBranch(user.getBranch().toCommonDTO());
            }
            userDetailDTO.setCompany(user.getCompany().toCommonDTO());
            return userDetailDTO;
        }).orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    public Page<UserListCabinetDTO> getListForCabinet(UserFilter filter) {
        ResultList<User> resultList = userRepository.getResultList(filter);
        List<UserListCabinetDTO> result = resultList
                .getList()
                .stream()
                .map(user -> {
                    UserListCabinetDTO userListDTO = new UserListCabinetDTO();
                    userListDTO.setId(user.getId());
                    userListDTO.setTin(user.getTin());
                    userListDTO.setPinfl(user.getPinfl());
                    userListDTO.setFirstName(user.getFirstName());
                    userListDTO.setLastName(user.getLastName());
                    userListDTO.setPatronymic(user.getPatronymic());
                    userListDTO.setLogin(user.getLogin());
                    userListDTO.setStatus(user.getStatus());
                    userListDTO.setRole(user.getRole());
                    userListDTO.setBranch(user.getBranch().toCommonDTO());
                    return userListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<UserListAdminDTO> getListForAdmin(UserFilter filter) {
        ResultList<User> resultList = userRepository.getResultList(filter);
        List<UserListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(user -> {
                    UserListAdminDTO userListDTO = new UserListAdminDTO();
                    userListDTO.setId(user.getId());
                    userListDTO.setTin(user.getTin());
                    userListDTO.setPinfl(user.getPinfl());
                    userListDTO.setFirstName(user.getFirstName());
                    userListDTO.setLastName(user.getLastName());
                    userListDTO.setPatronymic(user.getPatronymic());
                    userListDTO.setLogin(user.getLogin());
                    userListDTO.setStatus(user.getStatus());
                    userListDTO.setRole(user.getRole());
                    if (user.getBranchId() != null) {
                        userListDTO.setBranch(user.getBranch().toCommonDTO());
                    }
                    userListDTO.setCompany(user.getCompany().toCommonDTO());
                    return userListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<OwnerDTO> lookUp(UserFilter filter) {
        return userRepository.getResultList(filter).getList().stream().map(user -> {
            OwnerDTO ownerDTO = new OwnerDTO();
            ownerDTO.setId(user.getId());
            ownerDTO.setFirstName(user.getFirstName());
            ownerDTO.setLastName(user.getLastName());
            ownerDTO.setPatronymic(user.getPatronymic());
            ownerDTO.setLogin(user.getLogin());
            ownerDTO.setPhone(user.getPhone());
            ownerDTO.setPinfl(user.getPinfl());
            ownerDTO.setTin(user.getTin());
            return ownerDTO;
        }).collect(Collectors.toList());
    }

    public OwnerDTO getOwner() {
        Company company = getCurrentCompany();
        OwnerDTO ownerDTO = new OwnerDTO();
        if (company.getOwner() == null) {
            return ownerDTO;
        }
        ownerDTO.setId(company.getOwnerId());
        ownerDTO.setFirstName(company.getOwner().getFirstName());
        ownerDTO.setLastName(company.getOwner().getLastName());
        ownerDTO.setPatronymic(company.getOwner().getPatronymic());
        ownerDTO.setPhone(company.getOwner().getPhone());
        return ownerDTO;
    }

    @Transactional
    public Long updateOwner(Long id, OwnerDTO ownerDT0) {
        Company company = getCurrentCompany();
        User owner = userRepository.findById(id).orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));

        if (!owner.getId().equals(company.getOwnerId())) {
            throw badRequestExceptionThrow(localizationService.localize("owner.does.not.match", new Object[]{owner.getName()})).get();
        }
        owner.setFirstName(ownerDT0.getFirstName());
        owner.setLastName(ownerDT0.getLastName());
        owner.setPatronymic(ownerDT0.getPatronymic());
        owner = userRepository.save(owner);
        clearCache(owner);
        return owner.getId();
    }

    private void clearCache(User user) {
        cacheService.evictCache(CacheConstants.USERS_BY_LOGIN_CACHE, user.getLogin());
    }

    @Transactional
    public void updateStatus(Long id, UserStatus status) {
        userRepository
                .findById(id)
                .map(user -> {
                    user.setStatus(status);
                    userRepository.save(user);
                    clearCache(user);
                    return user;
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }
}
