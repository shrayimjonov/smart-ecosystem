package uz.uzkassa.cts.web.rest.cabinet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.auth.*;
import uz.uzkassa.cts.dto.user.cabinet.CurrentUserCabinetDTO;
import uz.uzkassa.cts.enums.TemplateType;
import uz.uzkassa.cts.service.UserAuthService;
import uz.uzkassa.cts.service.UserService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetAccountRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetAccountResource {

    UserService userService;
    UserAuthService userAuthService;

    @GetMapping(ApiConstants.current)
    public ResponseEntity<CurrentUserCabinetDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserForCabinet());
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
    public ResponseEntity<Void> changeLoginConfirm(@RequestBody ChangeUsernameConfirmDTO changeLoginConfirmDTO, HttpServletRequest request, HttpServletResponse response) {
        userAuthService.changeUsernameConfirm(null, changeLoginConfirmDTO, request, response);
        return ResponseEntity.ok().build();
    }
}
