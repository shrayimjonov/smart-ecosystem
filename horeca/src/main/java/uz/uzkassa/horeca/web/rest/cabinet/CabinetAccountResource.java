package uz.uzkassa.horeca.web.rest.cabinet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.account.AccountDetailDTO;
import uz.uzkassa.horeca.dto.account.AccountIdDTO;
import uz.uzkassa.horeca.dto.account.AccountProfileDTO;
import uz.uzkassa.horeca.dto.auth.ChangeLoginConfirmDTO;
import uz.uzkassa.horeca.dto.auth.ChangePasswordDTO;
import uz.uzkassa.horeca.dto.auth.PhoneDTO;
import uz.uzkassa.horeca.dto.auth.SecretKeyDTO;
import uz.uzkassa.horeca.service.AccountService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetAccountRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetAccountResource {

    AccountService accountService;

    @GetMapping(ApiConstants.changePasswordInit)
    public ResponseEntity<SecretKeyDTO> changePasswordInit() {
        return ResponseEntity.ok(accountService.changePasswordInit());
    }

    @PostMapping(ApiConstants.changePasswordConfirm)
    public ResponseEntity<Void> changePasswordConfirm(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        accountService.changePasswordConfirm(changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping(ApiConstants.changeLoginInit)
    public ResponseEntity<SecretKeyDTO> changeLoginInit(@RequestBody PhoneDTO phoneDTO) {
        return ResponseEntity.ok(accountService.changeLoginInit(phoneDTO));
    }

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

    @PostMapping(path = "/upload/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfilePhoto(@RequestPart("file") MultipartFile file) {
        accountService.uploadProfilePhoto(file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/profile-photo")
    public ResponseEntity<Void> deleteProfilePhoto() {
        accountService.deleteProfilePhoto();
        return ResponseEntity.ok().build();
    }

    @PutMapping(ApiConstants.profile)
    public ResponseEntity<Long> updateProfile(@RequestBody AccountProfileDTO accountProfileDTO) {
        return ResponseEntity.ok(accountService.updateProfile(accountProfileDTO));
    }

    @GetMapping(ApiConstants.profile)
    public ResponseEntity<AccountDetailDTO> getProfile() {
        return ResponseEntity.ok(accountService.getProfile());
    }

    @GetMapping("/client/{phone}")
    public ResponseEntity<AccountIdDTO> getActiveClient(@PathVariable(value = "phone") String phone) {
        return ResponseEntity.ok(accountService.getActiveClient(phone));
    }

    @PutMapping("/language/{code}")
    public ResponseEntity<Void> updateLanguage(@PathVariable String code) {
        accountService.updateLanguage(code);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = ApiConstants.logout, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            SecurityContextHolder.getContext().setAuthentication(null);
        }
        return ResponseEntity.ok().build();
    }
}
