package uz.uzkassa.horeca.web.rest.pub;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.account.AccountCheckDTO;
import uz.uzkassa.horeca.dto.auth.*;
import uz.uzkassa.horeca.security.jwt.TokenProvider;
import uz.uzkassa.horeca.service.AccountService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicAccountRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicAccountResource {

    AuthenticationManagerBuilder authenticationManagerBuilder;
    TokenProvider tokenProvider;
    AccountService accountService;

    @Operation(operationId = "login")
    @PostMapping(ApiConstants.login)
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getPhone(),
                loginDTO.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication, loginDTO.isRememberMe());
        HttpHeaders headers = HeaderUtils.getHeader();
        headers.setBearerAuth(token);
        return new ResponseEntity<>(new TokenDTO(token), headers, HttpStatus.OK);
    }

    @Operation(operationId = "refreshToken")
    @PutMapping("/refresh-token")
    public ResponseEntity<TokenDTO> refreshToken(@RequestBody @Valid RefreshTokenDTO refreshTokenDTO) {
        TokenDTO tokenDTO = accountService.getTokenByRefreshToken(refreshTokenDTO);
        HttpHeaders headers = HeaderUtils.getHeader();
        headers.setBearerAuth(tokenDTO.getAccessToken());
        return new ResponseEntity<>(tokenDTO, headers, HttpStatus.OK);
    }

    @Operation(operationId = "check")
    @PostMapping(ApiConstants.check)
    public ResponseEntity<AccountCheckDTO> check(@Valid @RequestBody PhoneDTO phoneDTO) {
        return ResponseEntity.ok(accountService.check(phoneDTO));
    }

    @Operation(operationId = "verify")
    @PostMapping(ApiConstants.verify)
    public ResponseEntity<SecretKeyDTO> verify(@Valid @RequestBody VerifyDTO verifyDTO) {
        return ResponseEntity.ok(accountService.verify(verifyDTO));
    }

    @Operation(operationId = "resendActivationKey")
    @PostMapping(ApiConstants.resendActivationKey)
    public ResponseEntity<Void> resendActivationKey(@Valid @RequestBody PhoneDTO phoneDTO) {
        accountService.resendActivationCode(phoneDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "resetPasswordInit")
    @PostMapping(ApiConstants.resetPasswordInit)
    public ResponseEntity<Void> resetPasswordInit(@Valid @RequestBody PhoneDTO phoneDTO) {
        accountService.resetPasswordInit(phoneDTO.getPhone());
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "resendResetPasswordKey")
    @PostMapping(ApiConstants.resendResetPasswordKey)
    public ResponseEntity<Void> resendResetPasswordKey(@Valid @RequestBody PhoneDTO phoneDTO) {
        accountService.resetPasswordInit(phoneDTO.getPhone());
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "resetPasswordCheck")
    @PostMapping(ApiConstants.resetPasswordCheck)
    public ResponseEntity<SecretKeyDTO> resetPasswordCheck(@Valid @RequestBody VerifyDTO verifyDTO) {
        return ResponseEntity.ok(accountService.resetPasswordCheck(verifyDTO));
    }

    @Operation(operationId = "resetPasswordFinish")
    @PostMapping(ApiConstants.resetPasswordFinish)
    public ResponseEntity<Void> resetPasswordFinish(@Valid @RequestBody RegisterDTO registerDTO) {
        accountService.resetPasswordFinish(registerDTO);
        return ResponseEntity.ok().build();
    }
}
