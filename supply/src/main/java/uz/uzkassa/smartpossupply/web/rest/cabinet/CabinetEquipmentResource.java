package uz.uzkassa.smartpossupply.web.rest.cabinet;

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
@RequestMapping(ApiConstants.cabinetEquipmentRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetEquipmentResource {

    EquipmentService equipmentService;

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<EquipmentType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(EquipmentType.class));
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody EquipmentDTO equipmentDTO) {
        return ResponseEntity.ok(equipmentService.create(equipmentDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody EquipmentDTO equipmentDTO) {
        equipmentService.update(id, equipmentDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<EquipmentDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<EquipmentListMobileDTO>> getList(@ParameterObject EquipmentFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(equipmentService.getListForMobile(filter));
    }

    @PostMapping(value = "{id}/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPhoto(@PathVariable Long id, @RequestPart("file") MultipartFile photo) {
        equipmentService.uploadPhoto(id, photo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}/photo")
    public ResponseEntity<Void> removePhoto(@PathVariable Long id) {
        equipmentService.removePhoto(id);
        return ResponseEntity.ok().build();
    }
}
