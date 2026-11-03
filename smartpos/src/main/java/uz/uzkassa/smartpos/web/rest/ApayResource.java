package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.dto.AnonymousPaymentDTO;
import uz.uzkassa.smartpos.dto.ResponseData;
import uz.uzkassa.smartpos.integration.apay.ApayClient;
import uz.uzkassa.smartpos.integration.apay.CheckCompanyDTO;
import uz.uzkassa.smartpos.integration.apay.request.*;
import uz.uzkassa.smartpos.integration.apay.response.*;
import uz.uzkassa.smartpos.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpos.service.ApayService;
import uz.uzkassa.smartpos.service.TokenStoreService;

import java.util.List;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;

@Slf4j
@RestController
@RequestMapping("/api/apay")
@RequiredArgsConstructor
public class ApayResource {
    private final ApayService apayService;
    private final ApayClient apayClient;
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final TokenStoreService tokenStoreService;

    @PostMapping("/bill")
    public ResponseData<ApayCreateBillResponse> createBill(@RequestBody @Valid ApayCreateBillRequest request) {
        ApayCreateBillResponse response = apayService.createBill(request);
        return ResponseData.ok(response);
    }

    @PutMapping("/bill")
    public ResponseEntity<ApayUpdateBillResponse> updateBill(@RequestBody @Valid ApayUpdateBillRequest request) {
        return apayClient.updateBill(tokenStoreService.getToken(TokenType.APAY), request);
    }

    @GetMapping("/check/{billId}")
    public ResponseEntity<BillCheckStatusResponseDTO> checkBillStatus(@PathVariable final String billId) {
        return apayClient.checkBillStatus(tokenStoreService.getToken(TokenType.APAY), new BillIdRequestDTO(billId));
    }

    @PutMapping("/attach-receipt")
    public ResponseData<?> attachReceipt(@RequestBody @Valid final ApayAttachCheckRequest request) {
        apayClient.attachCheck(tokenStoreService.getToken(TokenType.APAY), request);
        return ResponseData.ok();
    }

    @PutMapping("/pay-bill")
    public ResponseEntity<ApayPayBillResponse> payBill(@RequestBody @Valid ApayPayBillRequest request) {
        return apayClient.payBill(tokenStoreService.getToken(TokenType.APAY), request);
    }

    @GetMapping("/cards")
    public ResponseEntity<List<ApayCardSimpleResponse>> getCards(@ParameterObject ApayCardNFCRequest request) {
        return apayClient.getUserCards(tokenStoreService.getToken(TokenType.APAY), request.getNfc(), request.getPin(), request.getBillId());
    }

    @GetMapping("/card/logo")
    public ResponseEntity<ApayBankLogoDTO> getCardById(@ParameterObject ApayCardPanDTO dto) {
        return apayClient.getCardById(tokenStoreService.getToken(TokenType.APAY), dto.getPan());
    }

    @PostMapping("/voucher")
    public ResponseEntity<VoucherDTO> getVoucher(@RequestBody ApayVoucherRequest dto) {
        return ResponseEntity.ok(apayService.getVoucher(dto));
    }

    @Secured(value = {SMARTPOS_ADMIN, SUPPORT, SUPPORT_MANAGER, SALES_MANAGER, REGION_MANAGER, SALES_DIRECTOR, OWNER})
    @PostMapping("/create-transaction")
    public ResponseEntity<APayAddTransactionResponseDto> create(@RequestBody APayPaymentAddRequestDto dto) {
        return ResponseEntity.ok().body(billingServiceFeignClient.createTransaction(tokenStoreService.getToken(TokenType.BILLING), dto));
    }


    @Secured(value = {SMARTPOS_ADMIN, SUPPORT, SUPPORT_MANAGER, SALES_MANAGER, REGION_MANAGER, SALES_DIRECTOR, OWNER})
    @PostMapping("/create-transaction-qr")
    public ResponseEntity<APayQrTransactionResponseDto> createQr(@RequestBody APayPaymentQrRequestDto dto) {
        return ResponseEntity.ok().body(billingServiceFeignClient.createTransactionQr(tokenStoreService.getToken(TokenType.BILLING), dto));
    }

    @Secured(value = {SMARTPOS_ADMIN, SUPPORT, SUPPORT_MANAGER, SALES_MANAGER, REGION_MANAGER, SALES_DIRECTOR, OWNER})
    @PostMapping("/confirm-transaction")
    public ResponseEntity<APayAddTransactionResponseDto> confirm(@RequestBody APayPaymentPayRequestDto dto) {
        billingServiceFeignClient.acceptPayment(tokenStoreService.getToken(TokenType.BILLING), dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tariffs")
    public ResponseEntity<List<ApayTariffDTO>> getTariffs() {
        return apayClient.tariffs(tokenStoreService.getToken(TokenType.APAY));
    }


    @GetMapping("/tariffs/{id}")
    public ResponseEntity<ApayTariffDTO> getTariffs(@PathVariable String id) {
        return apayClient.tariffById(tokenStoreService.getToken(TokenType.APAY), id);
    }

    @GetMapping("/bills/anonymous-payment")
    public ResponseEntity<AnonymousPaymentDTO> getAnonymousPayment(@RequestParam String billId) {
        return apayClient.anonymousPayment(tokenStoreService.getToken(TokenType.APAY), billId);
    }

    @GetMapping("/company/check/{tin}")
    public ResponseEntity<CheckCompanyDTO> checkCompany(@PathVariable String tin) {
        return apayClient.checkCompanyExist(tokenStoreService.getToken(TokenType.APAY), tin);
    }
}
