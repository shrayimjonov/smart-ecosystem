package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.apay.APayAddTransactionResponseDto;
import uz.uzkassa.cts.dto.apaypayment.request.APayChangeStatusRequestDto;
import uz.uzkassa.cts.dto.apaypayment.request.APayPaymentAddRequestDto;
import uz.uzkassa.cts.dto.apaypayment.request.APayPaymentPayRequestDto;
import uz.uzkassa.cts.dto.apaypayment.response.APayPaymentResponseDto;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoice1cResponse;
import uz.uzkassa.cts.filter.APayPaymentFilter;
import uz.uzkassa.cts.service.APayPaymentService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/apay-payment")
@Tag(name = "apay payments")
@Slf4j
public class APayPaymentResource {

    private final APayPaymentService service;

    public APayPaymentResource(APayPaymentService service) {
        this.service = service;
    }

    @Operation(summary = "create transaction")
    @PostMapping("/create-transaction")
    public ResponseEntity<APayAddTransactionResponseDto> create(@Valid @RequestBody APayPaymentAddRequestDto dto) {
        return ResponseEntity.ok(service.createTransaction(dto));
    }

    @Operation(summary = "confirm transaction")
    @PostMapping("/confirm-transaction")
    public ResponseEntity<APayAddTransactionResponseDto> confirm(@RequestBody APayPaymentPayRequestDto dto) {
        service.acceptPayment(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "get info")
    @GetMapping("/list")
    public ResponseEntity<Page<APayPaymentResponseDto>> getInfo(APayPaymentFilter filter) {
        log.debug("card payment request filter: {}", filter);
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary = "get info")
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(APayPaymentFilter filter) {
        log.debug("card payment request filter: {}", filter);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"a-pay-payments.xlsx\"")
                .body(service.download(filter));
    }

    @Operation(summary = "get info")
    @GetMapping("/getAmount")
    public ResponseEntity<BigDecimal> getAmount(APayPaymentFilter filter) {
        log.debug("card payment amount sum request: {}", filter);
        return ResponseEntity.ok(service.getConfirmedAmount(filter));
    }

    @Operation(summary = "get info")
    @GetMapping("/list1c")
    public ResponseEntity<Page<OneTimeOrderInvoice1cResponse>> list1c(APayPaymentFilter filter) {
        log.debug("card payment request filter: {}", filter);
        return ResponseEntity.ok(service.list1c(filter));
    }

    @Operation(summary = "change status")
    @PutMapping("/change-status")
    public ResponseEntity<APayPaymentResponseDto> changeStatus(@RequestBody APayChangeStatusRequestDto dto) {
        log.debug("change status: {}", dto.toString());
        return ResponseEntity.ok(service.changeStatus(dto));
    }
}
