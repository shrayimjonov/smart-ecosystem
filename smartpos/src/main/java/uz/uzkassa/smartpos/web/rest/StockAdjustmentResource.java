package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.filter.StockAdjustmentFilter;
import uz.uzkassa.smartpos.dto.warehouse.StockAdjustmentDTO;
import uz.uzkassa.smartpos.dto.warehouse.StockAdjustmentItemDTO;
import uz.uzkassa.smartpos.enums.StockAdjustmentReason;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.service.StockAdjustmentService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/warehouse/stock-adjustments")
@Secured({AuthoritiesConstants.SUPPORT, AuthoritiesConstants.SALES_MANAGER, AuthoritiesConstants.OWNER, AuthoritiesConstants.BRANCH_ADMIN, AuthoritiesConstants.SMARTPOS_ADMIN})
public class StockAdjustmentResource {
    private final StockAdjustmentService stockAdjustmentService;

    public StockAdjustmentResource(StockAdjustmentService stockAdjustmentService) {
        this.stockAdjustmentService = stockAdjustmentService;
    }

    @PostMapping
    public ResponseEntity<StockAdjustmentDTO> create(@Valid @RequestBody StockAdjustmentDTO stockAdjustment) {
        StockAdjustmentDTO result = stockAdjustmentService.createNew(stockAdjustment);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public Page<StockAdjustmentItemDTO> getAll(@ParameterObject StockAdjustmentFilter filter) {
        return stockAdjustmentService.getAllAdjustmentItems(filter);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockAdjustmentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(stockAdjustmentService.getStockAdjustment(id));
    }

    @GetMapping("/reasons")
    public List<StockAdjustmentReason> getReasons() {
        return Arrays.asList(StockAdjustmentReason.values());
    }
}
