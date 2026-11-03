package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CheckStatusResponseDTO;
import uz.uzkassa.horeca.dto.base.UpdateStatusDTO;
import uz.uzkassa.horeca.dto.hallitem.HallItemCheckDTO;
import uz.uzkassa.horeca.dto.hallitem.HallItemDTO;
import uz.uzkassa.horeca.dto.hallitem.HallItemMergeDTO;
import uz.uzkassa.horeca.dto.hallitem.mobile.MobileHallItemDetailDTO;
import uz.uzkassa.horeca.dto.hallitem.mobile.MobileHallTableListDTO;
import uz.uzkassa.horeca.enums.HallItemReason;
import uz.uzkassa.horeca.enums.HallItemStatus;
import uz.uzkassa.horeca.service.HallItemService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.02.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileHallItemRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileHallItemResource {

    HallItemService hallItemService;

    @Operation(operationId = "hallItemUpdate")
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody HallItemDTO hallItemDTO) {
        return ResponseEntity.ok(hallItemService.update(id, hallItemDTO));
    }

    @Operation(operationId = "hallItemGetDetail")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<MobileHallItemDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(hallItemService.getForMobile(id));
    }

    @Operation(operationId = "hallItemUpdateStatus")
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO) {
        hallItemService.updateStatus(id, updateStatusDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "hallItemGetCurrentUserTables")
    @GetMapping("/{hallId}/tables")
    public ResponseEntity<List<MobileHallTableListDTO>> getTables(@PathVariable Long hallId) {
        return ResponseEntity.ok(hallItemService.getTablesForMobile(hallId));
    }

    @PatchMapping("/merge")
    public ResponseEntity<Void> merge(@RequestBody HallItemMergeDTO hallItemMergeDTO) {
        hallItemService.merge(hallItemMergeDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "checkTable")
    @PostMapping(ApiConstants.id + "/check")
    public ResponseEntity<CheckStatusResponseDTO> check(@PathVariable Long id, @RequestBody HallItemCheckDTO hallItemCheckDTO) {
        return ResponseEntity.ok(hallItemService.check(id, hallItemCheckDTO));
    }

    @Operation(operationId = "getHallItemStatuses")
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<HallItemStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(HallItemStatus.class));
    }

    @GetMapping("/hall-items/reasons")
    public ResponseEntity<EnumSet<HallItemReason>> getHallItemReasons() {
        return ResponseEntity.ok(EnumSet.of(HallItemReason.TECHNICAL_SERVICES, HallItemReason.RESERVATION_FOR_EVENT, HallItemReason.SANITARY_REASON, HallItemReason.RESERVATION_FOR_SPECIAL_CLIENT, HallItemReason.PLANNING_AND_REARRANGEMENT, HallItemReason.OTHER));
    }
}
