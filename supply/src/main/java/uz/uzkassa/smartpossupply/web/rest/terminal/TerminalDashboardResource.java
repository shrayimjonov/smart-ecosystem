package uz.uzkassa.smartpossupply.web.rest.terminal;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.receipt.TerminalDashboardStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.ReceiptFilter;
import uz.uzkassa.smartpossupply.service.ReceiptService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.11.2024
 */
@RestController
@RequestMapping(ApiConstants.terminalDashboardRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalDashboardResource {

    ReceiptService receiptService;

    @Operation(summary = ApiConstants.Statistics, operationId = "dashboardStatistics")
    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<TerminalDashboardStatisticsDTO> getOrderStatistics(@ParameterObject ReceiptFilter filter) {
        return ResponseEntity.ok(receiptService.getDashboardStatistics(filter));
    }
}
