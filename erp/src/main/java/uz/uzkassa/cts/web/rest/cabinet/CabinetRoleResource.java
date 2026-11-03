package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.enums.Role;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetRoleRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetRoleResource {

    @GetMapping
    public ResponseEntity<EnumSet<Role>> getRoles() {
        return ResponseEntity.ok(EnumSet.of(Role.OPERATOR, Role.CURATOR, Role.SUPERVISOR, Role.MANAGER, Role.CALL_CENTER));
    }
}
