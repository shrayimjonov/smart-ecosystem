package uz.uzkassa.smartpossupply.web.rest.terminal;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.dto.report.MobileWarehouseProductReportDTO;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.service.WarehouseProductReportService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 24.02.2024
 */
@RestController
@RequestMapping(ApiConstants.terminalWarehouseProductRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalWarehouseProductResource {

    WarehouseProductReportService warehouseProductReportService;

    @Operation(summary = "Get Report", operationId = "terminalGetProductReport")
    @GetMapping("/report")
    public ResponseEntity<Page<MobileWarehouseProductReportDTO>> getReport(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        filter.setHideZero(true);
        return ResponseEntity.ok(warehouseProductReportService.getReport(filter));
    }

    @Operation(summary =ApiConstants.LookUp, operationId = "terminalGetProductLookup")
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<MobileWarehouseProductReportDTO>> getProductLookup(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setHideZero(true);
        filter.setSize(Constants.COMMON_BATCH_SIZE);
        filter.setPage(0);
        return ResponseEntity.ok(warehouseProductReportService.getReportList(filter));
    }
}
