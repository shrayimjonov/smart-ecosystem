package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.stockadjustment.StockAdjustmentDTO;
import uz.uzkassa.horeca.dto.stockadjustment.StockAdjustmentDetailDTO;
import uz.uzkassa.horeca.dto.stockadjustment.StockAdjustmentListDTO;
import uz.uzkassa.horeca.enums.StockAdjustmentStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.StockAdjustmentService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.ArrayList;
import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetStockAdjustmentRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetStockAdjustmentResource {

    StockAdjustmentService stockAdjustmentService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockAdjustmentDTO stockAdjustmentDTO) {
        return ResponseEntity.ok(stockAdjustmentService.create(stockAdjustmentDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockAdjustmentDTO stockAdjustmentDTO) {
        return ResponseEntity.ok(stockAdjustmentService.update(id, stockAdjustmentDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockAdjustmentDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockAdjustmentService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<StockAdjustmentListDTO>> getList(@ParameterObject BaseFilter filter) {
        if (HeaderUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(stockAdjustmentService.getList(filter));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockAdjustmentStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(StockAdjustmentStatus.class));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody StockAdjustmentStatus status) {
        stockAdjustmentService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockAdjustmentService.delete(id);
        return ResponseEntity.ok().build();
    }
}
