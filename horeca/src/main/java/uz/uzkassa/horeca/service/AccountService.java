package uz.uzkassa.horeca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.auth.User;
import uz.uzkassa.horeca.domain.auth.UserAuth;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.account.AccountCheckDTO;
import uz.uzkassa.horeca.dto.account.AccountDetailDTO;
import uz.uzkassa.horeca.dto.account.AccountIdDTO;
import uz.uzkassa.horeca.dto.account.AccountProfileDTO;
import uz.uzkassa.horeca.dto.auth.*;
import uz.uzkassa.horeca.dto.sms.SmsRequestDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.LanguageEnum;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.enums.UserStatus;
import uz.uzkassa.horeca.repository.FileUploadRepository;
import uz.uzkassa.horeca.repository.UserAuthRepository;
import uz.uzkassa.horeca.security.jwt.TokenProvider;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AccountService extends BaseService {

    SmsService smsService;
    FileUploadRepository fileUploadRepository;
    PasswordEncoder passwordEncoder;
    AmazonS3Service amazonS3Service;
    UserAuthRepository userAuthRepository;
    UserAuthService userAuthService;
    TokenProvider tokenProvider;

    @Transactional
    public String getRefreshToken(String login) {
        return userRepository.findFirstByLoginAndDeletedIsFalse(login).map(user -> {
            final String refreshToken = UUID.randomUUID().toString();
            userAuthService.create(UserAuthDTO.builder()
                    .login(user.getLogin())
                    .refreshToken(refreshToken)
                    .expiryDate(LocalDateTime.now().plusMonths(3))
                    .build());
            return refreshToken;
        }).orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "login", login));
    }


    public TokenDTO getTokenByRefreshToken(RefreshTokenDTO refreshTokenDTO) {
        UserAuth userAuth = userAuthRepository.findFirstByRefreshTokenOrderByIdDesc(refreshTokenDTO.getRefreshToken()).orElseThrow(notFoundExceptionThrow(UserAuth.class.getSimpleName(), "refreshToken", refreshTokenDTO.getRefreshToken()));
        if (userAuth.getExpiryDate() != null && userAuth.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredJwtException(null, null, "Token expired");
        }

        User user = userRepository.findFirstByLoginAndDeletedIsFalse(userAuth.getLogin()).orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "login", userAuth.getLogin()));
        if (UserStatus.PENDING == user.getStatus()) {
            throw userNotActivationExceptionThrow("User is not activated").get();
        }
        if (UserStatus.IN_ACTIVE == user.getStatus()) {
            throw userNotActivationExceptionThrow("User is inactivated").get();
        }
        if (user.getCompany() != null) {
            if (user.getCompany().isDeleted()) {
                throw userNotActivationExceptionThrow("Your company is deleted. Activate your company to sign in").get();
            }
            if (CommonStatus.IN_ACTIVE == user.getCompany().getStatus()) {
                throw userNotActivationExceptionThrow("Your company is in active. Activate your company to sign in").get();
            }
        }

        return new TokenDTO(tokenProvider.createToken(user), userAuth.getRefreshToken());
    }

    public List<String> getRoles() {
        return Arrays.stream(Role.values()).map(Role::getCode).collect(Collectors.toList());
    }

    @Transactional
    public AccountCheckDTO check(PhoneDTO phoneDTO) {
        User user = findOneByLogin(phoneDTO.getPhone()).orElse(null);
        if (user == null || UserStatus.PENDING == user.getStatus()) {
            final String activationCode = SecurityUtils.generateActivationCode();
            smsService.send(SmsRequestDTO.builder().phone(phoneDTO.getPhone()).code(activationCode).build());
            userAuthService.create(UserAuthDTO.builder().login(phoneDTO.getPhone()).activationCode(activationCode).build());
        }
        return new AccountCheckDTO(user != null, user != null ? user.getStatus() : null);
    }

    public Optional<User> findOneByLogin(String login) {
        return userRepository.findFirstByLoginAndDeletedIsFalse(login);
    }

    @Transactional
    public SecretKeyDTO verify(VerifyDTO verifyDTO) {
        userAuthRepository
                .findFirstByLoginAndActivationCodeOrderByIdDesc(verifyDTO.getPhone(), verifyDTO.getActivationKey())
                .map(userAuth -> {
                    if (userAuth.getExpiryDate().isBefore(LocalDateTime.now())) {
                        throw badRequestExceptionThrow("Activation code is expired").get();
                    }
                    return userAuth;
                })
                .orElseThrow(
                        badRequestExceptionThrow("Incorrect activation code")
                );

        final String secretKey = UUID.randomUUID().toString();
        userAuthService.create(UserAuthDTO.builder().login(verifyDTO.getPhone()).secretKey(secretKey).build());
        return new SecretKeyDTO(secretKey);
    }

    private void clearUserCache(User user) {
        cacheService.clearUserCache(user.getLogin());
    }

    @Transactional
    public void resendActivationCode(PhoneDTO phoneDTO) {
        final String activationCode = SecurityUtils.generateActivationCode();
        smsService.send(SmsRequestDTO.builder().phone(phoneDTO.getPhone()).code(activationCode).build());
        userAuthService.create(UserAuthDTO.builder().login(phoneDTO.getPhone()).activationCode(activationCode).build());
    }

    @Transactional
    public void resetPasswordInit(String phone) {
        userRepository
                .findFirstByLoginAndDeletedIsFalse(phone)
                .ifPresentOrElse(
                        user -> {
                            final String activationCode = SecurityUtils.generateActivationCode();
                            smsService.send(SmsRequestDTO.builder().phone(phone).code(activationCode).build());
                            userAuthService.create(UserAuthDTO.builder().login(phone).activationCode(activationCode).build());
                        },
                        () -> notFoundExceptionThrow("User not found").get()
                );
    }

    @Transactional
    public SecretKeyDTO resetPasswordCheck(VerifyDTO verifyDTO) {
        return userRepository.findFirstByLoginAndDeletedIsFalse(verifyDTO.getPhone()).map(user -> {
            UserAuth userAuth = userAuthRepository.findFirstByLoginAndActivationCodeOrderByIdDesc(user.getLogin(), verifyDTO.getActivationKey()).orElseThrow(() -> badRequestExceptionThrow("Invalid activation code").get());
            if (userAuth.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw badRequestExceptionThrow("Activation code is expired").get();
            }
            final String secretKey = UUID.randomUUID().toString();
            userAuthService.create(UserAuthDTO.builder().login(verifyDTO.getPhone()).secretKey(secretKey).build());
            return new SecretKeyDTO(secretKey);
        }).orElseThrow(notFoundExceptionThrow("User not found"));
    }

    @Transactional
    public void resetPasswordFinish(RegisterDTO registerDTO) {
        userRepository.findFirstByLoginAndDeletedIsFalse(registerDTO.getPhone()).map(user -> {
            if (!userAuthRepository.existsByLoginAndSecretKey(user.getLogin(), registerDTO.getSecretKey())) {
                throw badRequestExceptionThrow("Incorrect secret key").get();
            }
            user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            userRepository.save(user);
            clearUserCache(user);
            userAuthRepository.deleteAllByLogin(user.getLogin());
            return user;
        }).orElseThrow(notFoundExceptionThrow("User not found"));
    }

    @Transactional
    public SecretKeyDTO changePasswordInit() {
        User user = this.getCurrentUser();
        final String activationCode = SecurityUtils.generateActivationCode();
        final String secretKey = SecurityUtils.generateSecretKey();
        smsService.send(SmsRequestDTO.builder().phone(user.getPhone()).code(activationCode).build());
        userAuthService.create(UserAuthDTO.builder().login(user.getPhone()).activationCode(activationCode).build());
        userAuthService.create(UserAuthDTO.builder().login(user.getPhone()).secretKey(secretKey).build());
        return new SecretKeyDTO(secretKey);
    }

    @Transactional
    public void changePasswordConfirm(ChangePasswordDTO changePasswordDTO) {
        User user = this.getCurrentUser();
        if (!Objects.equals(changePasswordDTO.getPassword(), changePasswordDTO.getPasswordConfirmation())) {
            throw badRequestExceptionThrow(localizationService.localize("user.password.dont.match")).get();
        }
        if (
                userAuthRepository
                        .findFirstByLoginAndActivationCodeOrderByIdDesc(user.getLogin(), changePasswordDTO.getActivationKey())
                        .isEmpty()
        ) {
            throw badRequestExceptionThrow("Invalid activation code").get();
        }
        if (!userAuthRepository.existsByLoginAndSecretKey(user.getLogin(), changePasswordDTO.getSecretKey())) {
            throw badRequestExceptionThrow("Incorrect secret key").get();
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getPassword()));
        userRepository.save(user);
        this.clearUserCache(user);
        userAuthRepository.deleteAllByLogin(user.getLogin());
    }

    @Transactional
    public SecretKeyDTO changeLoginInit(PhoneDTO phoneDTO) {
        if (findOneByLogin(phoneDTO.getPhone()).isPresent()) {
            throw conflictExceptionThrow(localizationService.localize("user.already.exists.by.phone", new Object[]{phoneDTO.getPhone()}))
                    .get();
        }
        User user = this.getCurrentUser();
        final String activationCode = SecurityUtils.generateActivationCode();
        final String secretKey = SecurityUtils.generateSecretKey();
        smsService.send(SmsRequestDTO.builder().phone(user.getPhone()).code(activationCode).build());
        userAuthService.create(UserAuthDTO.builder().login(user.getPhone()).activationCode(activationCode).build());
        userAuthService.create(UserAuthDTO.builder().login(user.getPhone()).secretKey(secretKey).build());
        return new SecretKeyDTO(secretKey);
    }

    @Transactional
    public void changeLoginConfirm(ChangeLoginConfirmDTO changeLoginConfirmDTO) {
        User user = this.getCurrentUser();
        String oldLogin = user.getLogin();
        if (
                userAuthRepository
                        .findFirstByLoginAndActivationCodeOrderByIdDesc(user.getLogin(), changeLoginConfirmDTO.getActivationKey())
                        .isEmpty()
        ) {
            throw badRequestExceptionThrow("Invalid activation code").get();
        }
        if (!userAuthRepository.existsByLoginAndSecretKey(user.getLogin(), changeLoginConfirmDTO.getSecretKey())) {
            throw badRequestExceptionThrow("Incorrect secret key").get();
        }
        user.setLogin(changeLoginConfirmDTO.getPhone());
        userRepository.save(user);
        this.clearUserCache(user);
        cacheService.clearUserCache(oldLogin);
        userAuthRepository.deleteAllByLogin(changeLoginConfirmDTO.getPhone());
    }

    @Transactional
    public void uploadProfilePhoto(MultipartFile photo) {
        amazonS3Service.validateFile(photo);
        User user = this.getCurrentUser();
        if (user.getPhotoId() != null) {
            amazonS3Service.delete(user.getPhotoId());
        }
        FileUpload profilePhoto = amazonS3Service.uploadUserProfilePhoto(photo, user.getId());
        user.setPhoto(profilePhoto);
        user.setPhotoId(profilePhoto.getId());
        userRepository.save(user);
        this.clearUserCache(user);
    }

    @Transactional
    public void deleteProfilePhoto() {
        User user = this.getCurrentUser();
        if (user.getPhotoId() != null) {
            amazonS3Service.delete(user.getPhotoId());
            user.setPhoto(null);
            user.setPhotoId(null);
            userRepository.save(user);
            this.clearUserCache(user);
        }
    }

    public AccountDetailDTO getProfile() {
        if (!HeaderUtils.isAuthenticated()) {
            throw unauthorizedExceptionThrow().get();
        }
        User user = this.getCurrentUser();
        AccountDetailDTO accountDetailDTO = new AccountDetailDTO();
        accountDetailDTO.setId(user.getId());
        accountDetailDTO.setPhone(user.getLogin());
        accountDetailDTO.setFirstName(user.getFirstName());
        accountDetailDTO.setLastName(user.getLastName());
        accountDetailDTO.setPatronymic(user.getPatronymic());
        if (user.getCompany() != null) {
            accountDetailDTO.setCompany(toCompanyBaseDTO(user.getCompany()));
        }
        if (user.getBranch() != null) {
            accountDetailDTO.setBranch(user.getBranch().toCommonDTO());
        }
        if (user.getDepartment() != null) {
            accountDetailDTO.setDepartment(user.getDepartment().toCommonDTO());
        }
        accountDetailDTO.setRole(user.getRole());
        accountDetailDTO.setJoiningDate(user.getJoiningDate());
        accountDetailDTO.setBirthDate(user.getBirthDate());
        if (user.getPhotoId() != null) {
            FileUpload fileUpload = fileUploadRepository
                    .findById(user.getPhotoId())
                    .orElseThrow(notFoundExceptionThrow(FileUpload.class.getSimpleName(), "id", user.getPhotoId()));
            accountDetailDTO.setProfileImage(fileUpload.toFileDTO());
        }
        return accountDetailDTO;
    }

    private Set<String> getCompanyBillingPermissions(Company company) {
        LocalDateTime endTimeOfDay = LocalDateTime.now();
        Set<String> permissions = new LinkedHashSet<>();
        if (company.isFreeTrial()) {
            permissions.add("FREE_TRIAL");
        } else {
            Hibernate.initialize(company.getPermissions());
            Map<String, LocalDateTime> companyPermissions = company.getPermissions();
            for (String serviceType : companyPermissions.keySet()) {
                if (companyPermissions.get(serviceType).isAfter(endTimeOfDay)) {
                    permissions.add(serviceType);
                }
            }
        }
        return permissions;
    }

    public AccountIdDTO getActiveClient(String phone) {
        return userRepository
                .findFirstByLoginAndDeletedIsFalse(phone).map(user -> new AccountIdDTO(user.getId()))
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "login", phone));
    }

    @Transactional
    public Long updateProfile(AccountProfileDTO accountProfileDTO) {
        try {
            log.info("============= Update Profile ================ :{}", objectMapper.writeValueAsString(accountProfileDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        User user = this.getCurrentUser();
        user.setFirstName(accountProfileDTO.getFirstName());
        user.setLastName(accountProfileDTO.getLastName());
        user.setPatronymic(accountProfileDTO.getPatronymic());
        userRepository.save(user);
        this.clearUserCache(user);

        return user.getId();
    }

    @Transactional
    public void updateLanguage(String code) {
        User user = this.getCurrentUser();
        user.setLanguage(LanguageEnum.get(code));
        userRepository.save(user);
        cacheService.clearUserCache(user.getLogin());
    }

    @Transactional
    public void delete(String phone) {
        userRepository
                .findFirstByLoginAndDeletedIsFalse(phone).map(user -> {
                    user.setDeleted(true);
                    user.setStatus(UserStatus.IN_ACTIVE);
                    userRepository.save(user);
                    cacheService.clearUserCache(user.getLogin());
                    return user;
                })
                .orElseThrow(notFoundExceptionThrow(User.class.getSimpleName(), "login", phone));
    }
}
