package uz.uzkassa.silen.rest;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.UserPermissionsDTO;
import uz.uzkassa.silen.service.UserPermissionService;

import java.util.List;

@RestController
@RequestMapping("/api/rbac/user-permissions")
@AllArgsConstructor
public class UserPermissionResource {
    private final UserPermissionService userPermissionService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UserPermissionsDTO userPermissionsDTO) {
        userPermissionService.save(userPermissionsDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<String>> getByUserId(@PathVariable("id") String id) {
        return ResponseEntity.ok(userPermissionService.getPermissionsByUser(id));
    }
}
