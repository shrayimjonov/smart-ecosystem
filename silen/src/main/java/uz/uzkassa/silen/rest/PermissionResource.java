package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.PermissionDTO;
import uz.uzkassa.silen.dto.PermissionListDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.enumeration.PermissionType;
import uz.uzkassa.silen.service.PermissionService;

import java.util.List;

@RestController
@RequestMapping("/api/rbac/permission")
@RequiredArgsConstructor
public class PermissionResource {
    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody PermissionDTO permissionDTO) {
        return ResponseEntity.ok().body(permissionService.create(permissionDTO));
    }

    @PutMapping("{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody PermissionDTO permissionDTO) {
        return ResponseEntity.ok().body(permissionService.update(id, permissionDTO));
    }

    @GetMapping("{id}")
    public ResponseEntity<PermissionDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok().body(permissionService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<PermissionListDTO>> getList(@RequestParam("type") PermissionType type) {
        return ResponseEntity.ok().body(permissionService.getList(type));
    }


    @GetMapping("/types")
    public ResponseEntity<List<SelectItem>> getTypes() {
        return ResponseEntity.ok().body(PermissionType.getAll());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
