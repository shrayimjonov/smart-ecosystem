package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.UpdateStatusDTO;
import uz.uzkassa.horeca.dto.order.OrderDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardDetailDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardListDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderStatisticsDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderDetailMobileDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderItemCommentDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderItemMobileDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderListMobileDTO;
import uz.uzkassa.horeca.dto.terminal.ApayCreateBillRequest;
import uz.uzkassa.horeca.dto.terminal.ApayCreateBillResponse;
import uz.uzkassa.horeca.enums.OrderReason;
import uz.uzkassa.horeca.enums.OrderStatus;
import uz.uzkassa.horeca.filters.OrderFilter;
import uz.uzkassa.horeca.service.OrderService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.02.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileOrderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileOrderResource {

    OrderService orderService;

    @Operation(operationId = "orderCreate")
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody OrderDTO orderDTO) {
        orderDTO.setWaiterId(HeaderUtils.getCurrentUserId());
        return ResponseEntity.ok(orderService.create(orderDTO));
    }

    @Operation(operationId = "orderUpdate")
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        orderDTO.setWaiterId(HeaderUtils.getCurrentUserId());
        return ResponseEntity.ok(orderService.update(id, orderDTO));
    }

    @Operation(operationId = "orderAddItem")
    @PutMapping(ApiConstants.id + "/items")
    public ResponseEntity<Long> addItem(@PathVariable Long id, @RequestBody List<OrderItemMobileDTO> items) {
        return ResponseEntity.ok(orderService.addItem(id, items));
    }

    @Operation(operationId = "orderGetDetail")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<OrderDetailMobileDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getForMobile(id));
    }

    @Operation(operationId = "orderUpdateStatus")
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusDTO updateStatusDTO) {
        orderService.updateStatus(id, updateStatusDTO);
        return ResponseEntity.ok().build();
    }


    @Operation(operationId = "orderUpdateComment")
    @PatchMapping(ApiConstants.updateComment)
    public ResponseEntity<Long> updateComment(@PathVariable Long id, @RequestBody OrderItemCommentDTO orderItemCommentDTO) {
        return ResponseEntity.ok(orderService.updateComment(id, orderItemCommentDTO));
    }

    @Operation(operationId = "orderTransfer")
    @PutMapping("/transfer/{tableId}")
    public ResponseEntity<Void> transfer(@RequestBody List<Long>orderIds, @PathVariable Long tableId) {
        orderService.transfer(orderIds, tableId);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "getOrderStatuses")
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<OrderStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(OrderStatus.class));
    }

    @Operation(operationId = "getOrderReasons")
    @GetMapping("/reasons")
    public ResponseEntity<EnumSet<OrderReason>> getOrderReasons() {
        return ResponseEntity.ok(EnumSet.allOf(OrderReason.class));
    }

    @Operation(operationId = "orderGetList")
    @GetMapping
    public ResponseEntity<Page<OrderListMobileDTO>> getList(@ParameterObject OrderFilter filter) {
        filter.setWaiterId(HeaderUtils.getCurrentUserId());
        return ResponseEntity.ok(orderService.getListForMobile(filter));
    }

    @Operation(operationId = "orderGetStatistics")
    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<OrderStatisticsDTO> getStatistics(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getStatistics(filter));
    }

    @Operation(operationId = "orderGetKanbanBoardList")
    @GetMapping("/kanban-board-list")
    public ResponseEntity<List<OrderKanbanBoardListDTO>> getKanbanBoardList(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getKanbanBoardList(filter));
    }

    @Operation(operationId = "orderGetKanbanBoardDetails")
    @GetMapping("/kanban-board-details")
    public ResponseEntity<List<OrderKanbanBoardDetailDTO>> getKanbanBoardDetails(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getKanbanBoardDetails(filter));
    }
}