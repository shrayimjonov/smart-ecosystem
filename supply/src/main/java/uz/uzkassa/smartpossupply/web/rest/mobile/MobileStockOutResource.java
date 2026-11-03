package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutListDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutLookUpDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.service.StockOutService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.01.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileStockOutRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileStockOutResource {

    StockOutService stockOutService;

    @Operation(summary = ApiConstants.Out)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockOutDTO stockDTO) {
        return ResponseEntity.ok(stockOutService.create(stockDTO));
    }

    @Operation(operationId = "convertFromOrder")
    @PostMapping("/convert/{orderId}")
    public ResponseEntity<Long> convertFromOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(stockOutService.convertFromOrder(orderId));
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

    @Operation(summary = "Get Aggregation Codes")
    @GetMapping("/{stockProductId}/aggregation-codes")
    public ResponseEntity<Map<CodeType, List<CheckedCodeDTO>>> getAggregationCodes(
            @PathVariable Long stockProductId
    ) {
        return ResponseEntity.ok(stockOutService.getAggregationCodes(stockProductId));
    }

    @Operation(summary = ApiConstants.Check)
    @PostMapping(ApiConstants.check)
    public ResponseEntity<List<CisInfoResponse>> batchCheck(@RequestBody @Valid BatchValidationDTO codeCheckDTO) {
        return ResponseEntity.ok(stockOutService.batchCheck(codeCheckDTO));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<StockOutListDTO>> getList(@ParameterObject StockFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        filter.setTransferType(TransferType.OUT);
        return ResponseEntity.ok(stockOutService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<StockOutLookUpDTO>> lookUp(@ParameterObject StockFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setTransferType(TransferType.OUT);
        return ResponseEntity.ok(stockOutService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(StockStatus.DRAFT, StockStatus.CHECKING, StockStatus.CHECKING_ERROR, StockStatus.COMPLETED, StockStatus.CANCELLED));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestParam("status") String status, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(stockOutService.updateStatus(id, StockStatus.valueOf(status), lastModifiedDate));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockOutService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
