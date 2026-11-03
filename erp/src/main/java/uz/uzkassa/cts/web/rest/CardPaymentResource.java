package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.kapitalbankpayment.response.KapitalbankPaymentResponseDto;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoice1cResponse;
import uz.uzkassa.cts.filter.KapitalBankFilter;
import uz.uzkassa.cts.service.KapitalbankPaymentService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/card-payment")
@Tag(name = "payments via debit and credit cards")
@Slf4j
public class CardPaymentResource {

    private final KapitalbankPaymentService service;

    public CardPaymentResource(KapitalbankPaymentService service) {
        this.service = service;
    }

    @Operation(summary ="get info")
    @GetMapping("/list")
    public ResponseEntity<Page<KapitalbankPaymentResponseDto>> getInfo(KapitalBankFilter filter) {
        log.debug("card payment request filter: {}", filter);
        Page<KapitalbankPaymentResponseDto> result = service.list(filter);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="get info")
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(KapitalBankFilter filter) {
        log.debug("card payment request filter: {}", filter);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"a-pay-payments.xlsx\"")
            .body(service.getDownloadUrl(filter));
    }

    @Operation(summary ="get info")
    @GetMapping("/getAmount")
    public ResponseEntity<BigDecimal> getAmount(KapitalBankFilter filter) {
        log.debug("card payment amount sum request: {}", filter);
        return ResponseEntity.ok(service.getConfirmedAmount(filter));
    }

    @Operation(summary ="get info")
    @GetMapping("/list1c")
    public ResponseEntity<Page<OneTimeOrderInvoice1cResponse>> getlist(KapitalBankFilter filter) {
        log.debug("card payment request filter: {}", filter);
        Page<OneTimeOrderInvoice1cResponse> result = service.list1c(filter);
        return ResponseEntity.ok(result);
    }
}
