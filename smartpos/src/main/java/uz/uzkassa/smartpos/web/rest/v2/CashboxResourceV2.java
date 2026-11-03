package uz.uzkassa.smartpos.web.rest.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.service.BranchService;
import uz.uzkassa.smartpos.service.UserService;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/v2/cashbox")
@RequiredArgsConstructor
public class CashboxResourceV2 {
    private final BranchService branchService;
    private final UserService userService;

    @PutMapping("/{branchId}/register-branch")
    @Secured({SMARTPOS_ADMIN, OWNER, BRANCH_ADMIN})
    public ResponseEntity registerCashbox(@PathVariable(name = "branchId") Long branchId) {
        branchService.registerCashbox(branchId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCashboxUser());
    }
}
