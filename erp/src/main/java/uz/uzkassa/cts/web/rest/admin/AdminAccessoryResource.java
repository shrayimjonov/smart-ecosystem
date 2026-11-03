package uz.uzkassa.cts.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.accessory.AccessoryDTO;
import uz.uzkassa.cts.dto.accessory.AccessoryDetailDTO;
import uz.uzkassa.cts.dto.accessory.AccessoryListDTO;
import uz.uzkassa.cts.enums.AccessoryType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.AccessoryService;

import java.util.EnumSet;



@RestController
@RequestMapping(ApiConstants.adminAccessoryRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminAccessoryResource {

    AccessoryService accessoryService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody AccessoryDTO accessoryDTO) {
        return ResponseEntity.ok(accessoryService.create(accessoryDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody AccessoryDTO accessoryDTO) {
        return ResponseEntity.ok(accessoryService.update(id, accessoryDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<AccessoryDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(accessoryService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<AccessoryListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(accessoryService.getList(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        accessoryService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/types")
    public ResponseEntity<EnumSet<AccessoryType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(AccessoryType.class));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accessoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
