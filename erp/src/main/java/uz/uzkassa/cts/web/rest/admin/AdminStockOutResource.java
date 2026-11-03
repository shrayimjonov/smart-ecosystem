package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.stockout.StockOutDTO;
import uz.uzkassa.cts.dto.stockout.StockOutDetailDTO;
import uz.uzkassa.cts.dto.stockout.StockOutListDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.StockOutService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminStockOutRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminStockOutResource {

    StockOutService stockOutService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockOutDTO stockOutDTO) {
        return ResponseEntity.ok(stockOutService.create(stockOutDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockOutDTO stockOutDTO) {
        return ResponseEntity.ok(stockOutService.update(id, stockOutDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockOutDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockOutService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<StockOutListDTO>> getList(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockOutService.getList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockOutService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") StockStatus status) {
        stockOutService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items" + ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> getItemsLookUp(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockOutService.getItemsLookUp(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject StockFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.STOCK_OUT, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject StockFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.STOCK_OUT, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
