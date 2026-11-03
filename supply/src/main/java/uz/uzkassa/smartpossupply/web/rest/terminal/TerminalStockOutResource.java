package uz.uzkassa.smartpossupply.web.rest.terminal;

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
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalStockOutStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalTopProductsStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.dto.stockout.MobileStockOutProductDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutListDTO;
import uz.uzkassa.smartpossupply.dto.stockout.TerminalStockOutDTO;
import uz.uzkassa.smartpossupply.dto.stockout.TerminalStockOutProductUpdateDTO;
import uz.uzkassa.smartpossupply.dto.stockout.terminal.TerminalStockOutDetailDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
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
@RequestMapping(ApiConstants.terminalStockOutRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalStockOutResource {

    StockOutService stockOutService;

    @Operation(summary = ApiConstants.Out)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody TerminalStockOutDTO stockDTO) {
        return ResponseEntity.ok(stockOutService.create(stockDTO));
    }

    @Operation(operationId = "convertFromOrder")
    @PostMapping("/convert/{orderId}")
    public ResponseEntity<Long> convertFromOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(stockOutService.convertFromOrder(orderId));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody TerminalStockOutDTO stockDTO) {
        return ResponseEntity.ok(stockOutService.update(id, stockDTO));
    }

    @Operation(summary = "Update stock products")
    @PutMapping("/products/" + ApiConstants.id)
    public ResponseEntity<Long> updateProducts(@PathVariable Long id, @RequestBody List<TerminalStockOutProductUpdateDTO> products) {
        return ResponseEntity.ok(stockOutService.updateProduct(id, products));
    }

    @Operation(summary = "Stock out product update")
    @PutMapping("/products/{stockProductId}")
    public ResponseEntity<Long> updateMark(@PathVariable Long stockProductId, @RequestBody MobileStockOutProductDTO stockOutProductDTO) {
        return ResponseEntity.ok(stockOutService.updateMark(stockProductId, stockOutProductDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<TerminalStockOutDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockOutService.getForTerminal(id));
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
        return ResponseEntity.ok(stockOutService.getListForTerminal(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(StockStatus.DRAFT, StockStatus.COMPLETED, StockStatus.CANCELLED));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestParam("status") String status, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(stockOutService.updateStatus(id, StockStatus.valueOf(status), lastModifiedDate));
    }

    @Operation(summary = "Update payment type", operationId = "stockOutUpdatePaymentType")
    @PatchMapping("/{id}/payment-type")
    public ResponseEntity<CheckStatusResponseDTO> updatePaymentType(@PathVariable Long id, @RequestParam("paymentType") String paymentType, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(stockOutService.updatePaymentType(id, PaymentType.valueOf(paymentType), lastModifiedDate));
    }

    @Operation(summary = "Payment Types", operationId = "getPaymentTypes")
    @GetMapping(ApiConstants.paymentTypes)
    public ResponseEntity<EnumSet<PaymentType>> getPaymentTypes() {
        return ResponseEntity.ok(EnumSet.of(PaymentType.CASH, PaymentType.CARD, PaymentType.TRANSFER));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockOutService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete stock product")
    @DeleteMapping("/products/{stockProductId}")
    public ResponseEntity<Void> deleteStockProduct(@PathVariable Long stockProductId) {
        stockOutService.deleteStockProduct(stockProductId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "statistics")
    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<TerminalStockOutStatisticsDTO> getStockOutStatistics(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockOutService.getStockOutStatisticForTerminal(filter));
    }

    @Operation(summary = "top products")
    @GetMapping("/top-products")
    public ResponseEntity<Page<TerminalTopProductsStatisticsDTO>> getTopProducts(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockOutService.getTopProducts(filter));
    }
}
