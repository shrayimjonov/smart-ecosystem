package uz.uzkassa.smartpossupply.web.rest.terminal;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.*;
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.dto.file.PhotoByteArrayDTO;
import uz.uzkassa.smartpossupply.dto.file.PhotoDTO;
import uz.uzkassa.smartpossupply.dto.user.CurrentUserCabinetDTO;
import uz.uzkassa.smartpossupply.dto.user.CurrentUserFullNameMobileDTO;
import uz.uzkassa.smartpossupply.dto.user.SecretKeyDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.security.jwt.TokenProvider;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;
import uz.uzkassa.smartpossupply.service.LocalizationService;
import uz.uzkassa.smartpossupply.service.UserService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.AppGlobalException;

import java.util.Base64;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.terminalAccountRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalAccountResource {

    TokenProvider tokenProvider;
    AuthenticationManagerBuilder authenticationManagerBuilder;
    UserService userService;
    LocalizationService localizationService;

    @Operation(summary = ApiConstants.Current, operationId ="getCurrentUser")
    @GetMapping(ApiConstants.current)
    public ResponseEntity<CurrentUserCabinetDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserForCabinet());
    }

    @Operation(summary = ApiConstants.Login, operationId ="login")
    @PostMapping(ApiConstants.login)
    public ResponseEntity<MobileTokenDTO> login(@Valid @RequestBody CabinetLoginDTO cabinetLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            cabinetLoginDTO.getPhone(),
            cabinetLoginDTO.getPassword()
        );
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            MobileTokenDTO mobileTokenDTO = tokenProvider.createTokenForMobile(authentication, true);
            HttpHeaders httpHeaders = SecurityUtils.getHeader();
            httpHeaders.setBearerAuth(mobileTokenDTO.getAccessToken());
            return new ResponseEntity<>(mobileTokenDTO, httpHeaders, HttpStatus.OK);
        } catch (AppGlobalException | AuthenticationException e) {
            throw new AppGlobalException(localizationService.localize(MessageConstants.INCORRECT_USERNAME_OR_PASSWORD), e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = ApiConstants.Logout, operationId ="logout")
    @GetMapping(value = ApiConstants.logout, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change Password Init", operationId ="changePasswordInit")
    @GetMapping(ApiConstants.changePasswordInit)
    public ResponseEntity<Void> resetPasswordInit() {
        userService.changePasswordInit();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change Password Check", operationId ="changePasswordCheck")
    @PatchMapping(ApiConstants.changePasswordCheck)
    public ResponseEntity<SecretKeyDTO> resetPasswordCheck(@Valid @RequestBody BaseActivationDTO baseActivationDTO) {
        return ResponseEntity.ok(userService.changePasswordCheck(baseActivationDTO));
    }

    @Operation(summary = "Change Password Finish", operationId ="changePasswordFinish")
    @PatchMapping(ApiConstants.changePasswordFinish)
    public ResponseEntity<Void> resetPasswordFinish(@Valid @RequestBody BaseForgotPasswordDTO baseForgotPasswordDTO) {
        userService.changePasswordFinish(baseForgotPasswordDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change Username Init", operationId ="changeUsernameInit")
    @GetMapping(ApiConstants.changeUsernameInit)
    public ResponseEntity<Void> changeUsernameInit() {
        userService.changeUsernameInit();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change Username Check", operationId ="changeUsernameCheck")
    @PatchMapping(ApiConstants.changeUsernameCheck)
    public ResponseEntity<SecretKeyDTO> changeUsernameCheck(@Valid @RequestBody BaseActivationDTO baseActivationDTO) {
        return ResponseEntity.ok(userService.changeUsernameCheck(baseActivationDTO));
    }

    @Operation(summary = "Change Username Finish", operationId ="changeUsernameFinish")
    @PatchMapping(ApiConstants.changeUsernameFinish)
    public ResponseEntity<Void> changeUsernameFinish(@Valid @RequestBody ChangeUsernameFinishDTO changeUsernameFinishDTO) {
        userService.changeUsernameFinish(changeUsernameFinishDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Resend Activation Key", operationId ="resendActivationKey")
    @GetMapping(ApiConstants.reSendActivationKey)
    public ResponseEntity<Void> resendActivationKey() {
        userService.resendActivationKey();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Upload Photo", operationId ="uploadPhoto")
    @PostMapping(value = "/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDTO> uploadLogo(@RequestPart("file") MultipartFile photo) {
        return ResponseEntity.ok(userService.uploadPhoto(photo));
    }

    @Operation(summary = "Upload Photo Using Byte Array", operationId ="uploadPhotoUsingByteArray")
    @PostMapping("/upload/photo/{fileName}")
    public ResponseEntity<FileDTO> uploadLogo(@Valid @RequestBody PhotoByteArrayDTO photo, @PathVariable String fileName, @RequestParam String contentType) {
        return ResponseEntity.ok(userService.uploadPhoto(new CustomMultipartFile(photo.getPhoto(), fileName, contentType)));
    }

    @Operation(summary = "Upload Photo Using Base64String", operationId ="uploadPhotoUsingBase64String")
    @PostMapping("/upload/photo/base64")
    public ResponseEntity<FileDTO> uploadLogo(@Valid @RequestBody PhotoDTO photo) {
        return ResponseEntity.ok(userService.uploadPhoto(new CustomMultipartFile(Base64.getDecoder().decode(photo.getBody().getBytes()), photo.getFileName(), photo.getContentType())));
    }

    @Operation(summary = "Remove Photo", operationId ="removePhoto")
    @DeleteMapping("/photo")
    public ResponseEntity<Void> removePhoto() {
        userService.removePhoto();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change Full Name", operationId ="changeFullName")
    @PatchMapping("/change-full-name")
    public ResponseEntity<Void> updateFullName(@RequestBody CurrentUserFullNameMobileDTO currentUserFullNameMobileDTO) {
        userService.updateFullName(currentUserFullNameMobileDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Roles)
    @GetMapping(ApiConstants.roles)
    public ResponseEntity<List<Role>> getRoles() {
        return ResponseEntity.ok(Role.getCabinetRoles());
    }
}
