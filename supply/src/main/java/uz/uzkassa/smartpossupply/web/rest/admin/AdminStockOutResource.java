package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutListDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.StockOutService;

import java.time.LocalDateTime;
import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminStockOutRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminStockOutResource {

    StockOutService stockOutService;

    @Operation(summary = ApiConstants.Out)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockOutDTO stockDTO) {
        return ResponseEntity.ok(stockOutService.create(stockDTO));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockOutDTO stockDTO) {
        return ResponseEntity.ok(stockOutService.update(id, stockDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockOutDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockOutService.get(id));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<StockOutListDTO>> getList(@ParameterObject StockFilter filter) {
        filter.setTransferType(TransferType.OUT);
        filter.setFetchDeleted(true);
        return ResponseEntity.ok(stockOutService.getList(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(StockStatus.class));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestParam("status") String status, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(stockOutService.updateStatus(id, StockStatus.valueOf(status), lastModifiedDate));
    }

    @Operation(summary = ApiConstants.Copy)
    @PutMapping(ApiConstants.copy)
    public ResponseEntity<Long> copy(@PathVariable Long id) {
        return ResponseEntity.ok(stockOutService.copy(id));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockOutService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Reverse)
    @PutMapping(ApiConstants.reverse)
    public ResponseEntity<Void> reverse(@PathVariable Long id) {
        stockOutService.reverse(id);
        return ResponseEntity.ok().build();
    }
}
