package uz.uzkassa.cts.web.rest.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.auth.ChangePasswordDTO;
import uz.uzkassa.cts.dto.auth.ChangeUsernameConfirmDTO;
import uz.uzkassa.cts.dto.auth.SecretKeyDTO;
import uz.uzkassa.cts.dto.auth.UsernameDTO;
import uz.uzkassa.cts.dto.user.admin.CurrentUserAdminDTO;
import uz.uzkassa.cts.service.UserAuthService;
import uz.uzkassa.cts.service.UserService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminAccountRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminAccountResource {

    UserService userService;
    UserAuthService userAuthService;

    @GetMapping(ApiConstants.current)
    public ResponseEntity<CurrentUserAdminDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserForAdmin());
    }

    @GetMapping(ApiConstants.changePasswordInit)
    public ResponseEntity<SecretKeyDTO> changePasswordInit() {
        return ResponseEntity.ok(userAuthService.changePasswordInit());
    }

    @PostMapping(ApiConstants.changePasswordConfirm)
    public ResponseEntity<Void> changePasswordConfirm(@RequestBody ChangePasswordDTO changePasswordDTO) {
        userAuthService.changePasswordConfirm(changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.changeUsernameInit)
    public ResponseEntity<SecretKeyDTO> changeUsernameInit(@RequestBody UsernameDTO usernameDTO) {
        return ResponseEntity.ok(userAuthService.changeUsernameInit(null, usernameDTO));
    }

    @PostMapping(ApiConstants.changeUsernameConfirm)
    public ResponseEntity<Void> changeUsernameConfirm(@RequestBody ChangeUsernameConfirmDTO changeLoginConfirmDTO, HttpServletRequest request, HttpServletResponse response) {
        userAuthService.changeUsernameConfirm(null, changeLoginConfirmDTO, request, response);
        return ResponseEntity.ok().build();
    }
}
