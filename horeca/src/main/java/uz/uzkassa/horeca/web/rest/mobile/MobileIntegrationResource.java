package uz.uzkassa.horeca.web.rest.mobile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.terminal.*;
import uz.uzkassa.horeca.service.BillService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileApayRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileIntegrationResource {

    BillService billService;

    @PostMapping("/bill")
    public ResponseData<ApayCreateBillResponse> createBill(@RequestBody @Valid ApayCreateBillRequest request) {
        return ResponseData.ok(billService.create(request));
    }

    @GetMapping("/bill/anonymous-payment")
    public ResponseEntity<AnonymousPaymentDTO> getAnonymousPayment(@RequestParam String billId) {
        return ResponseEntity.ok(billService.getAnonymousPayment(billId));
    }

    @PutMapping("/bill")
    public ResponseData<ApayUpdateBillResponse> updateBill(@RequestBody @Valid ApayUpdateBillRequest request) {
        return ResponseData.ok(billService.update(request));
    }

    @GetMapping("/cards")
    public ResponseEntity<List<ApayCardSimpleResponse>> getCards(@ParameterObject ApayCardNFCRequest request) {
        return ResponseEntity.ok(billService.getUserCards(request.getNfc(), request.getPin(), request.getBillId()));
    }

    @PutMapping("/pay-bill")
    public ResponseEntity<ApayPayBillResponse> payBill(@RequestBody @Valid ApayPayBillRequest request) {
        return ResponseEntity.ok(billService.payBill(request));
    }

    @GetMapping("/check/{billId}")
    public ResponseEntity<BillCheckStatusResponseDTO> checkBillStatus(@PathVariable String billId) {
        return ResponseEntity.ok(billService.checkStatus(new BillIdRequestDTO(billId)));
    }

    @GetMapping("/card/logo")
    public ResponseEntity<ApayBankLogoDTO> getCardById(@ParameterObject ApayCardPanDTO dto) {
        return ResponseEntity.ok(billService.getCardById(dto.getPan()));
    }
}
