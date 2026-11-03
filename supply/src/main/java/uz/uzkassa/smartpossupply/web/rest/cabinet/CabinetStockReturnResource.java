package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.dto.stockreturn.StockReturnDTO;
import uz.uzkassa.smartpossupply.dto.stockreturn.StockReturnDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockreturn.StockReturnListDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.StockReturnService;
import uz.uzkassa.smartpossupply.service.handlers.excel.StockReturnListStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 09.03.2023 17:16
 */
@RestController
@RequestMapping(ApiConstants.cabinetStockReturnRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetStockReturnResource {

    StockReturnService stockReturnService;
    StockReturnListStrategy stockReturnListStrategy;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockReturnDTO stockReturnDTO) {
        return ResponseEntity.ok(stockReturnService.create(stockReturnDTO));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockReturnDTO stockReturnDTO) {
        return ResponseEntity.ok(stockReturnService.update(id, stockReturnDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockReturnDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockReturnService.get(id));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<StockReturnListDTO>> getList(@ParameterObject StockFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(stockReturnService.getList(filter));
    }

    @Operation(summary = ApiConstants.Copy)
    @PutMapping(ApiConstants.copy)
    public ResponseEntity<Long> copy(@PathVariable Long id) {
        return ResponseEntity.ok(stockReturnService.copy(id));
    }

    @Operation(summary = "Get Aggregation Codes")
    @GetMapping("/aggregation-codes/{stockProductId}")
    public ResponseEntity<Map<CodeType, List<CheckedCodeDTO>>> getAggregationCodes(@PathVariable Long stockProductId) {
        return ResponseEntity.ok(stockReturnService.getAggregationCodes(stockProductId));
    }

    @Operation(summary = ApiConstants.Check)
    @PostMapping(ApiConstants.check)
    public ResponseEntity<List<CisInfoResponse>> check(@RequestBody @Valid BatchValidationDTO validationDTO) {
        return ResponseEntity.ok(stockReturnService.check(validationDTO));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(StockStatus.DRAFT, StockStatus.CHECKING, StockStatus.CHECKING_ERROR, StockStatus.COMPLETED, StockStatus.CANCELLED));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestParam("status") String status, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(stockReturnService.updateStatus(id, StockStatus.valueOf(status), lastModifiedDate));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockReturnService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportCSV)
    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Void> exportCsv(@ParameterObject StockFilter filter) {
        stockReturnListStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Resource> exportExcel(@ParameterObject StockFilter filter) {
        stockReturnListStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "Assign cashier")
    @PatchMapping("/assign/cashier/{id}")
    public ResponseEntity<Long> assignCashier(@PathVariable Long id, @RequestParam(name = "cashierId") Long cashierId) {
        stockReturnService.assignCashier(id, cashierId);
        return ResponseEntity.ok().build();
    }
}
