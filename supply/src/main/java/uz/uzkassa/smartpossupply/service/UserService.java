package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.domain.FileUpload;
import uz.uzkassa.smartpossupply.domain.address.Address;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.auth.UserAuth;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.company.CompanyPermission;
import uz.uzkassa.smartpossupply.domain.settings.CompanySettings;
import uz.uzkassa.smartpossupply.dto.auth.*;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.base.SelectItemDTO;
import uz.uzkassa.smartpossupply.dto.billingpermission.PermissionDetailDTO;
import uz.uzkassa.smartpossupply.dto.branch.BranchCoordinateDTO;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.CompanySettingsListDTO;
import uz.uzkassa.smartpossupply.dto.integration.BonusResponseFilter;
import uz.uzkassa.smartpossupply.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.smartpossupply.dto.sms.SmsDTO;
import uz.uzkassa.smartpossupply.dto.user.*;
import uz.uzkassa.smartpossupply.dto.user.admin.AdminUserDTO;
import uz.uzkassa.smartpossupply.dto.user.admin.AdminUserDetailDTO;
import uz.uzkassa.smartpossupply.dto.user.admin.AdminUserListDTO;
import uz.uzkassa.smartpossupply.enums.CompanyStatus;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.MarketplaceSettingsStep;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.filters.UserFilter;
import uz.uzkassa.smartpossupply.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpossupply.mappers.FileUploadMapper;
import uz.uzkassa.smartpossupply.property.JwtTokenProperties;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqPermissionProducer;
import uz.uzkassa.smartpossupply.repository.FileUploadRepository;
import uz.uzkassa.smartpossupply.repository.RolePermissionRepository;
import uz.uzkassa.smartpossupply.repository.UserAuthRepository;
import uz.uzkassa.smartpossupply.utils.OtpEncryptionUtils;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
//@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserService extends BaseService {

    PasswordEncoder passwordEncoder;
    SmsService smsService;
    FileUploadService fileUploadService;
    FileUploadMapper fileUploadMapper;
    FileUploadRepository fileUploadRepository;

    RabbitMqPermissionProducer rabbitMqPermissionProducer;
    PermissionService permissionService;
    RolePermissionRepository rolePermissionRepository;
    BillingServiceFeignClient billingServiceFeignClient;
    UserAuthRepository userAuthRepository;
    CacheService cacheService;
    OtpEncryptionUtils otpEncryptionUtils;
    JwtTokenProperties jwtTokenProperties;
    CompanySettingsService companySettingsService;

    @Override
    public User getCurrentUser() {
        return super.getCurrentUser();
    }

    public CurrentUserMobileDTO getCurrentUserForMobile() {
        User user = userRepository.findFirstByLoginForMobile(getCurrentUserLogin()).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", getCurrentUserLogin()));
        CurrentUserMobileDTO currentUserDTO = new CurrentUserMobileDTO();
        currentUserDTO.setId(user.getId());
        currentUserDTO.setLogin(user.getLogin());
        currentUserDTO.setFirstName(user.getFirstName());
        currentUserDTO.setLastName(user.getLastName());
        currentUserDTO.setPatronymic(user.getPatronymic());
        if (user.getPhoto() != null) {
            currentUserDTO.setPhoto(user.getPhoto().toFileDTO());
        }
        if (user.getCompanyId() != null) {
            currentUserDTO.setCompany(user.getCompany().toCommonBaseDTO());
            currentUserDTO.setPermissions(getCompanyBillingPermissions(user.getCompany()));
            if (user.getCompany().getActivityTypeId() != null) {
                currentUserDTO.setActivityType(user.getCompany().getActivityType().toCommonDTO());
            }
        }
        if (user.getBranchId() != null) {
            currentUserDTO.setBranch(user.getBranch().toCommonDTO());
        }
        if (user.getWarehouseId() != null) {
            currentUserDTO.setWarehouse(user.getWarehouse().toCommonDTO());
        }
        currentUserDTO.setRole(user.getRole());
        return currentUserDTO;
    }

    public CurrentUserCabinetDTO getCurrentUserForCabinet() {
        User user = userRepository.findFirstByLoginForCabinet(getCurrentUserLogin()).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", getCurrentUserLogin()));

        CurrentUserCabinetDTO currentUserCabinetDTO = new CurrentUserCabinetDTO();
        currentUserCabinetDTO.setId(user.getId());
        currentUserCabinetDTO.setLogin(user.getLogin());
        currentUserCabinetDTO.setFirstName(user.getFirstName());
        currentUserCabinetDTO.setLastName(user.getLastName());
        currentUserCabinetDTO.setPatronymic(user.getPatronymic());
        currentUserCabinetDTO.setStatus(user.getStatus());
        currentUserCabinetDTO.setUserTin(user.getTin());
        if (user.getCompanyId() != null) {
            List<CompanySettingsListDTO> companySettings = companySettingsService.getList();
            Boolean isMarketplace = companySettings.stream()
                    .filter(settings -> settings.getSettingsType().equals(GenericSettingsType.IN_MARKETPLACE))
                    .map(CompanySettingsListDTO::isEnabled)
                    .findFirst()
                    .orElse(false);

            currentUserCabinetDTO.setCompany(user.getCompany().toCommonBaseDTO());
            currentUserCabinetDTO.setCompanyType(user.getCompany().getCompanyType());
            currentUserCabinetDTO.setProductGroup(user.getCompany().getProductGroup());
            currentUserCabinetDTO.setPermissions(getCompanyBillingPermissions(user.getCompany()));
            currentUserCabinetDTO.setPermissionDetails(getCompanyBillingPermissionDetails(user.getCompany()));
            currentUserCabinetDTO.setParentCompanyId(user.getCompany().getParentId());
            currentUserCabinetDTO.setInMarketplace(isMarketplace);
        }
        if (user.getBranchId() != null) {
            currentUserCabinetDTO.setBranch(user.getBranch().toCommonDTO());
        }
        if (user.getWarehouseId() != null) {
            currentUserCabinetDTO.setWarehouse(user.getWarehouse().toCommonDTO());
        }
        currentUserCabinetDTO.setRole(user.getRole());
        return currentUserCabinetDTO;
    }

    public CurrentUserAdminDTO getCurrentUserForAdmin() {
        User user = userRepository.findFirstByLoginForAdmin(getCurrentUserLogin()).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", getCurrentUserLogin()));
        CurrentUserAdminDTO currentUserDTO = new CurrentUserAdminDTO();
        currentUserDTO.setId(user.getId());
        currentUserDTO.setFirstName(user.getFirstName());
        currentUserDTO.setLastName(user.getLastName());
        currentUserDTO.setPatronymic(user.getPatronymic());
        currentUserDTO.setPermissions(permissionService.getUserPermissions(user.getId()));
        currentUserDTO.setRole(user.getRole());
        return currentUserDTO;
    }

    @Transactional
    public void updateCurrentUser(FullNameDTO currentUserDTO) {
        User user = getCurrentUser();
        user.setFirstName(currentUserDTO.getFirstName());
        user.setLastName(currentUserDTO.getLastName());
        user.setPatronymic(currentUserDTO.getPatronymic());
        userRepository.save(user);
        cacheService.evictUser(user.getLogin());
    }

    private Set<String> getCompanyBillingPermissions(Company company) {
        LocalDateTime endTimeOfDay = LocalDateTime.now();
        Set<String> permissions = new LinkedHashSet<>();
        if (company.isFreeTrial()) {
            permissions.add("FREE_TRIAL");
        } else {
            Map<String, LocalDateTime> companyPermissions = company.getPermissions()
                    .stream().collect(Collectors.toMap(CompanyPermission::getServiceType, CompanyPermission::getEndDate));
            for (String serviceType : companyPermissions.keySet()) {
                if (companyPermissions.get(serviceType).isAfter(endTimeOfDay)) {
                    permissions.add(serviceType);
                }
            }
        }
        return permissions;
    }

    private List<PermissionDetailDTO> getCompanyBillingPermissionDetails(Company company) {
      return   company.getPermissions().stream()
              .filter(permission -> permission.getEndDate() == null || permission.getEndDate().isAfter(LocalDateTime.now()))
              .map(permission -> {
            PermissionDetailDTO permissionDetailDTO = new PermissionDetailDTO();
            permissionDetailDTO.setXizmatId(permission.getXizmatId());
            permissionDetailDTO.setTitle(permission.getTitle());
            permissionDetailDTO.setTitleUz(permission.getTitleUz());
            permissionDetailDTO.setXizmatType(permission.getXizmatType());
            permissionDetailDTO.setAgreementId(permission.getAgreementId());
            permissionDetailDTO.setServiceType(permission.getServiceType());
            return permissionDetailDTO;
        }).toList();
    }

    @Transactional
    public void register(RegistrationDTO registrationDTO) {
        User user = userRepository.findFirstByLoginAndDeletedIsFalse(registrationDTO.getPhone().toLowerCase()).orElse(null);
        if (user != null) {
            if (UserStatus.ACTIVE.equals(user.getStatus())) {
                throw validationService.badRequestExceptionThrow(
                        localizationService.localize(MessageConstants.USER_ALREADY_EXISTS_BY_LOGIN, new Object[]{registrationDTO.getPhone()})
                ).get();
            } else if (UserStatus.PENDING.equals(user.getStatus())) {
                smsService.sendActivationCode(new SmsDTO(user.getLogin()));
                return;
            } else {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.USER_IS_BLOCKED, new Object[]{registrationDTO.getPhone()})).get();
            }
        }

        if (!Role.getBusinessOwnerRole().contains(registrationDTO.getBusinessOwnerRole())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(
                    MessageConstants.BUSINESS_OWNER_ROLE_INVALID,
                    new Object[]{registrationDTO.getBusinessOwnerRole()})).get();
        }

        User owner = new User();
        owner.setLogin(registrationDTO.getPhone().toLowerCase());
        owner.setPassword(passwordEncoder.encode(SecurityUtils.generateActivationKey()));
        owner.setRole(registrationDTO.getBusinessOwnerRole());
        userRepository.save(owner);
        smsService.sendActivationCode(new SmsDTO(owner.getLogin()));
        cacheService.evictUser(owner.getLogin());
    }

    @Transactional
    public SecretKeyDTO activateStart(ActivationDTO activateDTO) {
        return userAuthRepository.findFirstByUsername(activateDTO.getPhone())
                .map(userAuth -> this.baseChangePasswordCheck(userAuth, activateDTO.getActivationCode()))
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", activateDTO.getPhone()));
    }

    @Transactional
    public void activateFinish(ActivationFinishDTO activationFinishDTO) {
        if (!activationFinishDTO.getPassword().matches(activationFinishDTO.getConfirmPassword())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize("user.password.dont.match")).get();
        }
        userAuthRepository
                .findFirstByUsername(activationFinishDTO.getPhone()).map(userAuth -> {
                    if (!activationFinishDTO.getSecretKey().equals(userAuth.getSecretKey())) {
                        throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ITEM_INVALID, new Object[]{localizationService.localize(MessageConstants.SECRET_KEY)}), "").get();
                    }
                    userRepository.findFirstByLoginAndDeletedIsFalse(activationFinishDTO.getPhone())
                            .ifPresent(
                                    user -> {
                                        user.setPassword(passwordEncoder.encode(activationFinishDTO.getPassword()));
                                        user.setStatus(UserStatus.ACTIVE);
                                        userRepository.save(user);
                                        cacheService.evictUser(user.getLogin());
                                        rabbitMqPermissionProducer.save(
                                                new UserRolePermissionPayload(
                                                        user.getId(),
                                                        rolePermissionRepository.findAllPermissionIdsByRole(user.getRole())
                                                )
                                        );
                                    }
                            );
                    return userAuth;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", activationFinishDTO.getPhone()));
    }

    private void baseChangePasswordInit(User user) {
        smsService.sendActivationCode(new SmsDTO(user.getLogin()));
    }

    private SecretKeyDTO baseChangePasswordCheck(UserAuth userAuth, String activationCode) {
        if (!otpEncryptionUtils.matches(activationCode, userAuth.getActivationCode())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ACTIVATION_INCORRECT_CODE), localizationService.localize(MessageConstants.ACTIVATION_INCORRECT_CODE)).get();
        }
        if (LocalDateTime.now().isAfter(userAuth.getExpiryDate())) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ACTIVATION_CODE_EXPIRED), localizationService.localize(MessageConstants.ACTIVATION_CODE_EXPIRED)).get();
        }
        userAuth.setSecretKey(SecurityUtils.generateSecretKey());
        userAuthRepository.save(userAuth);
        return new SecretKeyDTO(userAuth.getSecretKey());
    }

    private void baseChangePasswordFinish(User user, String password, String confirmPassword, String secretKey) {
        if (!password.equals(confirmPassword)) {
            throw validationService.badRequestExceptionThrow(
                    localizationService.localize(MessageConstants.PASSWORD_DOES_NOT_MATCH)
            ).get();
        }

        var userAuth = userAuthRepository.findFirstByUsernameAndSecretKey(user.getLogin(), secretKey);
        if (userAuth.isEmpty()) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ITEM_INVALID, new Object[]{localizationService.localize(MessageConstants.SECRET_KEY)}), "").get();
        }
        if (userAuth.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            throw validationService.badRequestExceptionThrow("Secret key expired").get();
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setSecretKey(null);
        userRepository.save(user);
        cacheService.evictUser(user.getLogin());
    }

    @Transactional
    public void forgotPasswordInit(PhoneDTO phoneDTO) {
        userRepository.findFirstByLoginAndDeletedIsFalse(phoneDTO.getPhone())
                .map(user -> {
                    this.baseChangePasswordInit(user);
                    return user;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "phone", phoneDTO.getPhone()));
    }

    @Transactional
    public SecretKeyDTO forgotPasswordCheck(ActivationDTO activateDTO) {
        return userAuthRepository.findFirstByUsername(activateDTO.getPhone())
                .map(userAuth -> this.baseChangePasswordCheck(userAuth, activateDTO.getActivationCode())).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "phone", activateDTO.getPhone()));
    }

    @Transactional
    public void forgotPasswordFinish(ForgotPasswordDTO forgotPasswordDTO) {
        userRepository.findFirstByLoginAndDeletedIsFalse(forgotPasswordDTO.getPhone()).map(user -> {
            this.baseChangePasswordFinish(user, forgotPasswordDTO.getPassword(), forgotPasswordDTO.getConfirmPassword(), forgotPasswordDTO.getSecretKey());
            return user;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "phone", forgotPasswordDTO.getPhone()));

    }

    @Transactional
    public void changePasswordInit(ResetPasswordInitDTO resetPasswordInitDTO) {
        User user = userRepository.findByIdAndDeletedIsFalse(resetPasswordInitDTO.getEmployeeId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "employeeId", resetPasswordInitDTO.getEmployeeId()));

        if (!resetPasswordInitDTO.getCompanyId().equals(user.getCompanyId())) {
            throw validationService.badRequestExceptionThrow("Employee does not belong to company").get();
        }

        this.baseChangePasswordInit(user);

    }

    @Transactional
    public void changePasswordInit() {
        this.baseChangePasswordInit(getCurrentUser());
    }

    @Transactional
    public SecretKeyDTO changePasswordCheck(MarketplaceActivationDTO marketplaceActivationDTO) {
        User user = userRepository.findByIdAndDeletedIsFalse(marketplaceActivationDTO.getEmployeeId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "employeeId", marketplaceActivationDTO.getEmployeeId()));

        return this.changePasswordCheck(user, marketplaceActivationDTO);
    }

    @Transactional
    public SecretKeyDTO changePasswordCheck(BaseActivationDTO baseActivationDTO) {
        User user = getCurrentUser();
        return this.changePasswordCheck(user, baseActivationDTO);
    }

    public SecretKeyDTO changePasswordCheck(User user, BaseActivationDTO baseActivationDTO) {
        return userAuthRepository.findFirstByUsername(user.getLogin())
                .map(userAuth -> this.baseChangePasswordCheck(userAuth, baseActivationDTO.getActivationCode()))
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", getCurrentUser().getLogin()));
    }

    @Transactional
    public void changePasswordFinish(MarketplaceForgotPasswordDTO marketplaceForgotPasswordDTO) {
        User user = userRepository.findByIdAndDeletedIsFalse(marketplaceForgotPasswordDTO.getEmployeeId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "employeeId", marketplaceForgotPasswordDTO.getEmployeeId()));

        if (!marketplaceForgotPasswordDTO.getCompanyId().equals(user.getCompanyId())) {
            throw validationService.badRequestExceptionThrow("Employee does not belong to company").get();
        }

        this.baseChangePasswordFinish(user, marketplaceForgotPasswordDTO.getPassword(), marketplaceForgotPasswordDTO.getConfirmPassword(), marketplaceForgotPasswordDTO.getSecretKey());
    }

    @Transactional
    public void changePasswordFinish(BaseForgotPasswordDTO baseForgotPasswordDTO) {
        this.baseChangePasswordFinish(getCurrentUser(), baseForgotPasswordDTO.getPassword(), baseForgotPasswordDTO.getConfirmPassword(), baseForgotPasswordDTO.getSecretKey());
    }

    @Transactional
    public void changeUsernameInit() {
        smsService.sendActivationCode(new SmsDTO(getCurrentUser().getLogin()));
    }

    @Transactional
    public SecretKeyDTO changeUsernameCheck(BaseActivationDTO baseActivationDTO) {
        User user = getCurrentUser();
        return userAuthRepository.findFirstByUsername(user.getLogin())
                .map(userAuth -> this.baseChangePasswordCheck(userAuth, baseActivationDTO.getActivationCode()))
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", user.getLogin()));
    }

    @Transactional
    public void changeUsernameFinish(ChangeUsernameFinishDTO changeUsernameFinishDTO) {
        if (userRepository.existsUserByLoginAndDeletedIsFalse(changeUsernameFinishDTO.getPhone())) {
            throw validationService.badRequestExceptionThrow("Username already exists").get();
        }
        userAuthRepository.findFirstByUsername(changeUsernameFinishDTO.getPhone())
                .ifPresent(userAuth -> {
                    if (!changeUsernameFinishDTO.getSecretKey().equals(userAuth.getSecretKey())) {
                        throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.ITEM_INVALID, new Object[]{"", localizationService.localize(MessageConstants.SECRET_KEY)})).get();
                    }
                });
        User user = getCurrentUser();
        String oldLogin = user.getLogin();
        user.setLogin(changeUsernameFinishDTO.getPhone());
        userRepository.save(user);
        cacheService.evictUser(user.getLogin());
        cacheService.evictUser(oldLogin);
    }

    @Transactional
    public void resendActivationKey() {
        this.resendActivationKey(getCurrentUserLogin());
    }

    @Transactional
    public void resendActivationKey(String phone) {
        userAuthRepository.findFirstByUsername(phone)
                .map(userAuth -> {
                    smsService.sendActivationCode(new SmsDTO(userAuth.getUsername()));
                    return userAuth;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", phone));
    }

    @Transactional
    public void sendActivationKey(PhoneDTO phoneDTO) {
        User user = userRepository
                .findFirstByLoginAndDeletedIsFalse(phoneDTO.getPhone())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", phoneDTO.getPhone()));
        smsService.sendActivationCode(new SmsDTO(user.getLogin()));
    }

    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findFirstByLoginAndDeletedIsFalse);
    }

    public List<String> getAuthorities() {
        return Arrays.stream(Role.values()).map(Role::getCode).toList();
    }

    @Transactional
    public void delete(String login) {
        User user = userRepository
                .findFirstByLoginAndDeletedIsFalse(login)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", login));
        user.setDeleted(true);
        user.setStatus(UserStatus.BLOCKED);
        userRepository.save(user);
        cacheService.evictUser(user.getLogin());
    }

    @Transactional
    public FileDTO uploadPhoto(MultipartFile photo) {
        User user = getCurrentUser();
        FileUpload fileUpload = fileUploadService.fileUpload(photo);
        user.setPhotoId(fileUpload.getId());
        user.setPhoto(fileUpload);
        userRepository.save(user);
        cacheService.evictUser(user.getLogin());
        return fileUploadMapper.toLogoDTO(fileUpload);
    }

    @Transactional
    public void removePhoto() {
        User user = getCurrentUser();
        FileUpload photo = user.getPhoto();
        if (photo != null) {
            user.setPhoto(null);
            user.setPhotoId(null);
            userRepository.save(user);
            fileUploadRepository.delete(photo);
            cacheService.evictUser(user.getLogin());
        }
    }

    @Transactional
    public void updateFullName(CurrentUserFullNameMobileDTO currentUserFullNameMobileDTO) {
        User user = getCurrentUser();
        user.setFirstName(currentUserFullNameMobileDTO.getFirstName());
        user.setLastName(currentUserFullNameMobileDTO.getLastName());
        userRepository.save(user);
        cacheService.evictUser(user.getLogin());
    }

    public Long check(PhoneDTO phoneDTO) {
        User user = userRepository.findFirstByLoginAndDeletedIsFalse(phoneDTO.getPhone()).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "login", phoneDTO.getPhone()));
        return user.getId();
    }

    @Transactional
    public Long create(AdminUserDTO adminUserDTO) {
        if (userRepository.existsUserByLoginAndDeletedIsFalse(adminUserDTO.getLogin())) {
            throw validationService.conflictExceptionThrow(
                    localizationService.localize(
                            MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                            new Object[]{
                                    localizationService.localize(User.class.getSimpleName()),
                                    localizationService.localize(MessageConstants.LOGIN),
                                    adminUserDTO.getLogin(),
                            }
                    )
            ).get();
        }

        if (StringUtils.isNotEmpty(adminUserDTO.getPhone())) {
            if (userRepository.existsUserByLoginAndDeletedIsFalse(adminUserDTO.getPhone())) {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                                new Object[]{
                                        localizationService.localize(User.class.getSimpleName()),
                                        localizationService.localize(MessageConstants.LOGIN),
                                        adminUserDTO.getPhone(),
                                }
                        )
                ).get();
            }

            if (userRepository.existsUserByPhoneAndDeletedIsFalse(adminUserDTO.getPhone())) {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                                new Object[]{
                                        localizationService.localize(User.class.getSimpleName()),
                                        localizationService.localize(MessageConstants.PHONE),
                                        adminUserDTO.getPhone(),
                                }
                        )
                ).get();
            }
        }

        User user = new User();
        user.setFirstName(adminUserDTO.getFirstName());
        user.setLastName(adminUserDTO.getLastName());
        user.setPatronymic(adminUserDTO.getPatronymic());
        user.setLogin(adminUserDTO.getLogin());
        user.setPassword(passwordEncoder.encode(adminUserDTO.getPassword()));
        user.setPhone(adminUserDTO.getPhone());
        user.setRole(adminUserDTO.getRole());
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        cacheService.evictUser(user.getLogin());

        rabbitMqPermissionProducer.save(
                new UserRolePermissionPayload(
                        user.getId(),
                        rolePermissionRepository.findAllPermissionIdsByRole(adminUserDTO.getRole())
                )
        );
        return user.getId();
    }

    public AdminUserDetailDTO get(Long id) {
        return userRepository.findById(id).map(user -> {
            AdminUserDetailDTO adminUserDetailDTO = new AdminUserDetailDTO();
            adminUserDetailDTO.setId(id);
            adminUserDetailDTO.setFirstName(user.getFirstName());
            adminUserDetailDTO.setLastName(user.getLastName());
            adminUserDetailDTO.setPatronymic(user.getPatronymic());
            adminUserDetailDTO.setLogin(user.getLogin());
            adminUserDetailDTO.setPhone(user.getPhone());
            adminUserDetailDTO.setRole(user.getRole());
            return adminUserDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long update(Long id, AdminUserDTO adminUserDTO) {
        User user = userRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", id));

        if (StringUtils.isNotEmpty(adminUserDTO.getLogin()) && !adminUserDTO.getLogin().equals(user.getLogin())) {
            if (userRepository.existsUserByLoginAndIdNot(adminUserDTO.getLogin(), user.getId())) {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                                new Object[]{
                                        localizationService.localize(User.class.getSimpleName()),
                                        localizationService.localize(MessageConstants.LOGIN),
                                        adminUserDTO.getLogin(),
                                }
                        )
                ).get();
            }
        }

        if (StringUtils.isNotEmpty(adminUserDTO.getPhone()) && !adminUserDTO.getPhone().equals(user.getPhone())) {
            if (userRepository.existsUserByLoginAndIdNot(adminUserDTO.getPhone(), user.getId())) {
                throw validationService.conflictExceptionThrow(
                        localizationService.localize(
                                MessageConstants.ENTITY_ALREADY_EXIST_BY_PARAM,
                                new Object[]{
                                        localizationService.localize(User.class.getSimpleName()),
                                        localizationService.localize(MessageConstants.PHONE),
                                        adminUserDTO.getPhone(),
                                }
                        )).get();
            }
        }

        user.setFirstName(adminUserDTO.getFirstName());
        user.setLastName(adminUserDTO.getLastName());
        user.setPatronymic(adminUserDTO.getPatronymic());
        user.setLogin(adminUserDTO.getLogin());
        if (StringUtils.isNotEmpty(adminUserDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(adminUserDTO.getPassword()));
        }
        user.setPhone(adminUserDTO.getPhone());
        if (!adminUserDTO.getRole().equals(user.getRole())) {
            user.setRole(adminUserDTO.getRole());
            permissionService.deleteUserPermissionsByUserId(user.getId());
            rabbitMqPermissionProducer.save(
                    new UserRolePermissionPayload(
                            user.getId(),
                            rolePermissionRepository.findAllPermissionIdsByRole(adminUserDTO.getRole())
                    )
            );
        }

        userRepository.save(user);
        cacheService.evictUser(user.getLogin());
        return user.getId();
    }

    @Transactional
    public void updateStatus(Long id, UserStatus status) {
        userRepository.findById(id).map(user -> {
            user.setStatus(status);
            userRepository.save(user);
            cacheService.evictUser(user.getLogin());
            return user;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", id));
    }

    public Page<AdminUserListDTO> getList(UserFilter filter) {
        this.applyFilter(filter);
        ResultList<User> resultList = userRepository.getResultList(filter);
        List<AdminUserListDTO> result = resultList
                .getList()
                .stream()
                .map(user -> {
                    AdminUserListDTO adminUserListDTO = new AdminUserListDTO();
                    adminUserListDTO.setId(user.getId());
                    adminUserListDTO.setFirstName(user.getFirstName());
                    adminUserListDTO.setLastName(user.getLastName());
                    adminUserListDTO.setPatronymic(user.getPatronymic());
                    adminUserListDTO.setLogin(user.getLogin());
                    adminUserListDTO.setPhone(user.getPhone());
                    adminUserListDTO.setRole(user.getRole());
                    adminUserListDTO.setStatus(user.getStatus());
                    adminUserListDTO.setCompany(Optional.ofNullable(user.getCompany()).map(SimpleEntity::toCommonDTO).orElse(null));
                    return adminUserListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<CommonDTO> lookUp(UserFilter filter) {
        this.applyFilter(filter);
        return userRepository.lookUp(filter).stream().map(User::toCommonDTO).toList();
    }

    private void applyFilter(UserFilter filter) {
        filter.setRoles(Role.getAdminRoles().stream().map(Role::getCode).toList());
        if (isFromAdmin()) {
            User currentUser = getCurrentUser();
            if (Role.ROLE_SUPERVISOR.equals(currentUser.getRole())) {
                filter.setSupervisorId(currentUser.getId());
            } else if (Role.ROLE_SUPPORT.equals(currentUser.getRole())) {
                filter.setManagerId(currentUser.getId());
            }
        }
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordDTO changePasswordDTO) {
        User user = userRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", id));
        String currentEncryptedPassword = user.getPassword();
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), currentEncryptedPassword)) {
            throw validationService.badRequestExceptionThrow(
                    localizationService.localize(MessageConstants.ITEM_INVALID, new Object[]{localizationService.localize(MessageConstants.PASSWORD)})
            ).get();
        }
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);
        cacheService.evictUser(user.getLogin());
    }

    public ResponseEntity<Object> getBonusesList(String token, BonusResponseFilter filter) {
        User currentUser = getCurrentUser();
        if (Role.ROLE_SUPERVISOR.equals(currentUser.getRole())) {
            filter.setManagerId(userRepository.getIdsBySupervisorId(currentUser.getId()).stream().map(Object::toString).toList());
        } else if (Role.ROLE_SUPPORT.equals(currentUser.getRole())) {
            filter.setManagerId(Collections.singletonList(currentUser.getId().toString()));
        }
        return billingServiceFeignClient.getBonusesList(token, filter);
    }

    @Transactional
    public void assignSupervisor(UserSupervisorDTO userSupervisorDTO) {
        userRepository.findById(userSupervisorDTO.getSupervisorId())
                .map(supervisor -> {
                    userSupervisorDTO.getManagerIds()
                            .forEach(managerId -> userRepository.findById(managerId).map(manager -> {
                                manager.setSupervisorId(supervisor.getId());
                                userRepository.save(manager);
                                cacheService.evictUser(manager.getLogin());
                                return manager;
                            }).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", managerId)));
                    return supervisor;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", userSupervisorDTO.getSupervisorId()));
    }

    @Transactional
    public void removeSupervisor(Long managerId) {
        userRepository
                .findById(managerId)
                .map(manager -> {
                    manager.setSupervisorId(null);
                    userRepository.save(manager);
                    cacheService.evictUser(manager.getLogin());
                    return manager;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", managerId));
    }

    public List<BranchCoordinateDTO> getCurrentUserBranches() {
        return userRepository
                .findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "id", SecurityUtils.getCurrentUserId()))
                .getBranches()
                .stream()
                .map(branch -> {
                    BranchCoordinateDTO branchCoordinateDTO = new BranchCoordinateDTO();
                    branchCoordinateDTO.setId(branch.getId());
                    branchCoordinateDTO.setName(branch.getName());
                    branchCoordinateDTO.setLatitude(Optional.ofNullable(branch.getAddress()).map(Address::getLatitude).orElse(null));
                    branchCoordinateDTO.setLongitude(Optional.ofNullable(branch.getAddress()).map(Address::getLongitude).orElse(null));
                    return branchCoordinateDTO;
                }).toList();
    }

    public List<SelectItemDTO> getCurrentUserBranches(Long userId) {
        Long currentBranchId = SecurityUtils.getCurrentBranchId();
        return userRepository.findFirstByIdAndRole(userId, Role.ROLE_SUPERVISOR).map(user -> user.getBranches()
                .stream()
                .map(branch -> new SelectItemDTO(
                        branch.getId(),
                        branch.getName(),
                        null,
                        branch.getId().equals(currentBranchId)
                )).toList()
        ).orElse(List.of());
    }

    @Transactional
    public String getRefreshToken(String username) {
        final String refreshToken = UUID.randomUUID().toString();
        User user = userRepository.findFirstByLoginAndDeletedIsFalse(username).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "username", username));
        user.setRefreshToken(refreshToken);
        user.setRefreshDate(LocalDateTime.now().plusHours(jwtTokenProperties.refreshTokenValidityInHours()));
        userRepository.save(user);
        cacheService.evictUser(user.getLogin());
        return refreshToken;
    }

    public User getUserByRefreshToken(String refreshToken) {
        User user = userRepository.findFirstByRefreshToken(refreshToken).orElseThrow(validationService.entityNotFoundExceptionThrow(User.class.getSimpleName(), "refreshToken", refreshToken));
        if (user.getRefreshDate().isBefore(LocalDateTime.now())) {
            throw validationService.badRequestExceptionThrow("The token has expired").get();

        }
        if (UserStatus.PENDING == user.getStatus()) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.USER_NOT_ACTIVATED, new Object[]{user.getLogin()}), "User not activated").get();
        }
        if (UserStatus.BLOCKED == user.getStatus()) {
            throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.USER_BLOCKED, new Object[]{user.getLogin()}), "User blocked").get();
        }
        if (user.getCompany() != null) {
            if (user.getCompany().isDeleted()) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_DELETED, new Object[]{user.getCompany().getName()}), "Company deleted").get();
            } else if (CompanyStatus.IN_ACTIVE == user.getCompany().getStatus()) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_NOT_ACTIVATED, new Object[]{user.getCompany().getName()}), "Company not activated").get();
            } else if (CompanyStatus.BLOCKED == user.getCompany().getStatus()) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.COMPANY_BLOCKED, new Object[]{user.getCompany().getName()}), "Компания заблокирована").get();
            }
        }
        log.debug("Authenticating user: {}", user.getLogin());
        cacheService.evictUser(user.getLogin());
        return user;
    }


}
