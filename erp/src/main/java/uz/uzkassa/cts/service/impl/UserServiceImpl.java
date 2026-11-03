package uz.uzkassa.cts.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.permission.Authority;
import uz.uzkassa.cts.dto.auth.ActivationFinishDTO;
import uz.uzkassa.cts.dto.auth.ChangePasswordDTO;
import uz.uzkassa.cts.dto.auth.ChangeUsernameConfirmDTO;
import uz.uzkassa.cts.dto.postfix.PostfixMailboxEmailDTO;
import uz.uzkassa.cts.dto.user.UserDTO;
import uz.uzkassa.cts.dto.user.admin.CurrentUserAdminDTO;
import uz.uzkassa.cts.dto.user.admin.UserDetailAdminDTO;
import uz.uzkassa.cts.dto.user.cabinet.CurrentUserCabinetDTO;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.enums.UserStatus;
import uz.uzkassa.cts.repository.AuthorityRepository;
import uz.uzkassa.cts.repository.UserRepository;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.service.PostfixService;
import uz.uzkassa.cts.service.UserService;
import uz.uzkassa.cts.utils.RandomUtils;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostfixService postfixService;
    private final CacheService cacheService;
    private final ValidationContext validationContext;

    public UserServiceImpl(UserRepository userRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder, PostfixService postfixService, CacheService cacheService, ValidationContext validationContext) {
        super(userRepository);
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.postfixService = postfixService;
        this.cacheService = cacheService;
        this.validationContext = validationContext;
    }

    @Transactional
    @Override
    public Long create(UserDTO userDTO) {
        validationContext.validate(userDTO);
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPatronymic(userDTO.getPatronymic());
        user.setLogin(userDTO.getUsername());
        user.setPhone(userDTO.getPhone());
        user.setAuthorities(Set.of((Authority) authorityRepository.findAllById(userDTO.getRoles().stream().map(Role::getRoleCode).collect(Collectors.toSet()))));
        user.setPassword(passwordEncoder.encode(Optional.ofNullable(userDTO.getPassword()).orElseGet(RandomUtils::generatePassword)));
        userRepository.save(user);
        return user.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, UserDTO userDTO) {
        userDTO.setId(id);
        validationContext.validate(userDTO);
        return userRepository.findById(id).map(user -> {
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setPatronymic(userDTO.getPatronymic());
            user.setPhone(userDTO.getPhone());
            user.getAuthorities().clear();
            user.setAuthorities(Set.of((Authority) authorityRepository.findAllById(userDTO.getRoles().stream().map(Role::getRoleCode).collect(Collectors.toSet()))));
            return id;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
    }

    @Override
    public UserDetailAdminDTO getForAdmin(Long id) {
        return userRepository.findById(id).map(user -> {
            UserDetailAdminDTO userDetailDTO = new UserDetailAdminDTO();
            userDetailDTO.setId(user.getId());
            userDetailDTO.setFirstName(user.getFirstName());
            userDetailDTO.setLastName(user.getLastName());
            userDetailDTO.setPatronymic(user.getPatronymic());
            userDetailDTO.setUsername(user.getLogin());
            userDetailDTO.setPhone(user.getPhone());
            userDetailDTO.setRoles(user.getAuthorities().stream().map(authority -> Role.getRoleCode(authority.getName())).collect(Collectors.toList()));
            return userDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
    }

    @Override
    public CurrentUserCabinetDTO getCurrentUserForCabinet() {
        User user = getCurrentUserDomain();
        CurrentUserCabinetDTO currentUserDTO = new CurrentUserCabinetDTO();
        currentUserDTO.setId(user.getId());
        currentUserDTO.setFirstName(user.getFirstName());
        currentUserDTO.setLastName(user.getLastName());
        if (user.getCompanyId() != null) {
            currentUserDTO.setCompany(user.getCompany().toCommonDTO());
        }
        if (user.getBranchId() != null) {
            currentUserDTO.setBranch(user.getBranch().toCommonDTO());
        }
        user.getAuthorities().forEach(role -> currentUserDTO.addRole(Role.getRoleCode(role.getName())));
        return currentUserDTO;
    }

    @Override
    public CurrentUserAdminDTO getCurrentUserForAdmin() {
        User user = getCurrentUserDomain();
        CurrentUserAdminDTO currentUserDTO = new CurrentUserAdminDTO();
        currentUserDTO.setId(user.getId());
        currentUserDTO.setFirstName(user.getFirstName());
        currentUserDTO.setLastName(user.getLastName());
        if (user.getCompanyId() != null) {
            currentUserDTO.setCompany(user.getCompany().toCommonDTO());
        }
        if (user.getBranchId() != null) {
            currentUserDTO.setBranch(user.getBranch().toCommonDTO());
        }
        user.getAuthorities().forEach(role -> {
            currentUserDTO.addRole(Role.getRoleCode(role.getName()));
            if (Role.SUPER_ADMIN.getRoleCode().equals(role.getName())) {
                currentUserDTO.setSuperAdmin(true);
            }
        });
        return currentUserDTO;
    }

    @Override
    @Transactional
    public void activate(ActivationFinishDTO activationFinishDTO) {
        userRepository.findOneWithAuthoritiesByLoginAndDeletedIsFalse(activationFinishDTO.getUsername()).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(activationFinishDTO.getPassword()));
            user.setStatus(UserStatus.ACTIVE);
            userRepository.save(user);
        });
    }

    @Override
    @Transactional
    public void changePassword(User user, ChangePasswordDTO changePasswordDTO) {
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getPassword()));
        userRepository.save(user);
        cacheService.evict(CacheConstants.USER_BY_LOGIN_CACHE, user.getLogin());
    }

    @Override
    @Transactional
    public void changeUsername(User user, ChangeUsernameConfirmDTO changeUsernameConfirmDTO) {
        PostfixMailboxEmailDTO postfixMailboxEmailDTO = new PostfixMailboxEmailDTO();
        postfixMailboxEmailDTO.setOldEmail(user.getLogin());
        postfixMailboxEmailDTO.setEmail(changeUsernameConfirmDTO.getUsername());
        //postfixService.updateMailBoxUsername(postfixMailboxEmailDTO);//TODO POSTFIX
        userRepository.save(user);
        cacheService.evict(CacheConstants.USER_BY_LOGIN_CACHE, user.getLogin());
        cacheService.evict(CacheConstants.USER_BY_LOGIN_CACHE, changeUsernameConfirmDTO.getUsername());
    }
}
