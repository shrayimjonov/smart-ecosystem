package uz.uzkassa.smartpossupply.web.rest.pub;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.*;
import uz.uzkassa.smartpossupply.dto.user.SecretKeyDTO;
import uz.uzkassa.smartpossupply.security.jwt.TokenProvider;
import uz.uzkassa.smartpossupply.service.LocalizationService;
import uz.uzkassa.smartpossupply.service.UserService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.publicAccountRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicAccountResource {

    UserService userService;
    TokenProvider tokenProvider;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    LocalizationService localizationService;

    @Operation(summary = "Forgot Password Init", operationId ="forgotPasswordInit")
    @PostMapping(ApiConstants.forgotPasswordInit)
    public ResponseEntity<Void> forgotPasswordInit(@Valid @RequestBody PhoneDTO phoneDTO) {
        userService.forgotPasswordInit(phoneDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Forgot Password Check", operationId ="forgotPasswordCheck")
    @PostMapping(ApiConstants.forgotPasswordCheck)
    public ResponseEntity<SecretKeyDTO> forgotPasswordCheck(@Valid @RequestBody ActivationDTO activateDTO) {
        return ResponseEntity.ok(userService.forgotPasswordCheck(activateDTO));
    }

    @Operation(summary = "Forgot Password Finish", operationId ="forgotPasswordFinish")
    @PostMapping(ApiConstants.forgotPasswordFinish)
    public ResponseEntity<Void> forgotPasswordFinish(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        userService.forgotPasswordFinish(forgotPasswordDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Resend Activation Key", operationId ="resendActivationKey")
    @PostMapping(ApiConstants.reSendActivationKey)
    public ResponseEntity<Void> resendActivationKey(@Valid @RequestBody PhoneDTO phoneDTO) {
        userService.resendActivationKey(phoneDTO.getPhone());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Login)
    @PostMapping(ApiConstants.login)
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody AdminLoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(),
                loginDTO.getPassword()
        );
        Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new BadRequestException(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.INCORRECT_USERNAME_OR_PASSWORD));
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication, true);
        HttpHeaders httpHeaders = SecurityUtils.getHeader();
        httpHeaders.setBearerAuth(token);
        return new ResponseEntity<>(new TokenDTO(token), httpHeaders, HttpStatus.OK);
    }
}
