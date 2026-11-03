package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.order.OrderStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderStatusUpdateDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderUpdateStatusDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderListDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderListForExpeditorDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileReturnOrderDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpWithWarehouseQtyDTO;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.OrderRejectReason;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.enums.ProductOrigin;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.service.OrderService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023
 */

@RestController
@RequestMapping(ApiConstants.mobileOrderRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileOrderResource {

    OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody MobileOrderDTO mobileOrderDTO) {
        return ResponseEntity.ok(orderService.create(mobileOrderDTO));
    }

    @PostMapping(ApiConstants.offline)
    public ResponseEntity<MobileReturnOrderDTO> createOffline(@RequestBody MobileOrderDTO mobileOrderDTO) {
        return ResponseEntity.ok(orderService.createOffline(mobileOrderDTO));
    }

    @Operation(summary = ApiConstants.List, operationId = "getOrderList")
    @GetMapping
    public ResponseEntity<Page<MobileOrderListDTO>> getList(@ParameterObject OrderFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(orderService.getListForMobile(filter));
    }

    @Operation(summary = ApiConstants.Get, operationId = "getOrder")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<MobileOrderDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getForMobile(id));
    }

    @GetMapping(ApiConstants.offline)
    public ResponseEntity<List<MobileOrderDetailDTO>> getOfflineOrders(@RequestParam("agentId") Long agentId,
                                                                       @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(orderService.getForMobileOffline(agentId, date));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status, @RequestParam(value = "lastModifiedDate", required = false) LocalDateTime lastModifiedDate, @RequestParam(value = "rejectReason", required = false) OrderRejectReason rejectReason) {
        orderService.updateStatus(id, new OrderStatusUpdateDTO(OrderStatus.valueOf(status), lastModifiedDate, rejectReason));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Multiple order update status", operationId = "multipleOrderUpdateStatus")
    @PatchMapping("/status/multiple")
    public ResponseEntity<Void> updateStatusMultiple(@RequestBody List<OrderUpdateStatusDTO> orders, @RequestParam("status") String status) {
        orderService.updateStatus(orders, OrderStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.updateDeliveryDate)
    public ResponseEntity<Void> updateDeliveryDate(@PathVariable Long id, @RequestParam("deliveryDate") LocalDateTime deliveryDate) {
        orderService.updateDeliveryDate(id, deliveryDate);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Update, operationId = "updateOrder")
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody MobileOrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.update(id, orderDTO));
    }

    @Operation(summary = "Assign expeditor", operationId = "orderAssignExpeditor")
    @PatchMapping("/assign/expeditor/{id}")
    public ResponseEntity<Long> assignExpeditor(@PathVariable Long id, @RequestParam(name = "expeditorId") Long expeditorId) {
        orderService.assignExpeditor(id, expeditorId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Product Lookup", operationId = "productLookup")
    @GetMapping("/product/lookup")
    public ResponseEntity<Page<ProductLookUpWithWarehouseQtyDTO>> getProductLookUp(@ParameterObject ProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        filter.setBranchId(Optional.ofNullable(filter.getBranchId()).orElse(SecurityUtils.getCurrentBranchId()));
        filter.setWarehouseId(Optional.ofNullable(filter.getWarehouseId()).orElse(SecurityUtils.getCurrentWarehouseId()));
        filter.setEnvironmentType(EnvironmentType.WEB.getCode());
        return ResponseEntity.ok(orderService.getProductLookUpForMobile(filter));
    }

    @Operation(summary = "Recommended Qty", operationId = "getRecommendedQty")
    @GetMapping("/products/recommended-qty/{productId}")
    public ResponseEntity<BigDecimal> getRecommendedQty(@PathVariable Long productId) {
        return ResponseEntity.ok(orderService.getRecommendedQty(productId));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<OrderStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(OrderStatus.class));
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

    @Operation(operationId = "getOrderListForExpeditor")
    @GetMapping("/expeditors")
    public ResponseEntity<Page<MobileOrderListForExpeditorDTO>> getOrderListForExpeditor(@ParameterObject OrderFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null || filter.getLatitude() == null || filter.getLongitude() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(orderService.getListForExpeditor(filter));
    }

    @Operation(operationId = "getOrderStatistics")
    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<OrderStatisticsDTO> getStatistics(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getStatistics(filter));
    }
}