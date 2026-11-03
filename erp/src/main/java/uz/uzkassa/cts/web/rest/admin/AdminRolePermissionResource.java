package uz.uzkassa.cts.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.permission.RolePermissionDTO;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.service.RolePermissionService;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.adminRolePermissionRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminRolePermissionResource {

    RolePermissionService rolePermissionService;

    @PostMapping
    public ResponseEntity<List<String>> create(@RequestBody RolePermissionDTO rolePermissionDTO) {
        return ResponseEntity.ok(rolePermissionService.create(rolePermissionDTO));
    }

    @GetMapping("/{role}")
    public ResponseEntity<List<CommonCodeDTO>> getListByRole(@PathVariable Role role) {
        return ResponseEntity.ok(rolePermissionService.getListByRole(role));
    }
}
