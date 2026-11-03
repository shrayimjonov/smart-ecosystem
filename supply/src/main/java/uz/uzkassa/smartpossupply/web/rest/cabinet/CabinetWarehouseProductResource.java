package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aggregationcodes.AggregationCodeDTO;
import uz.uzkassa.smartpossupply.dto.aggregationcodes.AggregationCodeStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.base.EnumDTO;
import uz.uzkassa.smartpossupply.dto.report.WarehouseProductReportDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.TuronMarkStatus;
import uz.uzkassa.smartpossupply.filters.AggregationCodesReportFilter;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.WarehouseProductService;
import uz.uzkassa.smartpossupply.service.handlers.excel.StockBalanceReportStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetWarehouseProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetWarehouseProductResource {

    WarehouseProductService warehouseProductService;
    StockBalanceReportStrategy stockBalanceReportStrategy;

    @GetMapping("/report")
    public ResponseEntity<Page<WarehouseProductReportDTO>> getReport(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(warehouseProductService.getReport(filter));
    }

    @GetMapping("/report-total-qty")
    public ResponseEntity<BigDecimal> getReportTotalQty(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new BigDecimal(0));
        }
        return ResponseEntity.ok(warehouseProductService.getReportTotalQty(filter));
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Resource> getBalanceReportExportExcel(@ParameterObject BalanceReportFilter filter) {
        stockBalanceReportStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportCSV)
    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Void> getBalanceReportExportCsv(@ParameterObject BalanceReportFilter filter) {
        stockBalanceReportStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product-qty-details")
    public ResponseEntity<Page<StockProductQtyDetailListDTO>> getProductQtyDetails(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(warehouseProductService.getProductQtyDetails(filter));
    }

    @GetMapping("/product-qty-statistics")
    public ResponseEntity<StockProductQtyStatisticsDTO> getProductQtyStatistics(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(warehouseProductService.getProductQtyStatistics(filter));
    }

    @GetMapping("/codes")
    public ResponseEntity<Page<AggregationCodeDTO>> getCodes(@ParameterObject AggregationCodesReportFilter filter) {
        return ResponseEntity.ok(warehouseProductService.getCodes(filter));
    }

    @GetMapping("/codes/statistics")
    public ResponseEntity<AggregationCodeStatisticsDTO> getCodeStatistics(@ParameterObject AggregationCodesReportFilter filter) {
        return ResponseEntity.ok(warehouseProductService.getCodeStatistics(filter));
    }

    @GetMapping("/codes/statuses")
    public ResponseEntity<List<EnumDTO>> getCodeStatuses() {
        return ResponseEntity.ok(TuronMarkStatus.getAll());
    }
}
