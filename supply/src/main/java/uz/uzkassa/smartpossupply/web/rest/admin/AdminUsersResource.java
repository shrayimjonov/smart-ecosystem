package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.auth.ChangePasswordDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.user.UserSupervisorDTO;
import uz.uzkassa.smartpossupply.dto.user.admin.AdminUserDTO;
import uz.uzkassa.smartpossupply.dto.user.admin.AdminUserDetailDTO;
import uz.uzkassa.smartpossupply.dto.user.admin.AdminUserListDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.UserStatus;
import uz.uzkassa.smartpossupply.filters.UserFilter;
import uz.uzkassa.smartpossupply.service.UserService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminUserRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminUsersResource {

    UserService userService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody AdminUserDTO adminUserDTO) {
        return ResponseEntity.ok(userService.create(adminUserDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<AdminUserDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(userService.get(id));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<AdminUserListDTO>> getList(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(userService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(userService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<UserStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(UserStatus.class));
    }

    @Operation(summary = ApiConstants.Roles)
    @GetMapping(ApiConstants.roles)
    public ResponseEntity<EnumSet<Role>> getRoles() {
        return ResponseEntity.ok(EnumSet.of(Role.ROLE_ADMIN, Role.ROLE_MODERATOR, Role.ROLE_OPERATOR, Role.ROLE_SUPPORT, Role.ROLE_SUPERVISOR));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody AdminUserDTO adminUserDTO) {
        return ResponseEntity.ok(userService.update(id, adminUserDTO));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        userService.updateStatus(id, UserStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change Password")
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(id, changePasswordDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Assign supervisor")
    @PutMapping("/assign/supervisor")
    public ResponseEntity<Void> assignSupervisor(@RequestBody UserSupervisorDTO userSupervisorDTO) {
        userService.assignSupervisor(userSupervisorDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove supervisor")
    @PutMapping("/remove/supervisor/{managerId}")
    public ResponseEntity<Void> removeSupervisor(@PathVariable Long managerId) {
        userService.removeSupervisor(managerId);
        return ResponseEntity.ok().build();
    }
}
