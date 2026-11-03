package uz.uzkassa.horeca.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.order.admin.OrderDetailAdminDTO;
import uz.uzkassa.horeca.dto.order.admin.OrderListAdminDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardDetailDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardListDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderStatisticsDTO;
import uz.uzkassa.horeca.enums.OrderStatus;
import uz.uzkassa.horeca.filters.OrderFilter;
import uz.uzkassa.horeca.service.OrderService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminOrderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminOrderResource {

    OrderService orderService;

    @GetMapping(ApiConstants.id)
    public ResponseEntity<OrderDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderListAdminDTO>> getList(@ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(orderService.getListForAdmin(filter));
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
        if (filter.isMine() && HeaderUtils.isChef()) {
            filter.setChefId(HeaderUtils.getCurrentUserId());
        }
        return ResponseEntity.ok(orderService.getKanbanBoardDetails(filter));
    }

    @Operation(operationId = "getOrderStatuses")
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<OrderStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(OrderStatus.class));
    }
}
