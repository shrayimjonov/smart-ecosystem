package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonCodeDTO;
import uz.uzkassa.horeca.dto.permission.PermissionDTO;
import uz.uzkassa.horeca.dto.permission.PermissionListDTO;
import uz.uzkassa.horeca.dto.permission.SyncPermissionListDTO;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.PermissionService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.09.2024
 */
@RestController
@RequestMapping(ApiConstants.cabinetPermissionRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetPermissionResource {

    PermissionService permissionService;

    @GetMapping(ApiConstants.id)
    public ResponseEntity<PermissionDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<PermissionListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(permissionService.getList(filter));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<String>> getUserPermissions(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(permissionService.getUserPermissions(userId));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<CommonCodeDTO>> getRoles() {
        return ResponseEntity.ok(Role.getOrganizationCodesAsCommonDTO());
    }

    @GetMapping("/roles/{role}")
    public ResponseEntity<List<String>> getRolePermissions(@PathVariable("role") String role) {
        return ResponseEntity.ok(permissionService.getRolePermissions(Role.getByCode(role)));
    }

    @GetMapping(ApiConstants.all)
    public ResponseEntity<List<SyncPermissionListDTO>> getAllList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(permissionService.getAllList(filter));
    }

    @GetMapping("/users")
    public ResponseEntity<List<String>> getUserPermissions() {
        return ResponseEntity.ok().body(permissionService.getUserPermissions(HeaderUtils.getCurrentUserId()));
    }
}
