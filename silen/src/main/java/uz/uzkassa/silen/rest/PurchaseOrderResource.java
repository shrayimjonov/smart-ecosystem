package uz.uzkassa.silen.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.PurchaseOrderFilter;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderDTO;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderDetailDTO;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderListDTO;
import uz.uzkassa.silen.enumeration.PurchaseOrderStatus;
import uz.uzkassa.silen.enumeration.PurchaseOrderType;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.PurchaseOrderService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.PurchaseOrder}.
 */
@RestController
@RequestMapping("/api/purchase-order")
@RequiredArgsConstructor
public class PurchaseOrderResource {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<PurchaseOrderDTO> create(@RequestBody PurchaseOrderDTO dto) {
        if (dto.getId() != null) {
            throw new BadRequestException("A new Purchase Order cannot already have an ID");
        }
        purchaseOrderService.create(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> update(@PathVariable("id") Long id, @RequestBody PurchaseOrderDTO dto) {
        purchaseOrderService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<PurchaseOrderStatus> updateStatus(@PathVariable("id") Long id, @PathVariable("status") PurchaseOrderStatus status) {
        return ResponseEntity.ok(purchaseOrderService.updateStatus(id, status));
    }

    @PutMapping("/convert/{id}")
    public ResponseEntity<String> convertToShipment(@PathVariable("id") Long id) {
        return ResponseEntity.ok(purchaseOrderService.convertToShipment(id));
    }

    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> getItems(@ModelAttribute PurchaseOrderFilter filter) {
        return ResponseEntity.ok(purchaseOrderService.getItems(filter));
    }

    @GetMapping
    public ResponseEntity<Page<PurchaseOrderListDTO>> getAll(@ModelAttribute PurchaseOrderFilter filter) {
        return ResponseEntity.ok(purchaseOrderService.findAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDetailDTO> get(@PathVariable Long id) {
        PurchaseOrderDetailDTO result = purchaseOrderService.get(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<EnumDTO>> getStatuses() {
        return ResponseEntity.ok(PurchaseOrderStatus.getAll());
    }

    @GetMapping("/order-types")
    public ResponseEntity<List<EnumDTO>> getOrderTypes() {
        return ResponseEntity.ok(PurchaseOrderType.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseOrderService.delete(id);
        return ResponseEntity.ok().build();
    }
}
