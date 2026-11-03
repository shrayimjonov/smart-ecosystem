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
import uz.uzkassa.smartpossupply.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.dto.stockadjustment.*;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.filters.StockAdjustmentFilter;
import uz.uzkassa.smartpossupply.integration.EimzoClient;
import uz.uzkassa.smartpossupply.service.StockAdjustmentService;
import uz.uzkassa.smartpossupply.service.handlers.excel.StockAdjustmentAggregationCodesStrategy;
import uz.uzkassa.smartpossupply.service.handlers.excel.StockAdjustmentListStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetStockAdjustmentRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetStockAdjustmentResource {

    StockAdjustmentService stockAdjustmentService;
    StockAdjustmentListStrategy stockAdjustmentListStrategy;
    EimzoClient eimzoClient;
    StockAdjustmentAggregationCodesStrategy stockAdjustmentAggregationCodesStrategy;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody StockAdjustmentDTO stockAdjustmentDTO) {
        return ResponseEntity.ok(stockAdjustmentService.create(stockAdjustmentDTO));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody StockAdjustmentDTO stockAdjustmentDTO) {
        return ResponseEntity.ok(stockAdjustmentService.update(id, stockAdjustmentDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockAdjustmentDetailDTO> get(@PathVariable Long id, @RequestParam(required = false) boolean withCodes) {
        return ResponseEntity.ok(stockAdjustmentService.get(id, withCodes));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<StockAdjustmentListDTO>> getList(@ParameterObject StockAdjustmentFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(stockAdjustmentService.getList(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockAdjustmentStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(StockAdjustmentStatus.class));
    }

    @Operation(summary = ApiConstants.Reasons)
    @GetMapping(ApiConstants.reasons)
    public ResponseEntity<EnumSet<AdjustmentReason>> getReasons() {
        return ResponseEntity.ok(EnumSet.allOf(AdjustmentReason.class));
    }

    @Operation(summary = ApiConstants.documentTypes)
    @GetMapping(ApiConstants.documentTypes)
    public ResponseEntity<EnumSet<DocumentType>> getDocumentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DocumentType.class));
    }

    @Operation(summary = "Reject Reasons")
    @GetMapping("/reject" + ApiConstants.reasons)
    public ResponseEntity<EnumSet<AdjustmentRejectReason>> getRejectReasons() {
        return ResponseEntity.ok(EnumSet.allOf(AdjustmentRejectReason.class));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestBody @Valid AdjustmentStatusUpdateDTO statusUpdateDTO, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(stockAdjustmentService.updateStatus(id, statusUpdateDTO, lastModifiedDate));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockAdjustmentService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportCSV)
    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Void> exportCsv(@ParameterObject StockAdjustmentFilter filter) {
        stockAdjustmentListStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Aggregation codes export Csv")
    @GetMapping("/{id}/codes" + ApiConstants.exportCsv)
    public ResponseEntity<Void> exportCsv(@PathVariable Long id) {
        StockAdjustmentFilter filter = new StockAdjustmentFilter();
        filter.setId(id);
        stockAdjustmentAggregationCodesStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Resource> exportExcel(@ParameterObject StockAdjustmentFilter filter) {
        stockAdjustmentListStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Aggregation codes export Excel")
    @GetMapping("/{id}/codes" + ApiConstants.exportExcel)
    public ResponseEntity<Void> exportExcel(@PathVariable Long id) {
        StockAdjustmentFilter filter = new StockAdjustmentFilter();
        filter.setId(id);
        stockAdjustmentAggregationCodesStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Withdraw)
    @PutMapping(ApiConstants.withdraw)
    public ResponseEntity<Long> withdraw(@PathVariable Long id, @RequestBody @Valid WithdrawRequestDTO withdrawRequestDTO) {
        final Pkcs7Dto pkcs7Dto = eimzoClient.frontTimestamp(withdrawRequestDTO.getSignature());
        if (pkcs7Dto.isSuccess()) {
            withdrawRequestDTO.setSignature(pkcs7Dto.getPkcs7b64());
            return ResponseEntity.ok(stockAdjustmentService.withdraw(id, withdrawRequestDTO));
        } else {
            throw new BadRequestException("Not verified, reason: " + pkcs7Dto.getMessage());
        }
    }

    @Operation(summary = ApiConstants.Check)
    @PostMapping(ApiConstants.check)
    public ResponseEntity<List<CisInfoResponse>> check(@RequestBody @Valid BatchValidationDTO validationDTO) {
        return ResponseEntity.ok(stockAdjustmentService.check(validationDTO));
    }

    @Operation(summary = "Get Aggregation Codes")
    @GetMapping("/{stockAdjustmentProductId}/aggregation-codes")
    public ResponseEntity<Map<CodeType, List<CheckedCodeDTO>>> getAggregationCodes(@PathVariable Long stockAdjustmentProductId) {
        return ResponseEntity.ok(stockAdjustmentService.getAggregationCodes(stockAdjustmentProductId));
    }
}
