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
import uz.uzkassa.horeca.domain.redis.AccountBlockList;
import uz.uzkassa.horeca.dto.auth.PhoneDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.employee.EmployeeChangePasswordDTO;
import uz.uzkassa.horeca.dto.employee.EmployeeDTO;
import uz.uzkassa.horeca.dto.employee.admin.EmployeeDetailAdminDTO;
import uz.uzkassa.horeca.dto.employee.admin.EmployeeListAdminDTO;
import uz.uzkassa.horeca.dto.employee.cabinet.EmployeeDetailCabinetDTO;
import uz.uzkassa.horeca.dto.employee.cabinet.EmployeeListCabinetDTO;
import uz.uzkassa.horeca.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.enums.UserStatus;
import uz.uzkassa.horeca.filters.UserFilter;
import uz.uzkassa.horeca.rabbitmq.producer.RabbitMqPermissionProducer;
import uz.uzkassa.horeca.repository.RolePermissionRepository;
import uz.uzkassa.horeca.repository.redis.AccountBlockListRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class EmployeeService extends BaseService implements CacheConstants {

    PasswordEncoder passwordEncoder;
    AccountBlockListRepository accountBlockListRepository;
    RabbitMqPermissionProducer rabbitMqPermissionProducer;
    RolePermissionRepository rolePermissionRepository;
    PermissionService permissionService;

    protected void validate(EmployeeDTO userDTO) {
        Long companyId = getCurrentCompanyId(userDTO.getCompanyId());
        if (userDTO.getId() == null && StringUtils.isEmpty(userDTO.getLogin())) {
            throw badRequestExceptionThrow("Login is required").get();
        }
        if (userDTO.getId() == null && StringUtils.isEmpty(userDTO.getPassword())) {
            throw badRequestExceptionThrow("Password is required").get();
        }
        if (StringUtils.isEmpty(userDTO.getRole())) {
            throw badRequestExceptionThrow("Role is required").get();
        }
        Role role = Role.getByCode(userDTO.getRole());
        if (role == null) {
            throw badRequestExceptionThrow("Role is invalid").get();
        }
        if (
                userDTO.getId() == null &&
                        Role.ROLE_DIRECTOR.equals(role) &&
                        userRepository.existsUserByCompanyIdAndRolesInAndDeletedIsFalse(companyId, role)
        ) {
            throw conflictExceptionThrow("Director already exists").get();
        }
        if (
                userDTO.getId() != null &&
                        Role.ROLE_DIRECTOR.equals(role) &&
                        userRepository.existsUserByCompanyIdAndRolesInAndAndIdNotDeletedIsFalse(companyId, role, userDTO.getId())
        ) {
            throw conflictExceptionThrow("Director already exists").get();
        }
        if (StringUtils.isEmpty(userDTO.getFirstName())) {
            throw badRequestExceptionThrow("Firstname is required").get();
        }
        if (StringUtils.isEmpty(userDTO.getLastName())) {
            throw badRequestExceptionThrow("Lastname is required").get();
        }
        if (userDTO.getId() == null && userRepository.existsUserByLoginAndDeletedIsFalse(userDTO.getLogin())) {
            throw conflictExceptionThrow("User exists by this login: " + userDTO.getLogin()).get();
        }
        if (userDTO.getId() != null && userRepository.existsUserByLoginAndDeletedIsFalseAndIdNot(userDTO.getLogin(), userDTO.getId())) {
            throw conflictExceptionThrow("User exists by this login: " + userDTO.getLogin()).get();
        }
    }

    @Transactional
    public Long create(EmployeeDTO userDTO) {
        this.validate(userDTO);

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPatronymic(userDTO.getPatronymic());
        user.setCompanyId(getCurrentCompanyId(userDTO.getCompanyId()));
        user.setBranchId(userDTO.getBranchId());
        user.setDepartmentId(userDTO.getDepartmentId());
        user.setStatus(UserStatus.ACTIVE);
        user.setPhone(userDTO.getLogin());
        user.setLogin(userDTO.getLogin());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(Role.getByCode(userDTO.getRole()));
        user.setBirthDate(userDTO.getBirthDate());
        user.setJoiningDate(userDTO.getJoiningDate());
        userRepository.save(user);
        cacheService.evictCache(USERS_BY_LOGIN_CACHE, user.getLogin());
        rabbitMqPermissionProducer.sendToQueue(
                new UserRolePermissionPayload(
                        user.getId(),
                        userDTO.getRole(),
                        rolePermissionRepository.findAllPermissionCodesByRole(Role.getByCode(userDTO.getRole()))
                )
        );
        return user.getId();
    }

    @Transactional
    public Long update(Long id, EmployeeDTO userDTO) {
        userDTO.setId(id);
        this.validate(userDTO);
        return userRepository
                .findById(id)
                .map(user -> {
                    user.setFirstName(userDTO.getFirstName());
                    user.setLastName(userDTO.getLastName());
                    user.setPatronymic(userDTO.getPatronymic());
                    user.setBranchId(userDTO.getBranchId());
                    user.setDepartmentId(userDTO.getDepartmentId());
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
                    user.setRole(Role.getByCode(userDTO.getRole()));
                    user.setBirthDate(userDTO.getBirthDate());
                    user.setJoiningDate(userDTO.getJoiningDate());
                    userRepository.save(user);
                    cacheService.evictCache(USERS_BY_LOGIN_CACHE, user.getLogin());
                    return user.getId();
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    public EmployeeDetailAdminDTO getForAdmin(Long id) {
        return userRepository
                .findById(id)
                .map(user -> {
                    EmployeeDetailAdminDTO employeeDetailDTO = new EmployeeDetailAdminDTO();
                    employeeDetailDTO.setId(user.getId());
                    employeeDetailDTO.setFirstName(user.getFirstName());
                    employeeDetailDTO.setLastName(user.getLastName());
                    employeeDetailDTO.setPatronymic(user.getPatronymic());
                    employeeDetailDTO.setPhone(user.getPhone());
                    if (user.getDepartmentId() != null) {
                        employeeDetailDTO.setDepartment(user.getDepartment().toCommonDTO());
                    }
                    if (user.getBranchId() != null) {
                        employeeDetailDTO.setBranch(user.getBranch().toCommonDTO());
                    }
                    employeeDetailDTO.setBirthDate(user.getBirthDate());
                    employeeDetailDTO.setJoiningDate(user.getJoiningDate());
                    employeeDetailDTO.setRole(user.getRole());
                    if (user.getPhotoId() != null) {
                        employeeDetailDTO.setPhoto(user.getPhoto().toFileDTO());
                    }
                    return employeeDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    public EmployeeDetailCabinetDTO getForCabinet(Long id) {
        return userRepository
                .findById(id)
                .map(user -> {
                    EmployeeDetailCabinetDTO userDetailDTO = new EmployeeDetailCabinetDTO();
                    userDetailDTO.setId(user.getId());
                    userDetailDTO.setFirstName(user.getFirstName());
                    userDetailDTO.setLastName(user.getLastName());
                    userDetailDTO.setPatronymic(user.getPatronymic());
                    userDetailDTO.setPhone(user.getPhone());
                    if (user.getDepartmentId() != null) {
                        userDetailDTO.setDepartment(user.getDepartment().toCommonDTO());
                    }
                    if (user.getBranchId() != null) {
                        userDetailDTO.setBranch(user.getBranch().toCommonDTO());
                    }
                    userDetailDTO.setRole(user.getRole());
                    userDetailDTO.setBirthDate(user.getBirthDate());
                    userDetailDTO.setJoiningDate(user.getJoiningDate());
                    if (user.getPhotoId() != null) {
                        userDetailDTO.setPhoto(user.getPhoto().toFileDTO());
                    }
                    userDetailDTO.setBirthDate(user.getBirthDate());
                    userDetailDTO.setJoiningDate(user.getJoiningDate());
                    return userDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    public Page<EmployeeListAdminDTO> getListForAdmin(UserFilter filter) {
        if (filter.getCompanyId() == null && StringUtils.isEmpty(filter.getRole())) {
            filter.setRoles(Role.getAllowableAdminRoles());
        }
        ResultList<User> resultList = userRepository.getResultList(filter);
        List<EmployeeListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(user -> {
                    EmployeeListAdminDTO employeeListDTO = new EmployeeListAdminDTO();
                    employeeListDTO.setId(user.getId());
                    employeeListDTO.setFirstName(user.getFirstName());
                    employeeListDTO.setLastName(user.getLastName());
                    employeeListDTO.setPatronymic(user.getPatronymic());
                    employeeListDTO.setPhone(user.getPhone());
                    if (user.getDepartmentId() != null) {
                        employeeListDTO.setDepartment(user.getDepartment().toCommonDTO());
                    }
                    if (user.getBranchId() != null) {
                        employeeListDTO.setBranch(user.getBranch().toCommonDTO());
                    }
                    employeeListDTO.setBirthDate(user.getBirthDate());
                    employeeListDTO.setJoiningDate(user.getJoiningDate());
                    employeeListDTO.setRole(user.getRole());
                    employeeListDTO.setStatus(user.getStatus());
                    return employeeListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public Page<EmployeeListCabinetDTO> getListForCabinet(UserFilter filter) {
        if (StringUtils.isEmpty(filter.getRole())) {
            filter.setRoles(Role.getCabinetRoles());
        }
        ResultList<User> resultList = userRepository.getResultList(filter);
        List<EmployeeListCabinetDTO> result = resultList
                .getList()
                .stream()
                .map(user -> {
                    EmployeeListCabinetDTO userListDTO = new EmployeeListCabinetDTO();
                    userListDTO.setId(user.getId());
                    userListDTO.setFirstName(user.getFirstName());
                    userListDTO.setLastName(user.getLastName());
                    userListDTO.setPatronymic(user.getPatronymic());
                    userListDTO.setPhone(user.getPhone());
                    if (user.getDepartmentId() != null) {
                        userListDTO.setDepartment(user.getDepartment().toCommonDTO());
                    }
                    if (user.getBranchId() != null) {
                        userListDTO.setBranch(user.getBranch().toCommonDTO());
                    }
                    userListDTO.setBirthDate(user.getBirthDate());
                    userListDTO.setJoiningDate(user.getJoiningDate());
                    userListDTO.setRole(user.getRole());
                    userListDTO.setStatus(user.getStatus());
                    return userListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public void delete(Long id) {
        userRepository.findById(id).map(user -> {
            user.setStatus(UserStatus.IN_ACTIVE);
            userRepository.deleteById(id);
            cacheService.evictCache(USERS_BY_LOGIN_CACHE, user.getLogin());
            AccountBlockList accountBlockList = accountBlockListRepository.findById(id).orElse(new AccountBlockList(id));
            accountBlockList.setTtl(getTokenTTL());
            accountBlockListRepository.save(accountBlockList);
            return user;
        }).orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));

    }

    public List<CommonDTO> lookUp(UserFilter filter) {
        return userRepository.getResultList(filter).getList().stream().map(User::toCommonDTO).collect(Collectors.toList());
    }

    public void updateStatus(Long id, UserStatus status) {
        userRepository
                .findById(id)
                .map(user -> {
                    if (status.equals(user.getStatus())) {
                        throw badRequestExceptionThrow("User already " + status.getName()).get();
                    }
                    user.setStatus(status);
                    userRepository.save(user);
                    cacheService.evictCache(USERS_BY_LOGIN_CACHE, user.getLogin());

                    if (UserStatus.IN_ACTIVE.equals(status)) {
                        AccountBlockList accountBlockList = accountBlockListRepository.findById(id).orElse(new AccountBlockList(id));
                        accountBlockList.setTtl(getTokenTTL());
                    } else if (UserStatus.ACTIVE.equals(status)) {
                        accountBlockListRepository.findById(id).ifPresent(accountBlockListRepository::delete);
                    }
                    return user.getId();
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void changePassword(Long id, EmployeeChangePasswordDTO userChangePasswordDTO) {
        if (!Objects.equals(userChangePasswordDTO.getPassword(), userChangePasswordDTO.getPasswordConfirmation())) {
            throw badRequestExceptionThrow("Password and confirm password did not match each other").get();
        }
        userRepository
                .findById(id)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(userChangePasswordDTO.getPassword()));
                    userRepository.save(user);
                    cacheService.evictCache(USERS_BY_LOGIN_CACHE, user.getLogin());
                    return user.getId();
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void changeLogin(Long id, PhoneDTO phoneDTO) {
        if (userRepository.existsUserByLoginAndDeletedIsFalse(phoneDTO.getPhone())) {
            throw conflictExceptionThrow("User exists by this login: " + phoneDTO.getPhone()).get();
        }
        userRepository
                .findById(id)
                .map(user -> {
                    String oldLogin = user.getLogin();
                    user.setLogin(phoneDTO.getPhone());
                    user.setPhone(phoneDTO.getPhone());
                    userRepository.save(user);
                    cacheService.evictCache(USERS_BY_LOGIN_CACHE, oldLogin);
                    cacheService.evictCache(USERS_BY_LOGIN_CACHE, user.getLogin());
                    return user.getId();
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }
}
