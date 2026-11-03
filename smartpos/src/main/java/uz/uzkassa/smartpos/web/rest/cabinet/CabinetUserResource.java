package uz.uzkassa.smartpos.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.service.UserService;

@RestController
@RequestMapping("/api/v1/cabinet/users")
@RequiredArgsConstructor
public class CabinetUserResource {
    private final UserService userService;

    @PutMapping("/{id}/permission/change-price")
    @Operation(summary = "addChangePricePermissionToCashier")
    public void addChangePricePermissionToCashier(@PathVariable Long id) {
        userService.addChangePricePermissionToCashier(id);
    }
}
