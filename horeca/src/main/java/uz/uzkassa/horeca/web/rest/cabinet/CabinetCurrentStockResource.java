package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.stock.CurrentStockDTO;
import uz.uzkassa.horeca.dto.stock.CurrentStockListDTO;
import uz.uzkassa.horeca.dto.stock.CurrentStockUpdateStatusDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.MenuItemFilter;
import uz.uzkassa.horeca.service.CurrentStockService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.11.2024
 */
@RestController
@RequestMapping(ApiConstants.cabinetCurrentStockRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCurrentStockResource {

    CurrentStockService currentStockService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CurrentStockDTO currentStockDTO) {
        return ResponseEntity.ok(currentStockService.create(currentStockDTO));
    }

    @PostMapping("/multiple")
    public ResponseEntity<Void> create(@RequestBody List<CurrentStockDTO> currentStockDTOList) {
        currentStockService.create(currentStockDTOList);
        return ResponseEntity.ok().build();
    }
    @GetMapping
    public ResponseEntity<Page<CurrentStockListDTO>> getList(@ParameterObject MenuItemFilter filter) {
        return ResponseEntity.ok(currentStockService.getList(filter));
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> updateStatus(@RequestBody CurrentStockUpdateStatusDTO statusDTO) {
        currentStockService.updateStatus(statusDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        currentStockService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statuses")
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }

}
