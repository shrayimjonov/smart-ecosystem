package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.EnumDTO;
import uz.uzkassa.silen.dto.StoreInDTO;
import uz.uzkassa.silen.dto.StoreInDetailsDTO;
import uz.uzkassa.silen.dto.StoreInListDTO;
import uz.uzkassa.silen.dto.filter.StoreInFilter;
import uz.uzkassa.silen.enumeration.StockInStatus;
import uz.uzkassa.silen.enumeration.TransferType;
import uz.uzkassa.silen.service.StoreInService;

import java.util.List;

@RestController
@RequestMapping("/api/store-in")
@RequiredArgsConstructor
public class StoreInResource {
    private final StoreInService storeInService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid StoreInDTO storeInDTO) {
        return ResponseEntity.ok(storeInService.create(storeInDTO));
    }

    @GetMapping
    public ResponseEntity<Page<StoreInListDTO>> findAllByFilter(StoreInFilter filter) {
        filter.setTransferType(TransferType.RETURN);
        return ResponseEntity.ok(storeInService.findAllByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreInDetailsDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(storeInService.findById(id));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<EnumDTO>> getStatuses() {
        return ResponseEntity.ok(StockInStatus.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreInDetailsDTO> update(@PathVariable String id, @RequestBody StoreInDTO storeInDTO) {
        return ResponseEntity.ok(storeInService.update(id, storeInDTO));
    }

    @PutMapping("/close/{id}")
    public ResponseEntity<Void> close(@PathVariable String id) {
        storeInService.complete(id, StockInStatus.COMPLETED);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/shipment/{id}")
    public ResponseEntity<String> returnByShipmentId(@PathVariable String id) {
        return ResponseEntity.ok(storeInService.returnByShipmentId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        storeInService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/convert/{id}")
    public ResponseEntity<Long> convertToInvoice(@PathVariable String id, @RequestParam(required = false) String oldFacturaId) {
        return ResponseEntity.ok(storeInService.createStoreInInvoice(id, oldFacturaId));
    }

}
