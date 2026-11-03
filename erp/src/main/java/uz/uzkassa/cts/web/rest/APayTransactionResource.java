package uz.uzkassa.cts.web.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.apaypayment.request.APayFromAppRequestDto;
import uz.uzkassa.cts.dto.apaypayment.request.APayPaymentQRAddRequestDto;
import uz.uzkassa.cts.dto.apaytransaction.request.APayTransactionRequestDto;
import uz.uzkassa.cts.dto.apaytransaction.response.APayClientInfoResponseDto;
import uz.uzkassa.cts.dto.apaytransaction.response.APayTransactionResponseDto;
import uz.uzkassa.cts.dto.apay.APayAddTransactionResponseDto;
import uz.uzkassa.cts.service.APayPaymentService;
import uz.uzkassa.cts.service.APayTransactionService;

@RestController
@RequestMapping("/api/apay-transactions")
@Tag(name = "apay transactions")
@Slf4j
public class APayTransactionResource {

    private final APayTransactionService service;
    private final APayPaymentService aPayPaymentService;

    public APayTransactionResource(APayTransactionService service,
                                   APayPaymentService aPayPaymentService) {
        this.service = service;
        this.aPayPaymentService = aPayPaymentService;
    }

    @Operation(summary ="Add")
    @PostMapping
    public ResponseEntity<APayTransactionResponseDto> create(@Valid @RequestBody APayTransactionRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary ="get info by tin")
    @GetMapping("/get-info-by-tin/{tin}")
    public ResponseEntity<APayClientInfoResponseDto> getInfo(@PathVariable String tin) {
        return ResponseEntity.ok(service.getClientInfoByTin(tin));
    }

    @Operation(summary ="create transaction from apay application")
    @PostMapping("/create-transaction-from-app")
    public ResponseEntity<APayAddTransactionResponseDto> createTransaction(@Valid @RequestBody APayFromAppRequestDto dto) {
        return ResponseEntity.ok(aPayPaymentService.createTransactionFromApp(dto));
    }

    @Operation(summary ="create transaction from apay application")
    @PostMapping("/create-transaction-qr")
    public ResponseEntity<APayAddTransactionResponseDto> createQRTransaction(@Valid @RequestBody APayPaymentQRAddRequestDto dto) {
        return ResponseEntity.ok().body(aPayPaymentService.createQRTransaction(dto));
    }

}
