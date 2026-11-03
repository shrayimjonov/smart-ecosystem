package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.apay.APayAddTransactionRequestDto;
import uz.uzkassa.cts.dto.apay.APayAddTransactionResponseDto;
import uz.uzkassa.cts.dto.apay.APayPayRequestDto;
import uz.uzkassa.cts.dto.apay.APayPayResponseDto;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.service.APayService;
import uz.uzkassa.cts.service.TokenStoreService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/apay-test")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class APayTestResource {

    APayService service;
    TokenStoreService tokenStoreService;

    @GetMapping
    public ResponseEntity<String> login() {
        return ResponseEntity.ok(tokenStoreService.getToken(IntegrationType.APAY));
    }

    @Operation(summary = "add transaction")
    @PostMapping("add")
    public ResponseEntity<APayAddTransactionResponseDto> add(@RequestBody APayAddTransactionRequestDto dto) {
        return ResponseEntity.ok(service.add(dto));
    }

    @Operation(summary = "confirm transaction")
    @PutMapping("confirm")
    public ResponseEntity<APayPayResponseDto> pay(@RequestBody APayPayRequestDto dto) {
        return ResponseEntity.ok(service.pay(dto));
    }

    @Operation(summary = "confirm transaction")
    @GetMapping("check")
    public ResponseEntity<Void> check() {
        //aPayPaymentCronJob.checkPaymentStatus();
        return ResponseEntity.ok().build();
    }
}
