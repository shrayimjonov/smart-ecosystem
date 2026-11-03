package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.equipmentrequest.EquipmentRequestDTO;
import uz.uzkassa.cts.dto.equipmentrequest.cabinet.EquipmentRequestDetailCabinetDTO;
import uz.uzkassa.cts.dto.equipmentrequest.cabinet.EquipmentRequestListCabinetDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.filter.EquipmentRequestFilter;
import uz.uzkassa.cts.service.EquipmentRequestService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created date time: 25.12.2024 15:51
 */

@RestController
@RequestMapping(ApiConstants.cabinetEquipmentRequestRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetEquipmentRequestResource {

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
    public ResponseEntity<EquipmentRequestDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentRequestService.getForCabinet(id));
    }

    @GetMapping
    public ResponseEntity<Page<EquipmentRequestListCabinetDTO>> getList(@ParameterObject EquipmentRequestFilter filter) {
        return ResponseEntity.ok(equipmentRequestService.getListForCabinet(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam ApproverCommonStatus status) {
        equipmentRequestService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
