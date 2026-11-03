package uz.uzkassa.cts.web.rest.admin;

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
@RequestMapping(ApiConstants.adminRoleRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminRoleResource {

    @GetMapping
    public ResponseEntity<EnumSet<Role>> getRoles() {
        return ResponseEntity.ok(EnumSet.of(Role.ADMIN, Role.OPERATOR, Role.DIRECTOR, Role.ACCOUNTANT,  Role.PROMO_MANAGER, Role.HR, Role.EMPLOYEE, Role.CURATOR, Role.CALL_CENTER, Role.WAREHOUSE_MANAGER, Role.USER));
    }
}
