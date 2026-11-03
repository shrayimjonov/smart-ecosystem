package uz.uzkassa.horeca.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.UpdateStatusDTO;
import uz.uzkassa.horeca.dto.order.OrderDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderDetailCabinetDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderListCabinetDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderShortListCabinetDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardDetailDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardListDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderStatisticsDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderItemMobileDTO;
import uz.uzkassa.horeca.enums.OrderStatus;
import uz.uzkassa.horeca.enums.StockStatus;
import uz.uzkassa.horeca.filters.OrderFilter;
import uz.uzkassa.horeca.service.OrderService;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetOrderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetOrderResource {

    OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.create(orderDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.update(id, orderDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<OrderDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getForCabinet(id));
    }

    @Deprecated
    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<OrderShortListCabinetDTO>> getByTableId(@PathVariable Long tableId) {
        return ResponseEntity.ok(orderService.getByTableId(tableId));
    }

    @GetMapping
    public ResponseEntity<Page<OrderListCabinetDTO>> getList(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getListForCabinet(filter));
    }

    @GetMapping("/kanban-board-list")
    public ResponseEntity<List<OrderKanbanBoardListDTO>> getKanbanBoardList(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getKanbanBoardList(filter));
    }

    @GetMapping("/kanban-board-details")
    public ResponseEntity<List<OrderKanbanBoardDetailDTO>> getKanbanBoardDetails(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getKanbanBoardDetails(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        orderService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO) {
        orderService.updateStatus(id, updateStatusDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.updateNumber)
    public ResponseEntity<Void> updateNumber(@PathVariable Long id, @RequestParam String orderNumber) {
        orderService.updateNumber(id, orderNumber.trim());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/assign-waiter/{waiterId}")
    public ResponseEntity<Void> assignWaiter(@PathVariable Long id, @PathVariable Long waiterId) {
        orderService.assignWaiter(id, waiterId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/transfer/{tableId}")
    public ResponseEntity<Void> transfer(@RequestBody List<Long> orderIds, @PathVariable Long tableId) {
        orderService.transfer(orderIds, tableId);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "orderAddItem")
    @PatchMapping(ApiConstants.id + "/items")
    public ResponseEntity<Long> addItem(@PathVariable Long id, @RequestBody List<OrderItemMobileDTO> items) {
        return ResponseEntity.ok(orderService.addItem(id, items));
    }

    @Operation(operationId = "orderGetStatistics")
    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<OrderStatisticsDTO> getStatistics(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getStatistics(filter));
    }

    @Operation(operationId = "getOrderStatuses")
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<OrderStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(OrderStatus.class));
    }

}
