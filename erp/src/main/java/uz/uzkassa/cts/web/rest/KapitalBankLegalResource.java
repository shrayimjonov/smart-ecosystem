package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.kapitallegal.request.KapitalLegalSentToBillingRequestDto;
import uz.uzkassa.cts.dto.kapitallegal.response.*;
import uz.uzkassa.cts.filter.KapitalLegalFilter;
import uz.uzkassa.cts.service.KapitalBankLegalService;
import uz.uzkassa.cts.dto.kapitallegal.request.KapitalLegalGetDocInfoRequest;
import uz.uzkassa.cts.dto.kapitallegal.request.KapitalLegalGetDocsRequest;
import uz.uzkassa.cts.dto.kapitallegal.request.KapitalLegalPaymentRequest;
import uz.uzkassa.cts.service.KapitalLegalPaymentDetailService;
import uz.uzkassa.cts.service.KapitalLegalPaymentService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/kapital-bank-legal")
@Tag(name = "Kapital Bank Legal")
@Slf4j
public class KapitalBankLegalResource {

    private final KapitalBankLegalService service;
    private final KapitalLegalPaymentService kapitalLegalPaymentService;
    private final KapitalLegalPaymentDetailService kapitalLegalPaymentDetailService;

    public KapitalBankLegalResource(KapitalBankLegalService service, KapitalLegalPaymentService kapitalLegalPaymentService, KapitalLegalPaymentDetailService kapitalLegalPaymentDetailService) {
        this.service = service;
        this.kapitalLegalPaymentService = kapitalLegalPaymentService;
        this.kapitalLegalPaymentDetailService = kapitalLegalPaymentDetailService;
    }

    @Operation(summary ="authenticate")
    @GetMapping("/authenticate")
    public ResponseEntity<KapitalLegalLoginResponse> authenticate() {
        log.debug("REST request to Kapital Bank Legal auth");
        KapitalLegalLoginResponse result = service.authenticate();
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Выписка по счету")
    @GetMapping("/get-documents")
    public ResponseEntity<KapitalLegalDoc1CResponse> getDocuments(KapitalLegalGetDocsRequest request) {
        log.debug("REST request to getDocs: {}", request);
        KapitalLegalDoc1CResponse result = service.get1CDocuments(request);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Остатки на счетах")
    @GetMapping("/get-account-info")
    public ResponseEntity<KapitalLegalGetDocInfoResponse> getAccountInfo(KapitalLegalGetDocInfoRequest request) {
        log.debug("REST request to getInfo: {}", request);
        KapitalLegalGetDocInfoResponse result = service.getAccountInfo(request);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Отправка платежного поручения")
    @GetMapping("/create-payment")
    public ResponseEntity<KapitalLegalSendPaymentResponse> cretePayment(KapitalLegalPaymentRequest request) {
        log.debug("REST request to createPayment: {}", request);
        KapitalLegalSendPaymentResponse result = service.sendPayment(request);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="Проверка состояний отправленных платежных поручений")
    @GetMapping("/check-payment")
    public ResponseEntity<KapitalLegalCheckPaymentResponse> checkPayment(KapitalLegalPaymentRequest request) {
        log.debug("REST request checkPayments: {}", request);
        KapitalLegalCheckPaymentResponse result = service.checkPayment(request);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="test insertion to db")
    @GetMapping("/sync")
    public ResponseEntity<Void> check() {
        log.debug("REST request check");
        kapitalLegalPaymentService.checkForPayments();
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="test insertion to db")
    @GetMapping("/sync/{date}")
    public ResponseEntity<Void> syncByDate(@PathVariable String date) {
        log.debug("REST request check");
        kapitalLegalPaymentService.syncByDate(date);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="List")
    @GetMapping("/list")
    public ResponseEntity<Page<KapitalLegalResponseDto>> getAll(KapitalLegalFilter filter) {
        log.debug("REST request to get all kapital legal : {}", filter);
        return ResponseEntity.ok(kapitalLegalPaymentDetailService.list(filter));
    }

    @Operation(summary ="get amount")
    @GetMapping("/getAmount")
    public ResponseEntity<BigDecimal> getAmount(KapitalLegalFilter filter) {
        log.debug("card payment amount sum request: {}", filter);
        return ResponseEntity.ok(kapitalLegalPaymentDetailService.getAmount(filter));
    }


    @Operation(summary ="send to billing")
    @PostMapping("/send-to-billing")
    public ResponseEntity<Void> list(@RequestBody KapitalLegalSentToBillingRequestDto ids) {
        log.debug("REST request to resend to bankClient: {}", ids);
        kapitalLegalPaymentDetailService.sendToBilling(ids.getIds());
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="send to billing cron")
    @PostMapping("/send-to-billing-cron")
    public ResponseEntity<Void> cronTest() {
        log.debug("REST request to send billing cron");
        kapitalLegalPaymentDetailService.sentToBillingCronJob();
        return ResponseEntity.ok().build();
    }
}
