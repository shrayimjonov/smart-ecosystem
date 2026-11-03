package uz.uzkassa.silen.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderProductDTO;
import uz.uzkassa.silen.dto.warehouse.PurchaseOrderProductDetailDTO;
import uz.uzkassa.silen.service.PurchaseOrderProductService;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.PurchaseOrder}.
 */
@RestController
@RequestMapping("/api/purchase-order/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PurchaseOrderProductResource {

    PurchaseOrderProductService purchaseOrderProductService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody PurchaseOrderProductDTO dto) {
        return ResponseEntity.ok(purchaseOrderProductService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable("id") Long id, @RequestBody PurchaseOrderProductDTO dto) {
        return ResponseEntity.ok(purchaseOrderProductService.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderProductDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderProductService.findOne(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseOrderProductService.delete(id);
        return ResponseEntity.ok().build();
    }
}
