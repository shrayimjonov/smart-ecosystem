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
import uz.uzkassa.smartpossupply.dto.stock.StockProductLookUpDTO;
import uz.uzkassa.smartpossupply.dto.stockin.StockInDTO;
import uz.uzkassa.smartpossupply.dto.stockin.StockInDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockin.StockInListDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockBalanceReport;
import uz.uzkassa.smartpossupply.dto.stockreport.StockReportDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.filters.StockInProductFilter;
import uz.uzkassa.smartpossupply.service.StockInService;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminStockInRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminStockInResource {

    StockInService stockInService;

    @Operation(summary = ApiConstants.In)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockInDTO stockInDTO) {
        return ResponseEntity.ok(stockInService.create(stockInDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockInDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockInService.get(id, null));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockInDTO stockInDTO) {
        return ResponseEntity.ok(stockInService.update(id, stockInDTO));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<StockInListDTO>> getList(@ParameterObject StockFilter filter) {
        filter.setFetchDeleted(true);
        return ResponseEntity.ok(stockInService.getList(filter));
    }

    @Operation(summary = "Balance Report")
    @GetMapping(ApiConstants.balanceReport)
    public ResponseEntity<Page<StockBalanceReport>> getBalanceReport(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(stockInService.getBalanceReport(filter));
    }

    @Operation(summary = ApiConstants.Report)
    @GetMapping(ApiConstants.report)
    public ResponseEntity<Page<StockReportDTO>> getReport(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(stockInService.getReport(filter));
    }

    @Deprecated
    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<StockProductLookUpDTO>> lookUp(@ParameterObject StockInProductFilter filter) {
        filter.setTransferType(TransferType.IN.name());
        return ResponseEntity.ok(stockInService.lookUpProduct(filter));
    }

    @Operation(summary = "Product Lookup")
    @GetMapping("/product/lookup")
    public ResponseEntity<List<StockProductLookUpDTO>> lookUpProduct(@ParameterObject StockInProductFilter filter) {
        filter.setTransferType(TransferType.IN.name());
        return ResponseEntity.ok(stockInService.lookUpProduct(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(StockStatus.class));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestParam("status") String status, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(stockInService.updateStatus(id, StockStatus.valueOf(status), lastModifiedDate));
    }

    @Operation(summary = ApiConstants.Copy)
    @PutMapping(ApiConstants.copy)
    public ResponseEntity<Long> copy(@PathVariable Long id) {
        return ResponseEntity.ok(stockInService.copy(id));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockInService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
