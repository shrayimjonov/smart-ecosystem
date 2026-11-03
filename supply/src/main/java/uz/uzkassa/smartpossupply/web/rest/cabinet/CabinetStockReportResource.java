package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.stock.ActiveCustomerBaseDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportByProductDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportByProductExtraInfoDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportCompanyDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportOrderDTO;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.ActiveCustomerFilter;
import uz.uzkassa.smartpossupply.filters.StockReportByProductFilter;
import uz.uzkassa.smartpossupply.service.StockReportService;
import uz.uzkassa.smartpossupply.service.handlers.excel.SalesProductReportStrategy;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetStockReportRotApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetStockReportResource {
    StockReportService stockReportService;
    SalesProductReportStrategy salesProductReportStrategy;

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/active-customer-base")
    public ResponseEntity<Page<ActiveCustomerBaseDTO>> getActiveCustomerBase(@ParameterObject ActiveCustomerFilter filter,
                                                                             @RequestParam(required = false, defaultValue = "false") Boolean notActive) {
        return ResponseEntity.ok(stockReportService.getActiveCustomerBase(filter, notActive));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/customers-count")
    public ResponseEntity<Map<String, Long>> allCustomerCount() {
        return ResponseEntity.ok(stockReportService.allCustomerCount());
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/by-products")
    public ResponseEntity<Page<StockReportByProductDTO>> getByProduct(@ParameterObject StockReportByProductFilter filter) {
        return ResponseEntity.ok(stockReportService.getStockReport(filter));
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping("/by-products" + ApiConstants.exportExcel)
    public ResponseEntity<Resource> exportExcel(@ParameterObject StockReportByProductFilter filter) {
        salesProductReportStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/by-products/extra-filter")
    public ResponseEntity<Page<StockReportByProductExtraInfoDTO>> getStockReportExtraInfo(@ParameterObject StockReportByProductFilter filter) {
        return ResponseEntity.ok(stockReportService.getStockReportExtraInfo(filter));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/by-products/company")
    public ResponseEntity<List<StockReportCompanyDTO>> getStockReportCompanyInfo(@ParameterObject StockReportByProductFilter filter) {
        return ResponseEntity.ok(stockReportService.getStockReportCompanyInfo(filter));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping("/{customerId}/last-order")
    public ResponseEntity<StockReportOrderDTO> getLastOrder(@PathVariable Long customerId) {
        return ResponseEntity.ok(stockReportService.getLastOrder(customerId));
    }
}
