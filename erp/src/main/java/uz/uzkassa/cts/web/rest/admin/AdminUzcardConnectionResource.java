package uz.uzkassa.cts.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.uzcard.UzcardConnectionDTO;
import uz.uzkassa.cts.dto.uzcard.admin.UzcardConnectionListAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.UzcardConnectionService;

@RestController
@RequestMapping(value = ApiConstants.adminUzcardConnectionRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUzcardConnectionResource {

    UzcardConnectionService uzcardConnectionService;

    @PostMapping(value = "/create")
    public ResponseEntity<Void> create(@RequestBody UzcardConnectionDTO uzcardConnectionDTO) {
        uzcardConnectionService.create(uzcardConnectionDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<UzcardConnectionListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(uzcardConnectionService.getListForAdmin(filter));
    }
}
