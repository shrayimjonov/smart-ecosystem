package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.equipmentrequest.EquipmentRequestDTO;
import uz.uzkassa.cts.dto.equipmentrequest.admin.EquipmentRequestDetailAdminDTO;
import uz.uzkassa.cts.dto.equipmentrequest.admin.EquipmentRequestListAdminDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.filter.EquipmentRequestFilter;
import uz.uzkassa.cts.service.EquipmentRequestService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created date time: 25.12.2024 15:51
 */

@RestController
@RequestMapping(ApiConstants.adminEquipmentRequestRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEquipmentRequestResource {

    EquipmentRequestService equipmentRequestService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody EquipmentRequestDTO equipmentRequestDTO) {
        return ResponseEntity.ok(equipmentRequestService.create(equipmentRequestDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody EquipmentRequestDTO equipmentRequestDTO) {
        return ResponseEntity.ok(equipmentRequestService.update(id, equipmentRequestDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<EquipmentRequestDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentRequestService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<EquipmentRequestListAdminDTO>> getList(@ParameterObject EquipmentRequestFilter filter) {
        return ResponseEntity.ok(equipmentRequestService.getListForAdmin(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam ApproverCommonStatus status) {
        equipmentRequestService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
