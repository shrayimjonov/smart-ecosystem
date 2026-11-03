package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CheckStatusResponseDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderItemCabinetDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderItemDetailCabinetDTO;
import uz.uzkassa.horeca.dto.order.cabinet.OrderItemMarkDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderItemStatisticsDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderListDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderItemChefStatsMapperDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderItemCommentDTO;
import uz.uzkassa.horeca.enums.OrderItemStatus;
import uz.uzkassa.horeca.filters.ChefStatsFilter;
import uz.uzkassa.horeca.filters.OrderItemFilter;
import uz.uzkassa.horeca.service.OrderItemService;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.05.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileOrderItemRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileOrderItemResource {

    OrderItemService orderItemService;

    @Operation(operationId = "orderItemDelete")
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<CheckStatusResponseDTO> delete(@PathVariable Long id, @RequestParam LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(orderItemService.delete(id, lastModifiedDate));
    }

    @Operation(operationId = "orderItemGetStatistics")
    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<OrderItemChefStatsMapperDTO> getStatistics(@ParameterObject ChefStatsFilter filter) {
        return ResponseEntity.ok(orderItemService.getMobileChefStatistics(filter));
    }

    @Operation(operationId = "orderItemUpdateStatus")
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestParam("status") String status, @RequestParam LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(orderItemService.updateStatus(id, OrderItemStatus.valueOf(status), lastModifiedDate));
    }

    @Operation(operationId = "orderItemUpdateComment")
    @PatchMapping(ApiConstants.updateComment)
    public ResponseEntity<Long> updateComment(@PathVariable Long id, @RequestBody OrderItemCommentDTO orderItemCommentDTO) {
        return ResponseEntity.ok(orderItemService.updateComment(id, orderItemCommentDTO));
    }

    @Operation(operationId = "orderItemGetKanbanBoardList")
    @GetMapping("/kanban-board-list")
    public ResponseEntity<List<OrderListDTO>> getKanbanBoardList(@ParameterObject OrderItemFilter filter) {
        return ResponseEntity.ok(orderItemService.getKanbanBoardList(filter));
    }

    @Operation(operationId = "orderItemUpdate")
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody OrderItemCabinetDTO orderItemDTO) {
        return ResponseEntity.ok(orderItemService.update(id, orderItemDTO));
    }

    @Operation(operationId = "orderItemDeleteMark")
    @DeleteMapping(ApiConstants.id + "/marks")
    public ResponseEntity<Long> deleteMark(@PathVariable Long id, @RequestBody OrderItemMarkDTO markDTO) {
        return ResponseEntity.ok(orderItemService.deleteMark(id, markDTO));
    }

    @Operation(operationId = "orderItemAddMark")
    @PostMapping(ApiConstants.id + "/marks")
    public ResponseEntity<Long> addMark(@PathVariable Long id, @RequestBody OrderItemMarkDTO markDTO) {
        return ResponseEntity.ok(orderItemService.addMark(id, markDTO));
    }

    @Operation(operationId = "orderItemReplaceMark")
    @PutMapping(ApiConstants.id + "/marks")
    public ResponseEntity<Long> replaceMark(@PathVariable Long id, @RequestBody OrderItemMarkDTO markDTO) {
        return ResponseEntity.ok(orderItemService.replaceMark(id, markDTO));
    }

    @Operation(operationId = "orderItemGetDetail")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<OrderItemDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.get(id));
    }
}
