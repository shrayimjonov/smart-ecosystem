package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.session.UserSessionDTO;
import uz.uzkassa.horeca.service.FirebaseNotificationService;
import uz.uzkassa.horeca.service.UserSessionService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.10.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileUserSessionRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileUserSessionResource {

    UserSessionService userSessionService;

    @Operation(operationId = "createUserSession")
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UserSessionDTO userSessionDTO) {
        userSessionService.create(userSessionDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "deleteUserSession")
    @DeleteMapping
    public ResponseEntity<Void> delete(@Valid @RequestBody UserSessionDTO userSessionDTO) {
        userSessionService.delete(userSessionDTO);
        return ResponseEntity.ok().build();
    }
}
