package uz.uzkassa.smartpos.web.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.FullNameDTO;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.integration.apay.FileDTO;
import uz.uzkassa.smartpos.service.EtcSmsService;
import uz.uzkassa.smartpos.service.LocalizationService;
import uz.uzkassa.smartpos.service.UserService;
import uz.uzkassa.smartpos.utils.EncryptionUtils;
import uz.uzkassa.smartpos.web.rest.vm.KeyAndPasswordVM;
import uz.uzkassa.smartpos.web.rest.vm.LoginChangeVM;
import uz.uzkassa.smartpos.web.rest.vm.PasswordChangeVM;
import uz.uzkassa.smartpos.web.rest.vm.RegisterVM;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountResource {
    private final UserService userService;
    private final EtcSmsService smsService;

    private final LocalizationService localizationService;

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public UserDTO getAccount() {
        return userService.getUserWithAuthorities();
    }

    /**
     * {@code POST  /register-sms} : register the user using sms. Provide 12 digit phone number in login field
     *
     * @param registerVM the register user View Model.
     */
    @PostMapping("/register-sms")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerWithSms(@Valid @RequestBody RegisterVM registerVM) {
        if (!EncryptionUtils.checkLogin(registerVM.getLogin())) {
            throw new BadRequestException(localizationService.getMessage("invalid.phone"));
        }

        User user = userService.registerOwner(registerVM);
        smsService.sendSms(new EtcRequestDto(user.getLogin(), StringUtils.join(Constants.COMMON_SMS, user.getActivationKey())));
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param param Specify login, sms code key, password
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @PostMapping("/activate-sms")
    public void activateAccountSms(@RequestBody KeyAndPasswordVM param) {
        userService.activateRegistrationSms(param.getLogin(), param.getKey(), param.getNewPassword());
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeVM current and new password.
     */
    @PostMapping("/account/change-password")
    public void changePassword(@RequestBody PasswordChangeVM passwordChangeVM) {
        if (!EncryptionUtils.checkPasswordLength(passwordChangeVM.getNewPassword())) {
            throw new BadRequestException(localizationService.getMessage("invalid.password"));
        }
        userService.changePassword(passwordChangeVM.getCurrentPassword(), passwordChangeVM.getNewPassword());
    }


    @PostMapping("/account/change-login")
    public void changeLogin(@RequestBody @Valid LoginChangeVM loginChangeVM) {
        userService.changeLogin(loginChangeVM.getCurrentLogin(), loginChangeVM.getNewLogin());
    }


    @PostMapping("/account/change/name")
    public void changeName(@RequestBody @Valid FullNameDTO changeName) {
        userService.changeName(changeName);
    }

    @GetMapping("/login/check")
    public boolean checkLogin(@RequestParam String login) {
        return userService.checkLogin(login);
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an sms to reset the password of the user.
     *
     * @param phone the mail of the user.
     * @throws UsernameNotFoundException {@code 400 (Bad Request)} if the phone is not registered.
     */

    @PostMapping("/account/reset-password/init")
    public void requestPasswordReset(@RequestBody @Valid String phone) {
        User user = userService.requestPasswordResetSms(phone);
        smsService.sendSms(new EtcRequestDto(user.getLogin(), StringUtils.join(Constants.COMMON_SMS, user.getResetKey())));
    }

    @PostMapping("/account/reset-password/check")
    public boolean checkResetKey(@RequestBody @Valid KeyAndPasswordVM keyAndPassword) {
        return userService.checkResetKey(keyAndPassword);
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping("/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody @Valid KeyAndPasswordVM keyAndPassword) {
        if (!EncryptionUtils.checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new BadRequestException(localizationService.getMessage("invalid.password"));
        }
        userService.completePasswordResetSms(keyAndPassword.getNewPassword(), keyAndPassword.getKey(), keyAndPassword.getLogin());
    }

    @PostMapping("/account/reset-pin/cashier")
    public void resetPinCashier(@RequestBody @Valid KeyAndPasswordVM resetVM) {
        userService.resetPin(resetVM.getLogin());
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadUserImage(@RequestParam Long userId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadUserImage(userId, file));
    }

    @DeleteMapping("/remove-image")
    public ResponseEntity<Void> removeUserImage(@RequestParam(required = false) Long userId) {
        userService.removeUserImage(userId);
        return ResponseEntity.ok().build();
    }

}
