package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.stockreturn.StockReturnDTO;
import uz.uzkassa.cts.dto.stockreturn.StockReturnDetailDTO;
import uz.uzkassa.cts.dto.stockreturn.StockReturnListDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.StockReturnService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminStockReturnRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminStockReturnResource {

    StockReturnService stockReturnService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockReturnDTO stockReturnDTO) {
        return ResponseEntity.ok(stockReturnService.create(stockReturnDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockReturnDTO stockReturnDTO) {
        return ResponseEntity.ok(stockReturnService.update(id, stockReturnDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockReturnDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockReturnService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<StockReturnListDTO>> getList(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockReturnService.getList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockReturnService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") StockStatus status) {
        stockReturnService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items" + ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> getItemsLookUp(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockReturnService.getItemsLookUp(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject StockFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.STOCK_RETURN, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject StockFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.STOCK_RETURN, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
