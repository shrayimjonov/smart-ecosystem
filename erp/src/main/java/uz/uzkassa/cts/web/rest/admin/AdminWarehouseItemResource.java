package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportListDTO;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportDetailListDTO;
import uz.uzkassa.cts.dto.stock.balancereport.StockBalanceReportStatisticsDTO;
import uz.uzkassa.cts.filter.StockFilter;
import uz.uzkassa.cts.service.WarehouseItemService;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminWarehouseItemRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminWarehouseItemResource {

    WarehouseItemService warehouseItemService;

    @GetMapping("/balance-report")
    public ResponseEntity<Page<StockBalanceReportListDTO>> getBalanceReport(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(warehouseItemService.getBalanceReport(filter));
    }

    @GetMapping("/balance-report-details")
    public ResponseEntity<Page<StockBalanceReportDetailListDTO>> getBalanceReportDetails(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(warehouseItemService.getBalanceReportDetails(filter));
    }

    @GetMapping("/balance-report-total-qty")
    public ResponseEntity<BigDecimal> getBalanceReportTotalQty(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(warehouseItemService.getBalanceReportTotalQty(filter));
    }

    @GetMapping("/balance-report-statistics")
    public ResponseEntity<StockBalanceReportStatisticsDTO> getBalanceReportStatistics(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(warehouseItemService.getBalanceReportStatistics(filter));
    }

    @GetMapping("/variations-qty")
    public ResponseEntity<BigDecimal> getVariationsQty(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(warehouseItemService.getVariationsQty(filter));
    }

}
