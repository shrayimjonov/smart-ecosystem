package uz.uzkassa.smartpos.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.domain.postgres.FileUpload;
import uz.uzkassa.smartpos.domain.postgres.company.Branch;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.domain.postgres.user.*;
import uz.uzkassa.smartpos.dto.*;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.base.CompanyBaseDTO;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.dto.filter.TelegramAccountFilter;
import uz.uzkassa.smartpos.dto.filter.UserFilter;
import uz.uzkassa.smartpos.dto.integration.erp.TerminalCashierErpCreateDto;
import uz.uzkassa.smartpos.dto.telegram.TelegramAccountDTO;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.enums.EntityType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.exceptions.UserNotFoundException;
import uz.uzkassa.smartpos.filter.specification.UserSpecificationFilter;
import uz.uzkassa.smartpos.integration.apay.FileDTO;
import uz.uzkassa.smartpos.mappers.CommonMapper;
import uz.uzkassa.smartpos.mappers.FileUploadMapper;
import uz.uzkassa.smartpos.repository.postgres.FileUploadRepository;
import uz.uzkassa.smartpos.repository.postgres.receipt.ShiftRepository;
import uz.uzkassa.smartpos.repository.postgres.specifications.UserSpecification;
import uz.uzkassa.smartpos.repository.postgres.terminal.DeviceRepository;
import uz.uzkassa.smartpos.repository.postgres.user.*;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.security.CustomUser;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.security.UserNotActivatedException;
import uz.uzkassa.smartpos.security.jwt.JWTFilter;
import uz.uzkassa.smartpos.security.jwt.TokenProvider;
import uz.uzkassa.smartpos.utils.RandomUtil;
import uz.uzkassa.smartpos.web.rest.vm.KeyAndPasswordVM;
import uz.uzkassa.smartpos.web.rest.vm.LoginVM;
import uz.uzkassa.smartpos.web.rest.vm.RegisterVM;
import uz.uzkassa.smartpos.web.rest.vm.TokenDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends SmartposService {
    private final CommonMapper commonMapper;
    private final FileUploadRepository fileUploadRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPermissionRepository userPermissionRepository;
    private final PermissionRepository permissionRepository;
    private final ShiftRepository shiftRepository;
    private final TelegramAccountService telegramAccountService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final ShiftReportService shiftReportService;
    private final FileUploadService fileUploadService;
    private final FileUploadMapper fileUploadMapper;
    private final UserRepository userRepository;
    private final TelegramAccountRepository telegramAccountRepository;
    private final EtcSmsService smsService;
    private final DeviceRepository deviceRepository;

    @Transactional
    public void saveUserPermissions(UserPermissionDTO userPermissionDTO) {
        User user = userRepository.findById(userPermissionDTO.getId()).orElseThrow(notFound(User.class.getSimpleName(), userPermissionDTO.getId()));
        if (CollectionUtils.isEmpty(userPermissionDTO.getPermissions())) {
            userPermissionRepository.updateUserPermission(false, user.getId());
            clearLoginCaches(user.getLogin());
            return;
        }

        HashMap<String, UserPermission> userPermissionMap = user.getUserPermissions().stream()
                .collect(Collectors.toMap(UserPermission::getPermissionCode, userPermission -> userPermission, (a, b) -> b, HashMap::new));
        Set<String> existingPermissions = new HashSet<>(permissionRepository.findAllPermissionCodes());
        List<Permission> newPermissions = new ArrayList<>();
        for (String permissionCode : userPermissionDTO.getPermissions()) {
            if (!existingPermissions.contains(permissionCode)) {
                Permission permission = new Permission();
                permission.setCode(permissionCode);
                newPermissions.add(permission);
            }
        }
        if (CollectionUtils.isNotEmpty(newPermissions)) {
            permissionRepository.saveAll(newPermissions);
        }

        for (String permissionCode : userPermissionDTO.getPermissions()) {
            UserPermission userPermission = userPermissionMap.getOrDefault(permissionCode, new UserPermission());
            userPermission.setUserId(user.getId());
            userPermission.setPermissionCode(permissionCode);
            userPermission.setHasAccess(true);
            userPermissionRepository.save(userPermission);
            userPermissionMap.remove(permissionCode, userPermission);
        }

        userPermissionMap.values().forEach(userPermission -> userPermission.setHasAccess(false));
        userPermissionRepository.saveAll(userPermissionMap.values());

        clearLoginCaches(user.getLogin());
    }

    public List<String> getUserPermissions(Long id) {
        return userPermissionRepository.findAllPermissionCodes(id);
    }

    public List<String> listPermissions() {
        return permissionRepository.findAllPermissionCodes();
    }

    public UserDTO getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin)
                .map(User::getAccountDTO)
                .orElseThrow(notFound(localizationService.getMessage("user")));
    }

    public void createAdminUser(UserDTO userDTO) {
        Arrays.asList(AuthoritiesConstants.ADMIN, AuthoritiesConstants.OWNER).forEach(userDTO.getAuthorities()::remove);
        Optional<User> oldUser = userRepository.findOneByLogin(userDTO.getLogin());
        if (oldUser.isPresent() && !oldUser.get().isDeleted()) {
            throw badRequest(localizationService.getMessage("login.already.used")).get();
        }
        User user = new User();
        oldUser.ifPresent(value -> {
            user.setId(value.getId());
            this.clearLoginCaches(value.getLogin());
        });
        user.setCompanyId(getCurrentCompanyId());
        userDTO2Entity(userDTO, user);
        user.setLogin(userDTO.getLogin());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setActivated(true);

        userRepository.save(user);
        this.clearLoginCaches(user.getLogin());
    }

    @Transactional
    public User createUser(UserDTO userDTO) {
        Arrays.asList(AuthoritiesConstants.SMARTPOS_ADMIN,
                AuthoritiesConstants.ADMIN,
                AuthoritiesConstants.OWNER,
                AuthoritiesConstants.SALES_MANAGER,
                AuthoritiesConstants.SUPPORT).forEach(userDTO.getAuthorities()::remove);
        Optional<User> oldUser = userRepository.findOneByLogin(userDTO.getLogin());
        if (oldUser.isPresent() && !oldUser.get().isDeleted()) {
            throw badRequest(localizationService.getMessage("login.already.used")).get();
        }
        User user = new User();
        oldUser.ifPresent(value -> {
            user.setId(value.getId());
            this.clearLoginCaches(value.getLogin());
        });
        if (userDTO.getCompanyId() == null) {
            user.setCompanyId(getCurrentCompanyId());
        } else {
            user.setCompanyId(userDTO.getCompanyId());
        }
        Company company = companyRepository.findById(user.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        if (isAdminCompany(company)) {
            throw new BadRequestException("Вы не можете создать пользователя для AdminCompany");
        }
        userDTO2Entity(userDTO, user);
        user.setLogin(userDTO.getLogin());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setActivationKey(userDTO.getPassword());
        user.setActivated(true);
        userRepository.save(user);
        this.clearLoginCaches(user.getLogin());
        return user;
    }

    @Transactional
    public UserDTO updateUser(UserDTO userDTO) throws EntityNotFound {
        Arrays.asList(AuthoritiesConstants.SMARTPOS_ADMIN,
                AuthoritiesConstants.ADMIN,
                AuthoritiesConstants.OWNER,
                AuthoritiesConstants.SALES_MANAGER,
                AuthoritiesConstants.SUPPORT).forEach(userDTO.getAuthorities()::remove);
        userRepository.findOneByLogin(userDTO.getLogin()).filter(user1 -> !user1.getId().equals(userDTO.getId())).ifPresent(user1 -> {
            throw badRequest(localizationService.getMessage("login.already.used")).get();
        });
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(notFound(localizationService.getMessage("user"), userDTO.getId()));
        if (user.getCompanyId() != null) {
            Company company = companyRepository.findById(user.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
            if (isAdminCompany(company)) {
                throw new BadRequestException("Вы не можете обновить пользователя в AdminCompany");
            }
        }
        if (user.isOwner()) {
            if (StringUtils.isNotEmpty(userDTO.getTelegramLogin())) {
                //todo Hayot.
                final Long companyId = user.getCompanyId();
                if (telegramAccountRepository.findAllByPhone(userDTO.getTelegramLogin()).stream().anyMatch(telegramAccount -> ((telegramAccount.getUserId() != null && !telegramAccount.getUserId().equals(userDTO.getId())) || (telegramAccount.getUserId() == null || telegramAccount.getCompanyId() != null && !telegramAccount.getCompanyId().equals(companyId))))) {
                    throw badRequest(localizationService.getMessage("login.telegram.already.used")).get();
                }
                TelegramAccountDTO telegramAccountDTO = TelegramAccountDTO.builder().userId(user.getId()).companyId(user.getCompanyId()).phone(userDTO.getTelegramLogin()).build();
                if (user.getBranchId() != null) {
                    var branchDto = new CommonDTO();
                    branchDto.setId(user.getBranchId());
                    telegramAccountDTO.addBranch(branchDto);
                }
                if (user.getId() != null) {
                    TelegramAccountFilter filter = new TelegramAccountFilter();
                    filter.setUserId(user.getId());
                    TelegramAccount telegramAccount = telegramAccountRepository.findAllByFilter(filter).getContent().stream().findFirst().orElse(null);
                    if (telegramAccount != null) {
                        telegramAccountDTO.setId(telegramAccount.getId());
                        if (!StringUtils.equals(telegramAccount.getPhone(), telegramAccountDTO.getPhone())) {
                            telegramAccountDTO.setTelegramId(null);
                            telegramAccountDTO.setName(null);
                        }
                    }
                }
                telegramAccountService.createIfNotExists(telegramAccountDTO);
            } else {
                if (CollectionUtils.isNotEmpty(user.getTelegramAccounts())) {
                    for (TelegramAccount telegramAccount : user.getTelegramAccounts()) {
                        telegramAccountService.delete(telegramAccount.getId());
                    }
                }
            }
        }
        userDTO2Entity(userDTO, user);
        if (!user.isOwner()) {
            this.clearLoginCaches(user.getLogin());
            user.setLogin(userDTO.getLogin());
        }
        user = userRepository.save(user);
        this.clearLoginCaches(user.getLogin());
        return user.getDTO();
    }

    public void updateUserAdmin(UserDTO userDTO) throws EntityNotFound {
        Arrays.asList(AuthoritiesConstants.ADMIN, AuthoritiesConstants.OWNER).forEach(userDTO.getAuthorities()::remove);
        userRepository.findOneByLogin(userDTO.getLogin())
                .filter(oldUser -> !oldUser.getId()
                        .equals(userDTO.getId())).ifPresent(user1 -> {
                    throw badRequest(localizationService.getMessage("login.already.used")).get();
                });
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(notFound(localizationService.getMessage("user"), userDTO.getId()));
        if (user.getAuthorities().contains(new Authority(AuthoritiesConstants.ADMIN))) {
            throw new BadRequestException("Вы не можете изменить администратора");
        }
        this.clearLoginCaches(user.getLogin());
        user.setLogin(userDTO.getLogin());
        if (userDTO.getFullName() != null) {
            user.setFirstName(userDTO.getFullName().getFirstName());
            user.setLastName(userDTO.getFullName().getLastName());
            user.setPatronymic(userDTO.getFullName().getPatronymic());
        }
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (userDTO.getBranchId() != null) {
            user.setBranchId(userDTO.getBranchId());
        }
        user.setOwner(false);
        if (userDTO.getAuthorities() != null && !userDTO.getAuthorities().isEmpty()) {
            user.getAuthorities().clear();
            authorityRepository.findById(AuthoritiesConstants.USER)
                    .ifPresent(user.getAuthorities()::add);
            for (String role : userDTO.getAuthorities()) {
                authorityRepository.findById(role).ifPresent(user.getAuthorities()::add);
            }
        }

        userRepository.save(user);
        this.clearLoginCaches(user.getLogin());
    }

    public void updateLogin(UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findOneByLogin(userDTO.getLogin());
        if (userOptional.isPresent() && !userOptional.get().getId().equals(userDTO.getId())) {
            throw badRequest(localizationService.getMessage("login.already.used")).get();
        }
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(notFound(localizationService.getMessage("user"), userDTO.getId()));

        this.clearLoginCaches(user.getLogin());
        user.setLogin(userDTO.getLogin());
        userRepository.save(user);
        this.clearLoginCaches(user.getLogin());
    }

    public void clearCache(String login) {
        this.clearLoginCaches(login);
    }

    public void clearToken(Long id) {
        userRepository.findById(id).map(User::getLogin).ifPresent(super::clearLoginCaches);
    }

    private void userDTO2Entity(UserDTO userDTO, User user) {
        if (userDTO.getFullName() != null) {
            user.setFirstName(userDTO.getFullName().getFirstName());
            user.setLastName(userDTO.getFullName().getLastName());
            user.setPatronymic(userDTO.getFullName().getPatronymic());
        }
        user.setLangKey(userDTO.getLangKey());
        if (user.getUserImage() != null && !Objects.equals(user.getImageId(), userDTO.getImageId())) {
            fileUploadService.removeObject(user.getUserImage());
        }
        user.setImageId(userDTO.getImageId());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setPersonalNumber(userDTO.getPersonalNumber());
        user.setSalary(userDTO.getSalary());
        user.setCardNumber(userDTO.getCardNumber());
        if (userDTO.getBranchId() != null) {
            branchRepository.findById(userDTO.getBranchId())
                    .filter(branch1 -> branch1.getCompanyId().equals(user.getCompanyId()))
                    .orElseThrow(notFound(localizationService.getMessage("branch"), userDTO.getBranchId()));
            user.setBranchId(userDTO.getBranchId());
        }
        if (userDTO.getBranchGroupId() != null) {
            user.setBranchGroupId(userDTO.getBranchGroupId());
        }
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }

        if (user.getId() == null) {
            userDTO.getAuthorities().add(AuthoritiesConstants.USER);
            Set<Authority> authorities = userDTO.getAuthorities().stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        } else if (!user.isOwner()) {
            user.getAuthorities().clear();
            authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(user.getAuthorities()::add);
            for (String role : userDTO.getAuthorities()) {
                authorityRepository.findById(role).ifPresent(user.getAuthorities()::add);
            }
        }
    }

    public Page<UserDTO> getUsers(UserFilter filter, boolean forAdmin) {
        UserSpecificationFilter specificationFilter = UserSpecificationFilter.builder()
                .activated(filter.isActivated())
                .dismissed(filter.isDismissed())
                .roles(filter.getRoles())
                .companyId(filter.getCompanyId())
                .branchIds(filter.getBranchIdsWithBranchId())
                .branchGroupIds(filter.getBranchGroupIds())
                .from(filter.getFrom())
                .to(filter.getTo())
                .search(filter.getSearch())
                .orderBy(filter.getOrderBy())
                .sortOrder(filter.getSortOrder())
                .build();
        if (!forAdmin) {
            specificationFilter.setCompanyId(getCurrentCompanyId());
            if (isBranchAdmin() && CollectionUtils.isEmpty(specificationFilter.getBranchIds())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> specificationFilter.setBranchGroupIds(Set.of(groupId)),
                                () -> specificationFilter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
            if (!isOwner() && !isBranchAdmin()) {
                specificationFilter.setBranchIds(Set.of(getCurrentBranchId()));
            }
        } else {
            specificationFilter.setActivated(null);
        }
        return userRepository
                .findAll((Specification<User>) (root, criteriaQuery, criteriaBuilder) ->
                                UserSpecification.getPredicateWithSort(root, criteriaQuery, criteriaBuilder, specificationFilter),
                        filter.getPageable())
                .map(user -> user.getListDTO(forAdmin));
    }

    public Page<UserDTO> getAdminUsers(UserFilter filter) {
        UserSpecificationFilter specificationFilter = UserSpecificationFilter.builder()
                .roles(filter.getRoles() == null ? Sets.newHashSet(AuthoritiesConstants.SMARTPOS_ADMIN, AuthoritiesConstants.SALES_MANAGER, AuthoritiesConstants.SUPPORT) : filter.getRoles())
                .activated(filter.isActivated())
                .search(filter.getSearch())
                .build();
        return userRepository
                .findAll((Specification<User>) (root, criteriaQuery, criteriaBuilder) ->
                                UserSpecification.getPredicateWithSort(root, criteriaQuery, criteriaBuilder, specificationFilter),
                        filter.getPageable())
                .map(user -> user.getListDTO(true));
    }

    public Page<UserDTO> getTelegramUsers(TelegramAccountFilter filter) {
        return telegramAccountRepository.findAllByFilter(filter).map(TelegramAccount::getTelegramDTO);
    }

    public List<UserDTO> getCashiers(UserFilter filter) {
        UserSpecificationFilter specificationFilter = UserSpecificationFilter.builder()
                .activated(filter.isActivated())
                .dismissed(false)
                .companyId(getCurrentCompanyId())
                .branchIds(Set.of(Optional.ofNullable(filter.getBranchId()).orElse(getCurrentBranchId())))
                .owner(isOwner())
                .build();
        return userRepository
                .findAll((Specification<User>) (root, criteriaQuery, criteriaBuilder) -> UserSpecification.getPredicateWithSort(root, criteriaQuery, criteriaBuilder, specificationFilter))
                .stream().map(user -> user.getListDTO(false)).collect(Collectors.toList());
    }

    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(notFound(localizationService.getMessage("user"), id));
        checkForAccessToCompany(user.getCompanyId());
        return user.getDTO();
    }

    public UserDTO getUser(String login) {
        User user = userRepository.findOneByLogin(login)
                .orElseThrow(notFound(localizationService.getMessage("user"), login));
        checkForAccessToCompany(user.getCompanyId());
        return user.getDTO();
    }

    public List<SelectItem> search(UserFilter filter, boolean forAdmin) {
        UserSpecificationFilter specificationFilter = UserSpecificationFilter.builder()
                .activated(filter.isActivated())
                .dismissed(false)
                .companyId(filter.getCompanyId())
                .branchIds(filter.getBranchIdsWithBranchId())
                .branchGroupIds(filter.getBranchGroupIds())
                .roles(filter.getRoles())
                .search(filter.getSearch())
                .orderBy(filter.getOrderBy())
                .sortOrder(filter.getSortOrder())
                .build();
        if (!forAdmin) {
            specificationFilter.setCompanyId(getCurrentCompanyId());
        }
        return userRepository.search(specificationFilter);
    }

    public UserDTO getCurrent() {
        final Long userId = getCurrentUserId();
        UserDTO dto = userRepository.findById(userId)
                .map(User::getDTO)
                .orElseThrow(notFound(localizationService.getMessage("user"), getCurrentUserId()));
        var terminals = deviceRepository.findAllByBranchIdAndType(getCurrentBranchId(), DeviceType.FISCAL);
        if (CollectionUtils.isNotEmpty(terminals)) {
            String terminalSerialNumber = terminals.getFirst().getSerialNumber();
            if (terminals.size() > 1) {
                terminalSerialNumber = shiftRepository.findFirstOpenShiftTerminalSerialNumber(userId);
                if (terminalSerialNumber == null) {
                    terminalSerialNumber = terminals.stream().filter(t -> Objects.equals(t.getSerialNumber(), dto.getTerminalSN())).findFirst().orElseGet(terminals::getFirst).getSerialNumber();
                }
            }
            dto.setTerminalSN(terminalSerialNumber);
        }
        return dto;
    }

    public UserDTO getCashboxUser() {
        return userRepository.findById(getCurrentUserId())
                .filter(user -> user.getBranch() != null)
                .filter(user -> StringUtils.isNotBlank(user.getBranch().getSerialNumber()))
                .map(User::getDTO)
                .orElseThrow(notFound(localizationService.getMessage("user"), getCurrentUserId()));
    }

    public User registerOwner(RegisterVM registerVM) {
        User user = userRepository.findOneByLogin(registerVM.getLogin().toLowerCase())
                .orElseGet(User::new);
        if (user.getId() != null) {
            if (user.isActivated() && !user.isDeleted()) {
                throw badRequest(localizationService.getMessage("user.login.used")).get();
            }
        } else {
            user.setLogin(registerVM.getLogin().toLowerCase());
            // new user will have "temp" password before activation
            user.setPassword(passwordEncoder.encode("temp"));
        }
        // new user is not active
        user.setActivationKey(RandomUtil.generateNumericKey());
        user.setActivated(false);
        user.setOwner(true);
        user.getAuthorities().clear();
        user.getAuthorities().add(authorityRepository.getReferenceById(AuthoritiesConstants.USER));
        user.getAuthorities().add(authorityRepository.getReferenceById(AuthoritiesConstants.OWNER));
        userRepository.save(user);
        this.clearLoginCaches(user.getLogin());
        return user;
    }

    public void activateRegistrationSms(String login, String key, String tempPassword) {
        userRepository.findOneByLoginAndActivationKey(login, key)
                .map(user -> {
                    // activate given user for the registration key.
                    user.setActivated(true);
                    user.setActivationKey(null);
                    user.setPassword(passwordEncoder.encode(tempPassword));
                    userRepository.save(user);
                    this.clearLoginCaches(user.getLogin());
                    return user;
                }).orElseThrow(badRequest(localizationService.getMessage("error.activation.code")));
    }

    public String activate(String login, String key, String tempPassword) {
        return userRepository.findOneByLoginAndActivationKey(login, key)
                .map(user -> {
                    final String refreshToken = UUID.randomUUID().toString();
                    user.setActivated(true);
                    user.setActivationKey(null);
                    user.setPassword(passwordEncoder.encode(tempPassword));
                    user.setRefreshToken(refreshToken);
                    user.setRefreshDate(LocalDateTime.now().plusMonths(3));
                    userRepository.saveAndFlush(user);
                    this.clearLoginCaches(user.getLogin());
                    return refreshToken;
                }).orElseThrow(badRequest(localizationService.getMessage("error.activation.code")));
    }

    public User requestPasswordResetSms(String login) {
        return userRepository.findOneByLogin(login)
//            .filter(User::isActivated)
                .map(user -> {
                    if (StringUtils.isEmpty(user.getResetKey())) {
                        user.setResetKey(RandomUtil.generateNumericKey());
                    }
                    user.setResetDate(LocalDateTime.now());
                    userRepository.save(user);
                    this.clearLoginCaches(user.getLogin());
                    return user;
                }).orElseThrow(notFound(localizationService.getMessage("user.number", login)));
    }

    public boolean checkResetKey(KeyAndPasswordVM keyAndPasswordVM) {
        User user = userRepository.findOneByLogin(keyAndPasswordVM.getLogin()).orElseThrow(notFound(localizationService.getMessage("user.number", keyAndPasswordVM.getKey())));
        return user.getResetKey() != null && user.getResetKey().equals(keyAndPasswordVM.getKey());
    }

    public void completePasswordResetSms(String newPassword, String key, String login) {
        userRepository.findOneByLoginAndResetKey(login, key)
                .filter(user -> user.getResetDate().isAfter(LocalDateTime.now().minusDays(1)))
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);
                    user.setActivated(true);
                    user.setFailedAttempts(null);
                    user.setLockTime(null);
                    userRepository.save(user);
                    this.clearLoginCaches(user.getLogin());
                    return user;
                }).orElseThrow(badRequest(localizationService.getMessage("error.recovery.code")));
    }


    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .ifPresent(user -> {
                    if (user.getAuthorities().contains(new Authority(AuthoritiesConstants.ADMIN))) {
                        throw new BadRequestException("Вы не можете изменить пароль администратора");
                    }
                    String currentEncryptedPassword = user.getPassword();
                    if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                        throw badRequest(localizationService.getMessage("password.incorrect")).get();
                    }
                    if (passwordEncoder.matches(newPassword, currentEncryptedPassword)) {
                        throw badRequest(localizationService.getMessage("new.password.same.old.one")).get();
                    }
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    this.clearLoginCaches(user.getLogin());
                });
    }

    public void changeLogin(String currentLogin, String newLogin) {
        userRepository.findOneByLogin(newLogin).ifPresent(user -> {
            throw new BadRequestException(localizationService.getMessage("login.already.used"));
        });
        userRepository.findOneByLogin(currentLogin)
                .ifPresent(user -> {
                    if (user.getAuthorities().contains(new Authority(AuthoritiesConstants.ADMIN))) {
                        throw new BadRequestException("Вы не можете изменить логин администратора");
                    }
                    user.setLogin(newLogin);
                    userRepository.save(user);
                    this.clearLoginCaches(currentLogin);
                    this.clearLoginCaches(newLogin);
                });
    }

    public void changeName(FullNameDTO dto) {
        userRepository.findOneByLogin(dto.getPhone())
                .ifPresent(user -> {
                    user.setFirstName(dto.getFirstName());
                    user.setLastName(dto.getLastName());
                    user.setPatronymic(dto.getPatronymic());
                    userRepository.save(user);
                    this.clearLoginCaches(dto.getPhone());
                });
    }

    public void changeUserPassword(final Long userId, final String password) {
        userRepository.findById(userId)
                .ifPresent(user -> {
                    user.setPassword(passwordEncoder.encode(password));
                    user.setDeleted(false);
                    user.setActivated(true);
                    userRepository.save(user);
                    this.clearLoginCaches(user.getLogin());
                });
    }

    @Transactional
    public void resetPin(String phone) {
        Optional.ofNullable(phone)
                .flatMap(userRepository::findOneByLogin)
                .filter(user1 -> user1.getAuthorities().stream().map(Authority::getName)
                        .collect(Collectors.toSet()).contains(AuthoritiesConstants.CASHIER))
                .map(user -> {
                    StringUtils.isEmpty(user.getResetKey());
                    String newPin = RandomUtil.generateNumericKey();
                    user.setResetKey(newPin);
                    user.setPassword(passwordEncoder.encode(newPin));
                    user.setResetDate(LocalDateTime.now());
                    user.setFailedAttempts(null);
                    user.setLockTime(null);
                    userRepository.save(user);
                    this.clearLoginCaches(user.getLogin());

                    smsService.sendSms(new EtcRequestDto(phone, StringUtils.join(Constants.COMMON_SMS, user.getResetKey())));
                    return user;
                }).orElseThrow(notFound(localizationService.getMessage("cashier.number", phone)));
    }

    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }

    public void deactivateUser(Long userId, boolean delete) {
        User user = userRepository.findById(userId)
                .orElseThrow(notFound(localizationService.getMessage("user"), userId));
        String login = user.getLogin();
        checkForAccessToCompany(user.getCompanyId());
        Company company = companyRepository.findById(user.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        if (isAdminCompany(company)) {
            throw new BadRequestException("Вы не можете удалить пользователя в AdminCompany");
        }
        if (delete) {
            if (user.isOwner()) {
                throw badRequest(localizationService.getMessage("error.delete.owner")).get();
            }
            user.setDeleted(true);
            user.setLogin(null);
        }
        user.setActivated(false);
        userRepository.save(user);
        clearLoginCaches(login);
    }

    public void activateUser(Long userId, UserActivationDTO activationDTO) {
        userRepository.findById(userId).map(user -> {
                    checkForAccessToCompany(user.getCompanyId());
                    user.setActivated(true);
                    user.setDeleted(false);
                    user.setLogin(activationDTO.getLogin());
                    user.setPassword(passwordEncoder.encode(activationDTO.getPassword()));
                    userRepository.save(user);
                    clearCache(user.getLogin());
                    return user;
                })
                .orElseThrow(notFound(localizationService.getMessage("user"), userId));
    }

    public String getRefreshToken(String username) {
//        log.debug("Generate refresh token for user: {}", username);
        final String refreshToken = UUID.randomUUID().toString();
        User user = userRepository.findOneByLogin(username).orElseThrow(notFound(localizationService.getMessage("user")));
        user.setRefreshToken(refreshToken);
        user.setRefreshDate(LocalDateTime.now().plusHours(applicationProperties.getRefreshTokenValidityInHours()));
        userRepository.save(user);
        this.clearLoginCaches(user.getLogin());
        return refreshToken;
    }

    public User getUserByRefreshToken(String refreshToken) {
        Optional<User> optionalUser = userRepository.findFirstByRefreshToken(refreshToken);
        if (optionalUser.isEmpty()) {
            throw new UserNotActivatedException(localizationService.getMessage("user.token.not.found"));
        }
        User user = optionalUser.get();
        if (user.isDeleted()) {
            throw new UserNotActivatedException(localizationService.getMessage("user.fired"));
        }
        if (!user.isActivated()) {
            throw new UserNotActivatedException(localizationService.getMessage("user.not.active"));
        }
        if (user.getRefreshDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException(localizationService.getMessage("token.expired"), Instant.now());
        }
        log.debug("Authenticating user: {}", user.getLogin());
        userRepository.save(user);
        this.clearLoginCaches(user.getLogin());
        return user;
    }

    public String getCurrentUsersCompanyTin() {
        return getCurrentCompanyTin();
    }

    @Transactional(noRollbackFor = BadCredentialsException.class)
    public ResponseEntity<TokenDTO> loginWeb(LoginVM loginVM) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            authentication.getPrincipal();
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (authentication.getPrincipal() instanceof CustomUser customUser) {
                String access_token = tokenProvider.createToken(customUser, false);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + access_token);
                shiftReportService.createOrGet(null);
                authenticationSuccessHandler(loginVM);
                return new ResponseEntity<>(new TokenDTO(access_token, null), httpHeaders, HttpStatus.OK);
            } else {
                throw new UserNotActivatedException("User " + loginVM.getUsername() + " was not activated");
            }
        } catch (Exception e) {
            authenticationFailureHandler(loginVM, e);
            throw e;
        }
    }


    public void authenticationFailureHandler(LoginVM vm, Exception e) {
        if (e instanceof BadCredentialsException) {
            User user = userRepository.findOneByLogin(vm.getUsername()).orElseThrow(() -> new UserNotFoundException(localizationService.getMessage("user.not.found", vm.getUsername())));
            Integer failedAttempts = user.getFailedAttempts() != null ? user.getFailedAttempts() + 1 : 1;
            user.setFailedAttempts(failedAttempts);
            LocalDateTime lockTime = Optional.ofNullable(user.getLockTime()).orElse(LocalDateTime.MIN);
            if (user.getFailedAttempts() % 3 == 0 && lockTime.isBefore(LocalDateTime.now())) {
                user.setLockTime(LocalDateTime.now().plusMinutes(20));
            }
            clearLoginCaches(vm.getUsername());
            userRepository.save(user);
        }
    }

    public void authenticationSuccessHandler(LoginVM vm) {
        User user = userRepository.findOneByLogin(vm.getUsername()).orElseThrow(() -> new UserNotFoundException(localizationService.getMessage("user.not.found", vm.getUsername())));
        user.setFailedAttempts(null);
        user.setLockTime(null);
        user.setLatestTerminal(getCurrentTerminalSnByHeader());
        clearLoginCaches(vm.getUsername());
        userRepository.save(user);
    }

    public List<CompanyBaseDTO> getManagerCompanies(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(notFound(localizationService.getMessage("user"), userId));
        if (CollectionUtils.isNotEmpty(user.getCompanies())) {
            return user.getCompanies().stream().map(commonMapper::toBaseDto).collect(Collectors.toList());
        }
        return null;
    }

    public FileDTO uploadUserImage(Long userId, MultipartFile file) {
        if (!Sets.newHashSet("image/jpeg", "image/png", "image/bmp", "image/ico", "image/heif", "image/heic").contains(file.getContentType())) {
            throw new BadRequestException("Ошибка формата файла(принимает только файлы jpeg,png,ico,heif,heic)");
        }
        User user = userRepository.findById(userId).orElseThrow(notFound(localizationService.getMessage("user"), userId));
        if (user.getUserImage() != null) {
            removeUserImage(userId);
        }
        FileUpload image = fileUploadService.fileUpload(file, userId, EntityType.USER, true);
        log.info("Image uploaded to {}", image.getUrl());
        user.setUserImage(image);
        user.setImageId(image.getId());
        userRepository.save(user);
        return fileUploadMapper.toDTO(image);

    }

    public void removeUserImage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(notFound(localizationService.getMessage("user"), userId));
        FileUpload image = user.getUserImage();
        if (image != null) {
            user.setUserImage(null);
            user.setImageId(null);
            userRepository.save(user);
            fileUploadService.removeObject(image);
            fileUploadRepository.delete(image);
        }
    }

    public FullNameDTO managerSearch(String managerLogin) {
        User user = userRepository.findOneByLogin(managerLogin).orElseThrow(notFound("Manager"));
        // TODO: 23/05/23 change role to manager
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.SALES_MANAGER);
        if (!user.getAuthorities().contains(authority)) throw new BadRequestException("This user isn't manager");
        return user.getFullName();
    }

    public List<ManagerLookUpDTO> managerLookUp(final UserFilter filter, final String role) {
        final UserSpecificationFilter specificationFilter = UserSpecificationFilter.builder()
                .activated(filter.isActivated())
                .dismissed(false)
                .roles(Sets.newHashSet(role))
                .search(filter.getSearch())
                .build();
        return userRepository.searchManager(specificationFilter);
    }

    public boolean checkLogin(String login) {
        return userRepository.findOneByLogin(login).isPresent();
    }

    public List<String> getRolesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login)
                .orElseThrow(() -> new EntityNotFound(localizationService.getMessage("user.not.found", login)))
                .getAuthorities()
                .stream().map(Authority::getName)
                .toList();
    }

    public void addChangePricePermissionToCashier(Long id) {
        User user = userRepository.findById(id).orElseThrow(notFound(localizationService.getMessage("user")));
        final String CATALOG_CHANGE_PRICE = "CATALOG_CHANGE_PRICE";
        if (permissionRepository.findByCode(CATALOG_CHANGE_PRICE).isEmpty()) {
            Permission permission = new Permission();
            permission.setCode(CATALOG_CHANGE_PRICE);
            permissionRepository.save(permission);
        }
        UserPermission userPermission = new UserPermission();
        userPermission.setPermissionCode(CATALOG_CHANGE_PRICE);
        userPermission.setUserId(id);
        userPermission.setHasAccess(true);
        user.getUserPermissions().add(userPermission);
        userRepository.save(user);
    }

    public void blockUser(Long userId, boolean activated) {
        User user = userRepository.findById(userId).orElseThrow(notFound(localizationService.getMessage("user")));
        user.setActivated(activated);
        userRepository.save(user);
        clearCache(user.getLogin());
    }

    public void createTerminalCashier(TerminalCashierErpCreateDto erpTerminalCashierDto) {
        Company company = companyRepository.findFirstByCtoId(erpTerminalCashierDto.companyErpId())
                .orElseThrow(() -> new BadRequestException("Для этой операции компания должна быть уже зарегистрирована в SmartPos."));
        Branch branch = branchRepository.findFirstByCtoId(erpTerminalCashierDto.branchErpId())
                .orElseThrow(() -> new BadRequestException("Для этой операции филиал должен быть уже зарегистрирован в SmartPos."));
        Optional<User> user = userRepository.findOneByLogin(erpTerminalCashierDto.login());
        if (user.isPresent()) {
            throw new BadRequestException("Пользователь с таким логином уже существует.");
        }
        User newUser = new User();
        newUser.setLogin(erpTerminalCashierDto.login());
        newUser.setFirstName(erpTerminalCashierDto.firstName());
        newUser.setLastName(erpTerminalCashierDto.lastName());
        newUser.setPatronymic(erpTerminalCashierDto.patronymic());
        newUser.setBranchId(branch.getId());
        newUser.setCompanyId(company.getId());
        newUser.setActivated(true);
        newUser.setOwner(false);
        String code = RandomUtil.generateNumericKey();
        newUser.setPassword(passwordEncoder.encode(code));
        newUser.setAuthorities(Set.of(new Authority(AuthoritiesConstants.CASHIER), new Authority(AuthoritiesConstants.USER)));
        userRepository.save(newUser);
        smsService.sendSms(new EtcRequestDto(erpTerminalCashierDto.login(), StringUtils.join(Constants.COMMON_SMS, code)));

    }
}
