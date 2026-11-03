package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonCodeDTO;
import uz.uzkassa.smartpossupply.dto.permission.PermissionDTO;
import uz.uzkassa.smartpossupply.dto.permission.PermissionListDTO;
import uz.uzkassa.smartpossupply.dto.permission.SyncPermissionListDTO;
import uz.uzkassa.smartpossupply.dto.permission.UserRolePermissionDTO;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.PermissionService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminPermissionRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminPermissionResource {

    PermissionService permissionService;

    //@Secured("ROLE_SUPER_ADMIN")
    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody PermissionDTO permissionDTO) {
        return ResponseEntity.ok(permissionService.create(permissionDTO));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody PermissionDTO permissionDTO) {
        return ResponseEntity.ok(permissionService.update(id, permissionDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<PermissionDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.get(id));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<List<PermissionListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(permissionService.getList(filter));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    //@Secured("ROLE_SUPER_ADMIN")
    @Operation(summary = "Create User Permissions")
    @PostMapping("/users/{userId}")
    public ResponseEntity<Void> saveUserPermissions(
        @PathVariable("userId") Long userId,
        @RequestBody UserRolePermissionDTO userRolePermissionDTO
    ) {
        permissionService.saveUserPermissions(userId, userRolePermissionDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "List By User Id")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<String>> getUserPermissions(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(permissionService.getUserPermissions(userId));
    }

    @Operation(summary = "Roles")
    @GetMapping("/roles")
    public ResponseEntity<List<CommonCodeDTO>> getRoles() {
        return ResponseEntity.ok(Role.getOrganizationRolesAsCommonDTO());
    }

    //@Secured("ROLE_SUPER_ADMIN")
    @Operation(summary = "Create Role Permissions")
    @PostMapping("/roles/{role}")
    public ResponseEntity<Void> saveRolePermissions(
        @PathVariable("role") String role,
        @RequestBody UserRolePermissionDTO userRolePermissionDTO
    ) {
        permissionService.saveRolePermissions(Role.getByCode(role),userRolePermissionDTO);
        return ResponseEntity.ok().build();
    }

    //@Secured("ROLE_SUPER_ADMIN")
    @Operation(summary = "List By Role")
    @GetMapping("/roles/{role}")
    public ResponseEntity<List<String>> getRolePermissions(@PathVariable("role") String role) {
        return ResponseEntity.ok(permissionService.getRolePermissions(Role.getByCode(role)));
    }

    @Operation(summary = ApiConstants.All)
    @GetMapping(ApiConstants.all)
    public ResponseEntity<List<SyncPermissionListDTO>> getAllList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(permissionService.getAllList(filter));
    }
}
