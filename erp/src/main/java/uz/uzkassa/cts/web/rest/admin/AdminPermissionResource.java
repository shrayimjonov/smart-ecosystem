package uz.uzkassa.cts.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.permission.PermissionDTO;
import uz.uzkassa.cts.dto.permission.PermissionTreeListDTO;
import uz.uzkassa.cts.dto.permission.admin.PermissionDetailAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.PermissionService;

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
    public ResponseEntity<PermissionDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getForAdmin(id));
    }

    @GetMapping(ApiConstants.tree)
    public ResponseEntity<List<PermissionTreeListDTO>> getTree(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(permissionService.getTree(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
