package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.report.MobileWarehouseProductReportDTO;
import uz.uzkassa.smartpossupply.dto.stock.MobileStockProductDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.warehouseproduct.WarehouseProductOfflineDTO;
import uz.uzkassa.smartpossupply.enums.ReportType;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.WarehouseProductReportService;
import uz.uzkassa.smartpossupply.service.WarehouseProductService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 24.02.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileWarehouseProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileWarehouseProductResource {

    WarehouseProductReportService warehouseProductReportService;
    WarehouseProductService warehouseProductService;

    @Operation(summary = "Get Report", operationId = "getReport")
    @GetMapping("/report")
    public ResponseEntity<Page<MobileWarehouseProductReportDTO>> getReport(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        filter.setBranchId(SecurityUtils.getCurrentBranchId());
        filter.setWarehouseId(SecurityUtils.getCurrentWarehouseId());
        return ResponseEntity.ok(warehouseProductReportService.getReport(filter));
    }

    @Operation(summary = "Get Report Total Qty", operationId = "getReportTotalQty")
    @GetMapping("/report-total-qty")
    public ResponseEntity<BigDecimal> getReportTotalQty(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new BigDecimal(0));
        }
        filter.setBranchId(SecurityUtils.getCurrentBranchId());
        filter.setWarehouseId(SecurityUtils.getCurrentWarehouseId());
        return ResponseEntity.ok(warehouseProductReportService.getReportTotalQty(filter));
    }

    @Operation(summary = "Get Product Qty Details", operationId = "getProductQtyDetails")
    @GetMapping("/product-qty-details")
    public ResponseEntity<Page<StockProductQtyDetailListDTO>> getProductQtyDetails(@ParameterObject StockFilter filter) {
        filter.setBranchId(SecurityUtils.getCurrentBranchId());
        filter.setWarehouseId(SecurityUtils.getCurrentWarehouseId());
        return ResponseEntity.ok(warehouseProductService.getProductQtyDetails(filter));
    }

    @Operation(summary = "Get Product Qty Statistics", operationId = "getProductQtyStatistics")
    @GetMapping("/product-qty-statistics")
    public ResponseEntity<StockProductQtyStatisticsDTO> getProductQtyStatistics(@ParameterObject StockFilter filter) {
        filter.setBranchId(SecurityUtils.getCurrentBranchId());
        filter.setWarehouseId(SecurityUtils.getCurrentWarehouseId());
        return ResponseEntity.ok(warehouseProductService.getProductQtyStatistics(filter));
    }

    @GetMapping(ApiConstants.offline)
    public ResponseEntity<List<WarehouseProductOfflineDTO>> getForOffline() {
        if (CollectionUtils.isEmpty(SecurityUtils.getCurrentBranchIds())) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(warehouseProductService.getForOffline(SecurityUtils.getCurrentBranchIds()));
    }


    @Operation(summary = "Get Stock Product", operationId = "getStockProduct")
    @GetMapping("/{stockId}/{productId}")
    public ResponseEntity<MobileStockProductDTO> getStockProduct(@PathVariable Long stockId, @PathVariable Long productId) {
        return ResponseEntity.ok(warehouseProductReportService.getStockProduct(stockId, productId));
    }

    @Operation(operationId = "getReportTypes")
    @GetMapping("/report-types")
    public ResponseEntity<EnumSet<ReportType>> getReportTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ReportType.class));
    }
}
