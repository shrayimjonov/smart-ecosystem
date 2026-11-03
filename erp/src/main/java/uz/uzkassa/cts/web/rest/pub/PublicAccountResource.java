package uz.uzkassa.cts.web.rest.pub;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.auth.*;
import uz.uzkassa.cts.enums.TemplateType;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.AccountService;
import uz.uzkassa.cts.service.UserAuthService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.publicAccountRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PublicAccountResource {

    AccountService accountService;
    UserAuthService userAuthService;

    @Operation(operationId = "login")
    @PostMapping(ApiConstants.login)
    public ResponseEntity<TokenDTO> login(@RequestBody LoginRememberMeDTO loginRememberMeDTO) {
        String token = accountService.getToken(loginRememberMeDTO);
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBearerAuth(token);
        return new ResponseEntity<>(new TokenDTO(token), headers, HttpStatus.OK);
    }

    @Operation(operationId = "logout")
    @GetMapping(value = ApiConstants.logout, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        accountService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.sendActivationCode)
    public ResponseEntity<Void> sendActivationCode(@RequestBody UsernameDTO usernameDTO) {
        userAuthService.sendActivationCode(usernameDTO, TemplateType.ACTIVATION);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.activate)
    public ResponseEntity<SecretKeyDTO> activate(@RequestBody ActivationDTO activateDTO) {
        return ResponseEntity.ok(userAuthService.activateStart(activateDTO));
    }

    @PostMapping(ApiConstants.activateFinish)
    public ResponseEntity<Void> activateFinish(@RequestBody ActivationFinishDTO activationFinishDTO) {
        userAuthService.activateFinish(activationFinishDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.changePasswordInit)
    public ResponseEntity<SecretKeyDTO> changePasswordInit(@RequestBody UsernameDTO usernameDTO) {
        return ResponseEntity.ok(userAuthService.changePasswordInit(usernameDTO));
    }

    @PostMapping(ApiConstants.changePasswordConfirm)
    public ResponseEntity<Void> changePasswordConfirm(@RequestBody ChangePasswordPublicDTO changePasswordDTO) {
        userAuthService.changePasswordConfirm(changePasswordDTO);
        return ResponseEntity.ok().build();
    }
}
