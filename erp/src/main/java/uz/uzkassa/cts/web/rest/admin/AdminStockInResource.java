package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.stockin.StockInDTO;
import uz.uzkassa.cts.dto.stockin.StockInDetailDTO;
import uz.uzkassa.cts.dto.stockin.StockInItemLookUpDTO;
import uz.uzkassa.cts.dto.stockin.StockInListDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.StockStatus;
import uz.uzkassa.cts.filter.ProductFilter;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.StockInService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminStockInRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminStockInResource {

    StockInService stockInService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockInDTO stockInDTO) {
        return ResponseEntity.ok(stockInService.create(stockInDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockInDTO stockInDTO) {
        return ResponseEntity.ok(stockInService.update(id, stockInDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockInDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockInService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<StockInListDTO>> getList(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockInService.getList(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockInService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") StockStatus status) {
        stockInService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items" + ApiConstants.lookUp)
    public ResponseEntity<List<StockInItemLookUpDTO>> getItemsLookUp(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(stockInService.getItemsLookUp(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject StockFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.STOCK_IN, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject StockFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.STOCK_IN, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
