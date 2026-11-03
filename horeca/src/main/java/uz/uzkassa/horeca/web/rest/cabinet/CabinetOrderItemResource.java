package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CheckStatusResponseDTO;
import uz.uzkassa.horeca.dto.order.cabinet.*;
import uz.uzkassa.horeca.dto.order.kanban.OrderItemStatisticsDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderListDTO;
import uz.uzkassa.horeca.enums.OrderItemStatus;
import uz.uzkassa.horeca.filters.OrderItemFilter;
import uz.uzkassa.horeca.service.OrderItemService;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetOrderItemRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetOrderItemResource {

    OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody OrderItemCabinetDTO orderItemDTO) {
        return ResponseEntity.ok(orderItemService.create(orderItemDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody OrderItemCabinetDTO orderItemDTO) {
        return ResponseEntity.ok(orderItemService.update(id, orderItemDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<OrderItemDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderItemDetailCabinetDTO>> getList(@ParameterObject OrderItemFilter filter) {
        return ResponseEntity.ok(orderItemService.getList(filter));
    }


    @GetMapping("/kanban-board-list")
    public ResponseEntity<List<OrderListDTO>> getKanbanBoardList(@ParameterObject OrderItemFilter filter) {
        return ResponseEntity.ok(orderItemService.getKanbanBoardList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<CheckStatusResponseDTO> delete(@PathVariable Long id, @RequestParam LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(orderItemService.delete(id, lastModifiedDate));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestParam("status") OrderItemStatus status, @RequestParam LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(orderItemService.updateStatus(id, status, lastModifiedDate));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<OrderItemStatisticsDTO> getStatistics(@ParameterObject OrderItemFilter filter) {
        return ResponseEntity.ok(orderItemService.getStatistics(filter));
    }

    @DeleteMapping(ApiConstants.id + "/marks")
    public ResponseEntity<Long> deleteMark(@PathVariable Long id, @RequestBody OrderItemMarkDTO markDTO) {
        return ResponseEntity.ok(orderItemService.deleteMark(id, markDTO));
    }

    @PostMapping(ApiConstants.id + "/marks")
    public ResponseEntity<Long> addMark(@PathVariable Long id, @RequestBody OrderItemMarkDTO markDTO) {
        return ResponseEntity.ok(orderItemService.addMark(id, markDTO));
    }

    @PutMapping(ApiConstants.id + "/marks")
    public ResponseEntity<Long> replaceMark(@PathVariable Long id, @RequestBody OrderItemMarkDTO markDTO) {
        return ResponseEntity.ok(orderItemService.replaceMark(id, markDTO));
    }

    @PostMapping("/marks/check")
    public ResponseEntity<MarkCheckResponseDTO> check(@RequestBody MarkCheckDTO markDTO) {
        return ResponseEntity.ok(orderItemService.check(markDTO));
    }
}
