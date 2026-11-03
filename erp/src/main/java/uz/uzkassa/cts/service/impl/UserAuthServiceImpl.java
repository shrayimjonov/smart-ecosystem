package uz.uzkassa.cts.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.UserAuth;
import uz.uzkassa.cts.dto.auth.*;
import uz.uzkassa.cts.dto.email.EmailDTO;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.TemplateType;
import uz.uzkassa.cts.repository.UserAuthRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.Utils;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@Service
public class UserAuthServiceImpl extends BaseServiceImpl<UserAuth, Long> implements UserAuthService {

    private final UserAuthRepository userAuthRepository;
    private final UserService userService;
    private final SmsService smsService;
    private final AccountService accountService;
    private final TemplateService templateService;
    private final EmailService emailService;
    private final ApplicationProperties applicationProperties;

    public UserAuthServiceImpl(UserAuthRepository userAuthRepository, UserService userService, SmsService smsService, AccountService accountService, TemplateService templateService, EmailService emailService, ApplicationProperties applicationProperties) {
        super(userAuthRepository);
        this.userAuthRepository = userAuthRepository;
        this.userService = userService;
        this.smsService = smsService;
        this.accountService = accountService;
        this.templateService = templateService;
        this.emailService = emailService;
        this.applicationProperties = applicationProperties;
    }

    @Transactional
    public void create(String username, String activationCode, String secretKey) {
        UserAuth userAuth = new UserAuth();
        userAuth.setUsername(username);
        userAuth.setActivationCode(activationCode);
        userAuth.setSecretKey(secretKey);
        userAuthRepository.save(userAuth);
    }

