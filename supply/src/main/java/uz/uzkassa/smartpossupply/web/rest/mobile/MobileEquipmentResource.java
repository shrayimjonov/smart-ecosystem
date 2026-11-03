package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentDTO;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentDetailDTO;
import uz.uzkassa.smartpossupply.dto.equipment.EquipmentListMobileDTO;
import uz.uzkassa.smartpossupply.enums.EquipmentType;
import uz.uzkassa.smartpossupply.filters.EquipmentFilter;
import uz.uzkassa.smartpossupply.service.EquipmentService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileEquipmentRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileEquipmentResource {

    EquipmentService equipmentService;

    @Operation(summary = ApiConstants.Create, operationId = "createEquipment")
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody EquipmentDTO equipmentDTO) {
        return ResponseEntity.ok(equipmentService.create(equipmentDTO));
    }

    @Operation(summary = ApiConstants.Update, operationId = "updateEquipment")
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody EquipmentDTO equipmentDTO) {
        equipmentService.update(id, equipmentDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Get, operationId = "getEquipment")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<EquipmentDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.get(id));
    }

    @Operation(summary = ApiConstants.List, operationId = "getEquipmentList")
    @GetMapping
    public ResponseEntity<Page<EquipmentListMobileDTO>> getList(@ParameterObject EquipmentFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(equipmentService.getListForMobile(filter));
    }

    @Operation(summary = ApiConstants.Types, operationId = "getEquipmentTypes")
    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<EquipmentType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(EquipmentType.class));
    }

    @Operation(summary = "Upload Photo", operationId = "uploadEquipmentPhoto")
    @PostMapping(value = "{id}/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPhoto(@PathVariable Long id, @RequestPart("file") MultipartFile photo) {
        equipmentService.uploadPhoto(id, photo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove Photo", operationId = "removeEquipmentPhoto")
    @DeleteMapping("{id}/photo")
    public ResponseEntity<Void> removePhoto(@PathVariable Long id) {
        equipmentService.removePhoto(id);
        return ResponseEntity.ok().build();
    }
}
