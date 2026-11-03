package uz.uzkassa.horeca.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.permission.PermissionDTO;
import uz.uzkassa.horeca.dto.permission.PermissionListDTO;
import uz.uzkassa.horeca.dto.permission.SyncPermissionListDTO;
import uz.uzkassa.horeca.dto.permission.UserRolePermissionDTO;
import uz.uzkassa.horeca.enums.PermissionType;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.PermissionService;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.adminPermissionRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminPermissionResource {

    PermissionService permissionService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody PermissionDTO permissionDTO) {
        return ResponseEntity.ok(permissionService.create(permissionDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody PermissionDTO permissionDTO) {
        return ResponseEntity.ok(permissionService.update(id, permissionDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<PermissionDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<PermissionListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(permissionService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<Void> saveUserPermissions(@PathVariable Long userId, @RequestBody UserRolePermissionDTO userRolePermissionDTO) {
        permissionService.saveUserPermissions(userId, userRolePermissionDTO.getPermissions());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<String>> getUserPermissions(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(permissionService.getUserPermissions(userId));
    }

    @GetMapping("/roles")
    public ResponseEntity<EnumSet<Role>> getRoles() {
        return ResponseEntity.ok(EnumSet.allOf(Role.class));
    }

    @PostMapping("/roles/{role}")
    public ResponseEntity<Void> saveRolePermissions(@PathVariable("role") String role, @RequestBody UserRolePermissionDTO userRolePermissionDTO) {
        permissionService.saveRolePermissions(Role.getByCode(role), userRolePermissionDTO.getPermissions());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles/{role}")
    public ResponseEntity<List<String>> getRolePermissions(@PathVariable("role") String role) {
        return ResponseEntity.ok(permissionService.getRolePermissions(Role.getByCode(role)));
    }

    @GetMapping(ApiConstants.all)
    public ResponseEntity<List<SyncPermissionListDTO>> getAllList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(permissionService.getAllList(filter));
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<PermissionType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(PermissionType.class));
    }
}
