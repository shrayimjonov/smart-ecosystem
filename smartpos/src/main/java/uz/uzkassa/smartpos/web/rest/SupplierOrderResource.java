package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.integration.enums.DeliveryType;
import uz.uzkassa.smartpos.integration.enums.OrderStatus;
import uz.uzkassa.smartpos.integration.enums.PaymentType;
import uz.uzkassa.smartpos.integration.enums.SaleType;
import uz.uzkassa.smartpos.integration.supply.dto.*;
import uz.uzkassa.smartpos.integration.supply.filter.CommentFilter;
import uz.uzkassa.smartpos.integration.supply.filter.OrderFilter;
import uz.uzkassa.smartpos.service.OrderService;

import java.util.EnumSet;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class SupplierOrderResource {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid OrderCreateDTO orderCreateDTO) {
        return ResponseEntity.ok(orderService.create(orderCreateDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody @Valid OrderCreateDTO orderCreateDTO) {
        return ResponseEntity.ok(orderService.update(id, orderCreateDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok().body(orderService.get(id));
    }

    @GetMapping("/statuses")
    public ResponseEntity<EnumSet<OrderStatus>> getStatuses() {
        return ResponseEntity.ok().body(EnumSet.allOf(OrderStatus.class));
    }

    @GetMapping("/sale-types")
    public ResponseEntity<EnumSet<SaleType>> getSaleTypes() {
        return ResponseEntity.ok().body(EnumSet.allOf(SaleType.class));
    }

    @GetMapping("/payment-types")
    public ResponseEntity<EnumSet<PaymentType>> getPaymentTypes() {
        return ResponseEntity.ok().body(EnumSet.allOf(PaymentType.class));
    }


    @GetMapping("/delivery-types")
    public ResponseEntity<EnumSet<DeliveryType>> getDeliveryTypes() {
        return ResponseEntity.ok().body(EnumSet.allOf(DeliveryType.class));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        orderService.updateStatus(id, OrderStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status/webhook")
    public ResponseEntity<Void> statusWebhook(@PathVariable Long id, @RequestBody OrderWebhookDto dto) {
        orderService.statusWebhook(id, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<OrderListDTO>> getList(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getList(filter));
    }

    @GetMapping("/lookup")
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.lookUp(filter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<Long> createComment(@PathVariable Long id, @RequestBody @Valid CreateCommentDTO dto) {
        return orderService.createComment(id, dto);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Object> getComments(@PathVariable Long id, @ParameterObject CommentFilter filter) {
        return orderService.getComments(id, filter);
    }
}
