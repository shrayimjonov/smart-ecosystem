package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.RolePermissionDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.enumeration.UserRole;
import uz.uzkassa.silen.service.PermissionService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/rbac/role")
@RequiredArgsConstructor
public class RoleResource {
    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<SelectItem>> getList() {
        return ResponseEntity.ok(UserRole.getAdminRoles());
    }

    @GetMapping("/{role}")
    public ResponseEntity<Set<String>> get(@PathVariable("role") UserRole role) {
        return ResponseEntity.ok(permissionService.getRolePermissions(role));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid RolePermissionDTO rolePermissionDTO) {
        permissionService.saveRolePermissions(rolePermissionDTO);
        return ResponseEntity.ok().build();
    }
}
