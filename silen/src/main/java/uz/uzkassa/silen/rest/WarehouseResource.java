package uz.uzkassa.silen.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.filter.StockCreateFilter;
import uz.uzkassa.silen.dto.filter.WarehouseFilter;
import uz.uzkassa.silen.dto.warehouse.StockStatsDTO;
import uz.uzkassa.silen.dto.warehouse.WarehouseDTO;
import uz.uzkassa.silen.dto.warehouse.WarehouseOperationStatsDTO;
import uz.uzkassa.silen.service.WarehouseService;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Warehouse}.
 */
@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseResource {
    WarehouseService warehouseService;

    @GetMapping("/stock")
    public ResponseEntity<Page<StockStatsDTO>> stock(@ModelAttribute WarehouseFilter filter) {
        return ResponseEntity.ok(warehouseService.stock(filter));
    }

    @GetMapping("/stock/stats")
    public ResponseEntity<StockStatsDTO> stockStats(@ModelAttribute WarehouseFilter filter) {
        return ResponseEntity.ok(warehouseService.stockStats(filter));
    }

    @GetMapping
    public ResponseEntity<Page<WarehouseDTO>> getAll(@ModelAttribute WarehouseFilter filter) {
        Page<WarehouseDTO> result = warehouseService.findAll(filter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/operations/stats")
    public ResponseEntity<WarehouseOperationStatsDTO> warehouseStatsByOperation(@ModelAttribute WarehouseFilter filter) {
        return ResponseEntity.ok(warehouseService.warehouseStatsByOperation(filter));
    }

    @GetMapping("/operations/clear")
    public ResponseEntity<Void> stockAmountUpdate(@ModelAttribute StockCreateFilter filter) {
        return ResponseEntity.ok(warehouseService.stockAmountUpdate(filter));
    }

    @GetMapping("/operations/zero")
    public ResponseEntity<Void> stockAmountZero(@ModelAttribute StockCreateFilter filter) {
        return ResponseEntity.ok(warehouseService.stockAmountZero(filter));
    }
}
