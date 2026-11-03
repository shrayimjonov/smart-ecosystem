package uz.uzkassa.cts.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.permission.UserPermissionDTO;
import uz.uzkassa.cts.service.UserPermissionService;

import java.util.List;


@RestController
@RequestMapping(ApiConstants.adminUserPermissionRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminUserPermissionResource {

    UserPermissionService userPermissionService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody UserPermissionDTO userPermissionDTO) {
        userPermissionService.create(userPermissionDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CommonCodeDTO>> getListByUserId(@RequestParam(value = "userId", required = false) Long userId) {
        return ResponseEntity.ok(userPermissionService.getListByUserId(userId));
    }
}
