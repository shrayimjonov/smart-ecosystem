package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.stockadjustment.StockAdjustmentDTO;
import uz.uzkassa.cts.dto.stockadjustment.StockAdjustmentDetailDTO;
import uz.uzkassa.cts.dto.stockadjustment.StockAdjustmentListDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.StockAdjustmentService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminStockAdjustmentRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminStockAdjustmentResource {

    StockAdjustmentService stockAdjustmentService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockAdjustmentDTO stockAdjustmentDTO) {
        return ResponseEntity.ok(stockAdjustmentService.create(stockAdjustmentDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockAdjustmentDTO stockAdjustmentDTO) {
        return ResponseEntity.ok(stockAdjustmentService.update(id, stockAdjustmentDTO));
    }

    @GetMapping
    public ResponseEntity<Page<StockAdjustmentListDTO>> getList(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockAdjustmentService.getList(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockAdjustmentDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockAdjustmentService.get(id));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, StockStatus status) {
        stockAdjustmentService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockAdjustmentService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items" + ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> getItemsLookUp(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockAdjustmentService.getItemsLookUp(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject StockFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.STOCK_ADJUSTMENT, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject StockFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.STOCK_ADJUSTMENT, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

}
