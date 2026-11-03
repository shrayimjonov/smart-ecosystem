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
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.dto.report.CompanyCommittentReportDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.CompanyCommittentService;
import uz.uzkassa.smartpossupply.service.handlers.excel.ProductReportByComittentStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCommittentRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompanyCommittentResource {

    CompanyCommittentService companyCommittentService;
    ProductReportByComittentStrategy productReportByComittentStrategy;

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CompanyCommittentLookUpDTO>> lookUp(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(companyCommittentService.lookUp(filter));
    }

    @GetMapping("/report")
    public ResponseEntity<Page<CompanyCommittentReportDTO>> getReport(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(companyCommittentService.getReport(filter));
    }

    @GetMapping("/report-total-qty")
    public ResponseEntity<BigDecimal> getReportTotalQty(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new BigDecimal(0));
        }
        return ResponseEntity.ok(companyCommittentService.getReportTotalQty(filter));
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Resource> getBalanceReportExportExcel(@ParameterObject BalanceReportFilter filter) {
        productReportByComittentStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportCSV)
    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Void> getBalanceReportExportCsv(@ParameterObject BalanceReportFilter filter) {
        productReportByComittentStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/qty-statistics")
    public ResponseEntity<StockProductQtyStatisticsDTO> getQtyStatistics(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(companyCommittentService.getQtyStatistics(filter));
    }

    @GetMapping("/qty-details")
    public ResponseEntity<Page<StockProductQtyDetailListDTO>> getQtyDetails(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(companyCommittentService.getQtyDetails(filter));
    }
}
