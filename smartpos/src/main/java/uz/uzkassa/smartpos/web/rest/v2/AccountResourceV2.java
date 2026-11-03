package uz.uzkassa.smartpos.web.rest.v2;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.dto.etcsms.request.EtcRequestDto;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.security.CustomUser;
import uz.uzkassa.smartpos.security.UserNotActivatedException;
import uz.uzkassa.smartpos.security.jwt.JWTFilter;
import uz.uzkassa.smartpos.security.jwt.TokenProvider;
import uz.uzkassa.smartpos.service.EtcSmsService;
import uz.uzkassa.smartpos.service.UserService;
import uz.uzkassa.smartpos.utils.EncryptionUtils;
import uz.uzkassa.smartpos.web.rest.vm.KeyAndPasswordVM;
import uz.uzkassa.smartpos.web.rest.vm.PasswordChangeVM;
import uz.uzkassa.smartpos.web.rest.vm.RegisterVM;
import uz.uzkassa.smartpos.web.rest.vm.TokenDTO;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AccountResourceV2 {
    private final UserService userService;
    private final EtcSmsService smsService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterVM registerVM) {
        if (!EncryptionUtils.checkLogin(registerVM.getLogin())) {
            throw new BadRequestException("Invalid phone");
        }

        User user = userService.registerOwner(registerVM);
        smsService.sendSms(new EtcRequestDto(user.getLogin(), StringUtils.join(Constants.COMMON_SMS, user.getActivationKey())));
        smsService.sendSms(new EtcRequestDto(user.getLogin(), Constants.PUBLIC_OFFER));
    }

    @PostMapping("/activate")
    public ResponseEntity<TokenDTO> activate(@RequestBody KeyAndPasswordVM param) {
        String refreshToken = userService.activate(param.getLogin(), param.getKey(), param.getNewPassword());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(param.getLogin(), param.getNewPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.getPrincipal() instanceof CustomUser customUser) {
            String access_token = tokenProvider.createToken(customUser, false);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + access_token);
            return new ResponseEntity<>(new TokenDTO(access_token, refreshToken), httpHeaders, HttpStatus.OK);
        } else {
            throw new UserNotActivatedException("User " + param.getLogin() + " was not activated");
        }
    }

    @PutMapping(path = "/change-password")
    public void changePassword(@RequestBody PasswordChangeVM passwordChangeVM) {
        if (!EncryptionUtils.checkPasswordLength(passwordChangeVM.getNewPassword())) {
            throw new BadRequestException("Invalid password");
        }
        userService.changePassword(passwordChangeVM.getCurrentPassword(), passwordChangeVM.getNewPassword());
    }

    @PostMapping(path = "/forgot-password")
    public void forgotPassword(@RequestBody @Valid String phone) {
        User user = userService.requestPasswordResetSms(phone);
        smsService.sendSms(new EtcRequestDto(user.getLogin(), StringUtils.join(Constants.COMMON_SMS, user.getResetKey())));
    }

    @PostMapping(path = "/reset-password")
    public void resetPassword(@RequestBody @Valid KeyAndPasswordVM keyAndPassword) {
        if (!EncryptionUtils.checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new BadRequestException("Invalid password");
        }
        userService.completePasswordResetSms(keyAndPassword.getNewPassword(), keyAndPassword.getKey(), keyAndPassword.getLogin());
    }

    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrent());
    }
}
