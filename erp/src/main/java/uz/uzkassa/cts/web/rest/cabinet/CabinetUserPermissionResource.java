package uz.uzkassa.cts.web.rest.cabinet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.service.UserPermissionService;

import java.util.List;


@RestController
@RequestMapping(ApiConstants.cabinetUserPermissionRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetUserPermissionResource {

    UserPermissionService userPermissionService;

    @GetMapping
    public ResponseEntity<List<CommonCodeDTO>> getListByUserId(@RequestParam(value = "userId", required = false) Long userId) {
        return ResponseEntity.ok(userPermissionService.getListByUserId(userId));
    }
}
