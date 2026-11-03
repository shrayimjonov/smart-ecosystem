package uz.uzkassa.smartpossupply.web.rest.terminal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.receipt.ReceiptListDTO;
import uz.uzkassa.smartpossupply.dto.receipt.TerminalReceiptDTO;
import uz.uzkassa.smartpossupply.dto.receipt.TerminalReceiptUpdatePaymentDTO;
import uz.uzkassa.smartpossupply.enums.ReceiptPaymentType;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;
import uz.uzkassa.smartpossupply.filters.ReceiptFilter;
import uz.uzkassa.smartpossupply.service.ReceiptService;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 08.10.2024
 */
@Slf4j
@RestController
@RequestMapping(ApiConstants.terminalReceiptRootApi)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TerminalReceiptResource {

    ReceiptService receiptService;
    ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody TerminalReceiptDTO receiptDTO) {
        return ResponseEntity.ok(receiptService.create(receiptDTO));
    }

    @PostMapping("/batch")
    public ResponseEntity<Void> batchCreate(@RequestBody List<TerminalReceiptDTO> receipts) {
        receiptService.batchCreate(receipts);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/convert/{stockOutId}")
    public ResponseEntity<Void> convertFromStockOutV2(@PathVariable Long stockOutId,
                                                      @RequestParam(value = "ignoreForeignMark", required = false) Boolean ignoreForeignMark,
                                                      @RequestHeader Map<String, String> headers) {
        receiptService.convertFromStockOut(stockOutId, ignoreForeignMark);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/convert/stock-return/{stockReturnId}")
    public ResponseEntity<Void> convertFromStockReturn(@PathVariable Long stockReturnId, @RequestHeader Map<String, String> headers,
                                                       @RequestParam(value = "ignoreForeignMark", required = false) Boolean ignoreForeignMark) {
        receiptService.convertFromStockReturn(stockReturnId, ignoreForeignMark);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReceiptListDTO>> getList(@ParameterObject ReceiptFilter filter) {
        return ResponseEntity.ok(receiptService.getList(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<TerminalReceiptDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.get(id));
    }

    @GetMapping("/by-uid/{uid}")
    public ResponseEntity<TerminalReceiptDTO> getByUid(@PathVariable String uid) {
        return ResponseEntity.ok(receiptService.get(uid));
    }

    @GetMapping("/stock/{stockOutId}")
    public ResponseEntity<List<TerminalReceiptDTO>> getByStockOutId(@PathVariable Long stockOutId) {
        return ResponseEntity.ok(receiptService.getAllByStockOutId(stockOutId));
    }

    @PatchMapping("/stock/{stockOutId}")
    public ResponseEntity<Void> updatePaymentType(@PathVariable Long stockOutId, @RequestBody TerminalReceiptUpdatePaymentDTO updatePaymentDTO) {
        receiptService.updatePayment(stockOutId, updatePaymentDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<ReceiptStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(ReceiptStatus.class));
    }

    @GetMapping(ApiConstants.paymentTypes)
    public ResponseEntity<EnumSet<ReceiptPaymentType>> getPaymentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ReceiptPaymentType.class));
    }
}
