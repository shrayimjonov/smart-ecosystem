package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.account.AccountCheckDTO;
import uz.uzkassa.horeca.dto.account.AccountDetailDTO;
import uz.uzkassa.horeca.dto.account.AccountProfileDTO;
import uz.uzkassa.horeca.dto.auth.*;
import uz.uzkassa.horeca.security.jwt.TokenProvider;
import uz.uzkassa.horeca.service.AccountService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.02.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileAccountRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileAccountResource {

    AccountService accountService;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    TokenProvider tokenProvider;

    @Operation(operationId = "changePasswordInit")
    @GetMapping(ApiConstants.changePasswordInit)
    public ResponseEntity<SecretKeyDTO> changePasswordInit() {
        return ResponseEntity.ok(accountService.changePasswordInit());
    }

    @Operation(operationId = "changePasswordConfirm")
    @PostMapping(ApiConstants.changePasswordConfirm)
    public ResponseEntity<Void> changePasswordConfirm(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        accountService.changePasswordConfirm(changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "changeLoginInit")
    @PutMapping(ApiConstants.changeLoginInit)
    public ResponseEntity<SecretKeyDTO> changeLoginInit(@RequestBody PhoneDTO phoneDTO) {
        return ResponseEntity.ok(accountService.changeLoginInit(phoneDTO));
    }

    @Operation(operationId = "changeLoginConfirm")
    @PutMapping(ApiConstants.changeLoginConfirm)
    public ResponseEntity<Void> changeLoginConfirm(
            @RequestBody ChangeLoginConfirmDTO changeLoginConfirmDTO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        accountService.changeLoginConfirm(changeLoginConfirmDTO);
        this.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "uploadProfilePhoto")
    @PostMapping(path = "/upload/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfilePhoto(@RequestPart("file") MultipartFile file) {
        accountService.uploadProfilePhoto(file);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "deleteProfilePhoto")
    @DeleteMapping("/profile-photo")
    public ResponseEntity<Void> deleteProfilePhoto() {
        accountService.deleteProfilePhoto();
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "updateProfile")
    @PutMapping(ApiConstants.profile)
    public ResponseEntity<Long> updateProfile(@RequestBody AccountProfileDTO accountProfileDTO) {
        return ResponseEntity.ok(accountService.updateProfile(accountProfileDTO));
    }

    @Operation(operationId = "getProfile")
    @GetMapping(ApiConstants.profile)
    public ResponseEntity<AccountDetailDTO> getProfile() {
        return ResponseEntity.ok(accountService.getProfile());
    }

    @Operation(operationId = "updateLanguage")
    @PutMapping("/language/{code}")
    public ResponseEntity<Void> updateLanguage(@PathVariable String code) {
        accountService.updateLanguage(code);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "logOut")
    @GetMapping(value = ApiConstants.logout, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return ResponseEntity.ok().build();
    }

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

    @Operation(operationId = "check")
    @PostMapping(ApiConstants.check)
    public ResponseEntity<AccountCheckDTO> check(@Valid @RequestBody PhoneDTO phoneDTO) {
        return ResponseEntity.ok(accountService.check(phoneDTO));
    }
}