    @Transactional
    public SecretKeyDTO activateStart(ActivationDTO activateDTO) {
        if (StringUtils.isEmpty(activateDTO.getUsername())) {
            throw validationService.requiredExceptionThrow(USERNAME).get();
        }
        if (StringUtils.isEmpty(activateDTO.getActivationCode())) {
            throw validationService.requiredExceptionThrow(CODE_ACTIVATION).get();
        }
        return userAuthRepository.findFirstByUsername(activateDTO.getUsername()).map(userAuth -> {
            if (!activateDTO.getActivationCode().equals(userAuth.getActivationCode())) {
                throw validationService.badRequestExceptionThrow(INCORRECT_ACTIVATION_CODE).get();
            }
            if (LocalDateTime.now().isAfter(userAuth.getExpiryDate())) {
                throw validationService.badRequestExceptionThrow(ACTIVATION_CODE_EXPIRED).get();
            }
            userAuth.setSecretKey(SecurityUtils.generateSecretKey());
            userAuthRepository.save(userAuth);
            return new SecretKeyDTO(userAuth.getSecretKey());
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(USER_AUTH));
    }

    @Transactional
    public void activateFinish(ActivationFinishDTO activationFinishDTO) {
        if (StringUtils.isEmpty(activationFinishDTO.getUsername())) {
            throw validationService.requiredExceptionThrow(USERNAME).get();
        }
        if (StringUtils.isEmpty(activationFinishDTO.getPassword())) {
            throw validationService.requiredExceptionThrow(PASSPORT).get();
        }
        if (StringUtils.isEmpty(activationFinishDTO.getConfirmPassword())) {
            throw validationService.requiredExceptionThrow(PASSPORT).get();
        }
        if (StringUtils.isEmpty(activationFinishDTO.getSecretKey())) {
            throw validationService.requiredExceptionThrow(SECRET_KEY).get();
        }
        if (!activationFinishDTO.getPassword().matches(activationFinishDTO.getConfirmPassword())) {
            throw validationService.badRequestExceptionThrow(PASSWORD_NOT_MATCHED).get();
        }
        userAuthRepository.findFirstByUsername(activationFinishDTO.getUsername().toLowerCase()).map(userAuth -> {
            if (!activationFinishDTO.getSecretKey().equals(userAuth.getSecretKey())) {
                throw validationService.badRequestExceptionThrow(INVALID_SECRET_KEY).get();
            }
            userService.activate(activationFinishDTO);
            return userAuth;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(USER_AUTH));
    }

    @Override
    @Transactional
    public void sendActivationCode(UsernameDTO usernameDTO, TemplateType templateType) {
        if (StringUtils.isEmpty(usernameDTO.getUsername())) {
            throw validationService.requiredExceptionThrow(USERNAME).get();
        }
        final String activationCode = SecurityUtils.generateActivationCode();
        ChannelType channelType = Utils.isValidEmail(usernameDTO.getUsername()) ? ChannelType.EMAIL : ChannelType.SMS;
        String message = templateService.getActivationMessage(activationCode, channelType, templateType);
        boolean isSent;
        if (ChannelType.EMAIL == channelType) {
            String subject = templateService.getSubject(TemplateType.ACTIVATION);
            isSent = emailService.send(EmailDTO
                    .builder()
                    .from("user@example.com")
                    .to(usernameDTO.getUsername())
                    .subject(subject)
                    .message(message)
                    .build());
        } else {
            isSent = smsService.send(new EtcRequestDTO(usernameDTO.getUsername(), message));
        }
        if (!isSent) {
            throw validationService.badRequestExceptionThrow((ChannelType.EMAIL == channelType ? "Email" : "SMS").concat(" provider is not working")).get();
        }
        UserAuth userAuth = userAuthRepository.findFirstByUsername(usernameDTO.getUsername()).orElse(new UserAuth());
        userAuth.setUsername(usernameDTO.getUsername());
        userAuth.setActivationCode(activationCode);
        userAuth.setExpiryDate(LocalDateTime.now().plusMinutes(2));
        userAuthRepository.save(userAuth);
    }

    @Override
    @Transactional
    public SecretKeyDTO changePasswordInit(UsernameDTO usernameDTO) {
        if (StringUtils.isEmpty(usernameDTO.getUsername())) {
            throw validationService.requiredExceptionThrow(USERNAME).get();
        }
        User user = userRepository.findOneWithAuthoritiesByLoginAndDeletedIsFalse(usernameDTO.getUsername()).orElseThrow(validationService.entityNotFoundExceptionThrow(usernameDTO.getUsername()));
        return this.changePasswordInit(user);
    }

    @Override
    @Transactional
    public SecretKeyDTO changePasswordInit() {
        return this.changePasswordInit(getCurrentUserDomain());
    }

    @Override
    @Transactional
    public SecretKeyDTO changePasswordInit(User user) {
        final String activationCode = SecurityUtils.generateActivationCode();
        final String secretKey = SecurityUtils.generateSecretKey();
        boolean isSent;
        ChannelType channelType;
        if (Utils.isValidPhone(user.getLogin()) || Utils.isValidPhone(user.getPhone())) {
            String message = templateService.getChangePasswordMessage(user, activationCode, ChannelType.SMS);
            String to = Utils.isValidPhone(user.getPhone()) ? user.getPhone() : user.getLogin();
            isSent = smsService.send(new EtcRequestDTO(to, message));
            channelType = ChannelType.SMS;
        } else if (Utils.isValidEmail(user.getLogin()) || Utils.isValidEmail(user.getEmail())) {
            String message = templateService.getChangePasswordMessage(user, activationCode, ChannelType.EMAIL);
            String subject = templateService.getSubject(TemplateType.CHANGE_PASSWORD);
            String to = Utils.isValidEmail(user.getEmail()) ? user.getEmail() : user.getLogin();
            isSent = emailService.send(EmailDTO
                    .builder()
                    .from(applicationProperties.getEmailProperty().getFrom())
                    .to(to)
                    .subject(subject)
                    .message(message)
                    .build());
            channelType = ChannelType.EMAIL;
        } else {
            throw validationService.badRequestExceptionThrow(INVALID_EMAIL_OR_PHONE).get();
        }
        if (!isSent) {
            throw validationService.badRequestExceptionThrow(OTP_SENDING_ERROR, channelType.getCode()).get();
        }

        this.create(user.getLogin(), activationCode, null);
        this.create(user.getLogin(), null, secretKey);
        return new SecretKeyDTO(secretKey);
    }

    @Override
    @Transactional
    public void changePasswordConfirm(ChangePasswordPublicDTO changePasswordDTO) {
        if (StringUtils.isEmpty(changePasswordDTO.getUsername())) {
            throw validationService.requiredExceptionThrow(USERNAME).get();
        }
        User user = userRepository.findOneWithAuthoritiesByLoginAndDeletedIsFalse(changePasswordDTO.getUsername()).orElseThrow(validationService.entityNotFoundExceptionThrow(changePasswordDTO.getUsername()));
        this.changePasswordConfirm(user, changePasswordDTO);
    }

    @Override
    @Transactional
    public void changePasswordConfirm(ChangePasswordDTO changePasswordDTO) {
        this.changePasswordConfirm(this.getCurrentUserDomain(), changePasswordDTO);
    }

    @Override
    @Transactional
    public void changePasswordConfirm(User user, ChangePasswordDTO changePasswordDTO) {
        if (StringUtils.isEmpty(changePasswordDTO.getActivationCode())) {
            throw validationService.requiredExceptionThrow(CODE_ACTIVATION).get();
        }
        if (StringUtils.isEmpty(changePasswordDTO.getSecretKey())) {
            throw validationService.requiredExceptionThrow(SECRET_KEY).get();
        }
        if (userAuthRepository.findFirstByUsernameAndActivationCodeOrderByIdDesc(user.getLogin(), changePasswordDTO.getActivationCode()).isEmpty()) {
            throw validationService.badRequestExceptionThrow(INCORRECT_ACTIVATION_CODE).get();
        }
        if (!userAuthRepository.existsByUsernameAndSecretKey(user.getLogin(), changePasswordDTO.getSecretKey())) {
            throw validationService.badRequestExceptionThrow(INVALID_SECRET_KEY).get();
        }
        userService.changePassword(user, changePasswordDTO);
        userAuthRepository.deleteAllByUsername(user.getLogin());
    }

    @Transactional
    public SecretKeyDTO changeUsernameInit(User user, UsernameDTO usernameDTO) {
        if (userRepository.findOneWithAuthoritiesByLoginAndDeletedIsFalse(usernameDTO.getUsername()).isPresent()) {
            throw validationService.entityAlreadyExistExceptionThrow(USER, USERNAME, usernameDTO.getUsername()).get();
        }
        if (user == null) {
            user = getCurrentUserDomain();
        }
        final String activationCode = SecurityUtils.generateActivationCode();
        final String secretKey = SecurityUtils.generateSecretKey();

        boolean isSent;
        ChannelType channelType;
        if (Utils.isValidPhone(user.getLogin()) || Utils.isValidPhone(user.getPhone())) {
            String message = templateService.getChangeUsernameMessage(user, activationCode, ChannelType.SMS);
            String to = Utils.isValidPhone(user.getPhone()) ? user.getPhone() : user.getLogin();
            isSent = smsService.send(new EtcRequestDTO(to, message));
            channelType = ChannelType.SMS;
        } else if (Utils.isValidEmail(user.getLogin()) || Utils.isValidEmail(user.getEmail())) {
            String message = templateService.getChangeUsernameMessage(user, activationCode, ChannelType.EMAIL);
            String subject = templateService.getSubject(TemplateType.CHANGE_PASSWORD);
            String to = Utils.isValidEmail(user.getEmail()) ? user.getEmail() : user.getLogin();
            isSent = emailService.send(EmailDTO
                    .builder()
                    .from(applicationProperties.getEmailProperty().getFrom())
                    .to(to)
                    .subject(subject)
                    .message(message)
                    .build());
            channelType = ChannelType.EMAIL;
        } else {
            throw validationService.badRequestExceptionThrow(INVALID_EMAIL_OR_PHONE).get();
        }
        if (!isSent) {
            throw validationService.badRequestExceptionThrow(OTP_SENDING_ERROR, channelType.getCode()).get();
        }

        this.create(user.getLogin(), activationCode, null);
        this.create(user.getLogin(), null, secretKey);
        return new SecretKeyDTO(secretKey);
    }

    @Transactional
    public void changeUsernameConfirm(User user, ChangeUsernameConfirmDTO changeUsernameConfirmDTO, HttpServletRequest request, HttpServletResponse response) {
        if (user == null) {
            user = getCurrentUserDomain();
        }
        if (userAuthRepository.findFirstByUsernameAndActivationCodeOrderByIdDesc(user.getLogin(), changeUsernameConfirmDTO.getActivationCode()).isEmpty()) {
            throw validationService.badRequestExceptionThrow(INCORRECT_ACTIVATION_CODE).get();
        }
        if (!userAuthRepository.existsByUsernameAndSecretKey(user.getLogin(), changeUsernameConfirmDTO.getSecretKey())) {
            throw validationService.badRequestExceptionThrow(INVALID_SECRET_KEY).get();
        }
        userService.changeUsername(user, changeUsernameConfirmDTO);
        userAuthRepository.deleteAllByUsername(changeUsernameConfirmDTO.getUsername());
        if (request != null && response != null) {
            accountService.logout(request, response);
        }
    }
}
