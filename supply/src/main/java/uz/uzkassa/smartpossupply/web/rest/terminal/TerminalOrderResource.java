package uz.uzkassa.smartpossupply.web.rest.terminal;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.order.OrderStatusUpdateDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalOrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalOrderListDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalOrderStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.OrderRejectReason;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.ProductOrigin;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.service.OrderService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023
 */

@RestController
@RequestMapping(ApiConstants.terminalOrderRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalOrderResource {

    OrderService orderService;


    @Operation(summary = ApiConstants.List, operationId = "getOrderList")
    @GetMapping
    public ResponseEntity<Page<TerminalOrderListDTO>> getList(@ParameterObject OrderFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        filter.setExpeditorId(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(orderService.getListForTerminal(filter));
    }

    @Operation(summary = ApiConstants.Get, operationId = "getOrder")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<TerminalOrderDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getForTerminal(id));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<OrderStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(OrderStatus.IN_DELIVERY, OrderStatus.LOADING, OrderStatus.UN_LOADING, OrderStatus.APPROVED, OrderStatus.COMPLETED));
    }

    @Operation(summary = "Payment Types", operationId = "getPaymentTypes")
    @GetMapping(ApiConstants.paymentTypes)
    public ResponseEntity<EnumSet<PaymentType>> getPaymentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(PaymentType.class));
    }

    @Operation(summary = "Product origins", operationId = "getProductOrigins")
    @GetMapping("/product-origins")
    public ResponseEntity<EnumSet<ProductOrigin>> getOrigins() {
        return ResponseEntity.ok(EnumSet.allOf(ProductOrigin.class));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status, @RequestParam(value = "lastModifiedDate", required = false) LocalDateTime lastModifiedDate, @RequestParam(value = "rejectReason", required = false) OrderRejectReason rejectReason) {
        orderService.updateStatus(id, new OrderStatusUpdateDTO(OrderStatus.valueOf(status), lastModifiedDate, rejectReason));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "order statistics", operationId = "getOrderStatistics")
    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<TerminalOrderStatisticsDTO> getStatistics(@ParameterObject OrderFilter filter){
        return ResponseEntity.ok(orderService.getOrderStatisticsForTerminal(filter));
    }
}
