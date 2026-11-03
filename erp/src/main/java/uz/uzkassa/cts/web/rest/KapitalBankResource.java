package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.domain.PaymentSystemSettings;
import uz.uzkassa.cts.dto.apaypayment.request.APayPaymentAddRequestDto;
import uz.uzkassa.cts.dto.apaypayment.request.APayPaymentPayRequestDto;
import uz.uzkassa.cts.dto.kapitalbank.request.*;
import uz.uzkassa.cts.dto.kapitalbank.response.*;
import uz.uzkassa.cts.enums.PaymentSystem;
import uz.uzkassa.cts.dto.apay.APayAddTransactionResponseDto;
import uz.uzkassa.cts.service.APayPaymentService;
import uz.uzkassa.cts.service.KapitalBankService;
import uz.uzkassa.cts.service.PaymentSystemSettingsService;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/kapitalbank")
@Tag(name = "Kapitalbank payment")
@Slf4j
public class KapitalBankResource {

    private final KapitalBankService kapitalBankService;
    private final APayPaymentService aPayPaymentService;
    private final PaymentSystemSettingsService paymentSystemSettingsService;

    public KapitalBankResource(KapitalBankService kapitalBankService,
                               APayPaymentService aPayPaymentService,
                               PaymentSystemSettingsService paymentSystemSettingsService) {
        this.kapitalBankService = kapitalBankService;
        this.aPayPaymentService = aPayPaymentService;
        this.paymentSystemSettingsService = paymentSystemSettingsService;
    }

    @Operation(summary ="Get info")
    @GetMapping("/info")
    public ResponseEntity<InfoResponseDto> getInfo(@RequestParam(name = "pan") String pan, @RequestParam(name = "expiry") String expiry) {
        InfoRequestDto dto = new InfoRequestDto(pan, expiry);
        log.debug("REST request to get pan: {}", dto);
        return ResponseEntity.ok(kapitalBankService.getInfo(dto));
    }

    @Operation(summary ="Create Transaction")
    @PostMapping("/create-transaction")
    public ResponseEntity<TransactionCreateResponseDto> createTransaction(@Valid @RequestBody TransactionCreateRequestDto dto) {
        log.debug("REST request to create transaction: {}", dto.toString());
        return ResponseEntity.ok(kapitalBankService.createTransaction(dto));
    }

    @Operation(summary ="Pay")
    @PostMapping("/pay")
    public ResponseEntity<PayResponseDto> pay(@Valid @RequestBody PayRequestDto dto) {
        log.debug("REST request to pay: {}", dto.toString());
        return ResponseEntity.ok(kapitalBankService.pay(dto));
    }

    @Operation(summary ="Check")
    @PostMapping("/check")
    public ResponseEntity<CheckStatusResponseDto> check(@Valid @RequestBody CheckStatusRequestDto dto) {
        log.debug("REST request to check: {}", dto.toString());
        return ResponseEntity.ok(kapitalBankService.check(dto));
    }

    @Operation(summary ="Get client info by device serial")
    @GetMapping("/client-info/device/{serialNumber}")
    public ResponseEntity<CustomerCheckResponseDto> getInfo(@PathVariable String serialNumber) {
        log.debug("REST request to get client info by device serial: {}", serialNumber);
        return ResponseEntity.ok(kapitalBankService.getClientInfoByDeviceSerial(serialNumber));
    }

    @Operation(summary ="Get client info by tin")
    @GetMapping("/client-info/tin/{tin}")
    public ResponseEntity<CustomerCheckResponseDto> getInfoByTin(@PathVariable String tin) {
        log.debug("REST request to get client info by tin: {}", tin);
        return ResponseEntity.ok(kapitalBankService.getClientInfoByTin(tin));
    }

    @Operation(summary ="Create payment")
    @PostMapping(value = {"/crete-payment", "/create-payment"})
    public ResponseEntity<TransactionCreateResponseDto> createPayment(@Valid @RequestBody CreatePaymentRequestDto dto) {
        log.debug("REST request to create-payment: {}", dto.toString());
        PaymentSystemSettings settings = paymentSystemSettingsService.getFirstRow();

        if (settings == null || PaymentSystem.KAPITAL_BANK.equals(settings.getPaymentSystem()))
            return ResponseEntity.ok(kapitalBankService.createPayment(dto));
        else {
            APayAddTransactionResponseDto responseDto = aPayPaymentService.createTransaction(new APayPaymentAddRequestDto(dto.getTin(), dto.getPan(), expireReplacePosition(dto.getExpiry()), dto.getDeviceSerialNumber(), dto.getAmount()));
            //APayAddTransactionResponseDto responseDto = aPayPaymentService.createTransaction(new APayPaymentAddRequestDto(dto.getTin(), dto.getPan(), dto.getExpiry(), dto.getDeviceSerialNumber(), dto.getAmount()));
            TransactionCreateResponseDto result = new TransactionCreateResponseDto();
            result.setTimestamp(ZonedDateTime.of(responseDto.getTime(), ZoneId.systemDefault()).toInstant().toEpochMilli());
            TransactionDataResponseDto resultBody = new TransactionDataResponseDto();
            resultBody.setId(responseDto.getLocalId());
            resultBody.setMaskedPhoneNumber(responseDto.getMaskedPhoneNumber());
            result.setData(resultBody);
            result.setStatus("OK");
            return ResponseEntity.ok(result);
        }
//        return ResponseEntity.ok(kapitalBankService.createPayment(dto));
    }

    private String expireReplacePosition(String expire) {
        String[] expireSplit = expire.split("(?<=\\G.{2})");
        return expireSplit[1] + expireSplit[0];
    }

    @Operation(summary ="Accept Payment")
    @PostMapping("/accept-payment")
    public ResponseEntity<Void> acceptPayment(@Valid @RequestBody PayRequestDto dto) {
        log.debug("REST request to create-payment: {}", dto.toString());
        PaymentSystemSettings settings = paymentSystemSettingsService.getFirstRow();
        if (settings == null || PaymentSystem.KAPITAL_BANK.equals(settings.getPaymentSystem()))
            kapitalBankService.acceptPayment(dto);
        else
            aPayPaymentService.acceptPayment(new APayPaymentPayRequestDto(dto.getCashId(), dto.getSms(), dto.getTransactionId()));
//        kapitalBankService.acceptPayment(dto);
        return ResponseEntity.ok().build();
    }


}
