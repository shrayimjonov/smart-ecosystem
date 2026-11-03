package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderListDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderStatusUpdateDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductLookUpWithWarehouseQtyDTO;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.service.OrderService;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminOrderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminOrderResource {

    OrderService orderService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.create(orderDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<OrderDetailDTO> get(@PathVariable Long id,
                                              @RequestParam(required = false) Boolean fetchQtyInWarehouse) {
        return ResponseEntity.ok(orderService.get(id, fetchQtyInWarehouse));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.update(id, orderDTO));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<OrderStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(OrderStatus.class));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @ParameterObject String status, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        orderService.updateStatus(id, new OrderStatusUpdateDTO(OrderStatus.valueOf(status), lastModifiedDate));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PutMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatusWithPut(@PathVariable Long id, @RequestParam("status") String status, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        orderService.updateStatus(id, new OrderStatusUpdateDTO(OrderStatus.valueOf(status), lastModifiedDate));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<OrderListDTO>> getList(@ParameterObject OrderFilter filter) {
        filter.setFetchDeleted(true);
        return ResponseEntity.ok(orderService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.lookUp(filter));
    }

    @Operation(summary = "Product Lookup")
    @GetMapping("/product/lookup")
    public ResponseEntity<List<ProductLookUpWithWarehouseQtyDTO>> getProductLookUp(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(orderService.getProductLookUp(filter));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Reverse)
    @PutMapping(ApiConstants.reverse)
    public ResponseEntity<Long> reverse(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.reverse(id));
    }
}
