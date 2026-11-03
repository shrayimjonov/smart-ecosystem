package uz.uzkassa.cts.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.accessorymodel.AccessoryModelDTO;
import uz.uzkassa.cts.dto.accessorymodel.AccessoryModelDetailDTO;
import uz.uzkassa.cts.dto.accessorymodel.AccessoryModelListDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.AccessoryModelService;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.adminAccessoryModelRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminAccessoryModelResource {

    AccessoryModelService accessoryModelService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody AccessoryModelDTO accessoryModelDTO) {
        return ResponseEntity.ok(accessoryModelService.create(accessoryModelDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody AccessoryModelDTO accessoryModelDTO) {
        return ResponseEntity.ok(accessoryModelService.update(id, accessoryModelDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<AccessoryModelDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(accessoryModelService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<AccessoryModelListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(accessoryModelService.getList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(accessoryModelService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        accessoryModelService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
