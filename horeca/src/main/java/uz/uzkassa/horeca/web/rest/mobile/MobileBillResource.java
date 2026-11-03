package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.terminal.*;
import uz.uzkassa.horeca.service.BillService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileBillRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileBillResource {

    BillService billService;

    @Operation(operationId = "createBill")
    @PostMapping("/{orderId}")
    public ResponseEntity<ApayCreateBillResponse> createBill(@PathVariable Long orderId, @RequestBody @Valid ApayCreateBillRequest apayCreateBillRequest) {
        return ResponseEntity.ok(billService.createForOrder(orderId, apayCreateBillRequest));
    }

    @Operation(operationId = "updateBill")
    @PutMapping
    public ResponseEntity<ApayUpdateBillResponse> update(@RequestBody @Valid ApayUpdateBillRequest apayUpdateBillRequest) {
        return ResponseEntity.ok(billService.update(apayUpdateBillRequest));
    }

    @Operation(operationId = "checkBillStatus")
    @GetMapping("/{orderId}")
    public ResponseEntity<List<BillCheckStatusResponseDTO>> checkBillStatus(@PathVariable Long orderId) {
        return ResponseEntity.ok(billService.checkStatus(orderId));
    }

    @Operation(operationId = "updateBillStatus")
    @PatchMapping("/{billId}")
    public ResponseEntity<Void> updateStatus(@PathVariable String billId) {
        billService.updateStatus(billId);
        return ResponseEntity.ok().build();
    }
}
