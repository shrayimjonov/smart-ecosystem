package uz.uzkassa.horeca.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CheckStatusResponseDTO;
import uz.uzkassa.horeca.dto.base.UpdateStatusDTO;
import uz.uzkassa.horeca.dto.hallitem.*;
import uz.uzkassa.horeca.filters.HallItemFilter;
import uz.uzkassa.horeca.service.HallItemService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetHallItemRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetHallItemResource {

    HallItemService hallItemService;

    @PostMapping("/{hallId}")
    public ResponseEntity<HallItemDetailDTO> create(@PathVariable Long hallId, @RequestBody HallItemDTO hallItemDTO) {
        return ResponseEntity.ok(hallItemService.create(hallId, hallItemDTO));
    }

    @PostMapping("/{hallId}/batch")
    public ResponseEntity<Void> batchCreate(@PathVariable Long hallId, @RequestBody HallItemBatchDTO hallItemBatchDTO) {
        hallItemService.batchCreate(hallId, hallItemBatchDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{hallId}/batch")
    public ResponseEntity<Void> batchUpdate(@PathVariable Long hallId, @RequestBody HallItemBatchDTO hallItemBatchDTO) {
        hallItemService.batchUpdate(hallId, hallItemBatchDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<HallItemDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(hallItemService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<HallItemListDTO>> getList(@ParameterObject HallItemFilter filter) {
        return ResponseEntity.ok(hallItemService.getList(filter));
    }

    @GetMapping("/{hallId}" + ApiConstants.items)
    public ResponseEntity<List<HallItemListDTO>> getItems(@PathVariable Long hallId) {
        return ResponseEntity.ok(hallItemService.getItems(hallId));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<HallItemTableStatisticsDTO> getStatistics(@ParameterObject HallItemFilter filter) {
        return ResponseEntity.ok(hallItemService.getStatistics(filter));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody HallItemDTO hallItemDTO) {
        return ResponseEntity.ok(hallItemService.update(id, hallItemDTO));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hallItemService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO) {
        hallItemService.updateStatus(id, updateStatusDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/merge")
    public ResponseEntity<Void> merge(@RequestBody HallItemMergeDTO hallItemMergeDTO) {
        hallItemService.merge(hallItemMergeDTO);
        return ResponseEntity.ok().build();
    }


    @Deprecated
    @GetMapping("/{hallId}/tables")
    public ResponseEntity<List<HallTableListDTO>> getTables(@PathVariable Long hallId) {
        return ResponseEntity.ok(hallItemService.getTables(hallId));
    }


    @Deprecated
    @GetMapping("/table/{id}")
    public ResponseEntity<HallItemTableDetailDTO> getTable(@PathVariable Long id) {
        return ResponseEntity.ok(hallItemService.getTable(id));
    }

    @Operation(operationId = "checkTable")
    @PostMapping(ApiConstants.id + "/check")
    public ResponseEntity<CheckStatusResponseDTO> check(@PathVariable Long id, @RequestBody HallItemCheckDTO hallItemCheckDTO) {
        return ResponseEntity.ok(hallItemService.check(id, hallItemCheckDTO));
    }
}
