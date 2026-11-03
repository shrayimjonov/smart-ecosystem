package uz.uzkassa.smartpossupply.web.rest.mobile;

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
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.OrderService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 18.01.2023 10:23
 */
@RestController
@RequestMapping(ApiConstants.mobileDashboardRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileDashboardResource {

    OrderService orderService;

    @Operation(summary = ApiConstants.Statistics, operationId ="getDashboardOrderStatistics")
    @GetMapping("/order-statistics")
    public ResponseEntity<MobileOrderStatisticsDTO> getOrderStatistics(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(orderService.getOrderStatisticsForMobile(filter));
    }
}
