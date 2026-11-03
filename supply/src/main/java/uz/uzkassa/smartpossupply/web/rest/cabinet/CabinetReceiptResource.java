package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.receipt.*;
import uz.uzkassa.smartpossupply.enums.ReceiptPaymentType;
import uz.uzkassa.smartpossupply.enums.ReceiptStatus;
import uz.uzkassa.smartpossupply.filters.ReceiptFilter;
import uz.uzkassa.smartpossupply.service.ReceiptService;
import uz.uzkassa.smartpossupply.service.SmartposReceiptService;

import java.util.EnumSet;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.12.2022 15:32
 */
@RestController
@RequestMapping(ApiConstants.cabinetReceiptRootApi)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CabinetReceiptResource {

    ReceiptService receiptService;
    SmartposReceiptService smartposReceiptService;

    @GetMapping
    public ResponseEntity<Page<ReceiptListDTO>> getList(@ParameterObject ReceiptFilter filter) {
        return ResponseEntity.ok(receiptService.getList(filter));
    }


    @GetMapping(ApiConstants.id)
    public ResponseEntity<CabinetReceiptDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.getForCabinet(id));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<ReceiptStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(ReceiptStatus.class));
    }

    @Operation(summary = ApiConstants.Send)
    @PostMapping(ApiConstants.send)
    public ResponseEntity<Void> send(@RequestBody @Valid ReceiptSendDTO sendDTO) {
        smartposReceiptService.send(sendDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-uid/{uid}")
    public ResponseEntity<SmartposReceiptDTO> getByUid(@PathVariable String uid) {
        return ResponseEntity.ok(smartposReceiptService.getByUID(uid));
    }

    @GetMapping("/stock/{stockOutId}")
    public ResponseEntity<Void> getByStockOutId(@PathVariable Long stockOutId) {
        smartposReceiptService.getByStockOutId(stockOutId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<ReceiptStatisticDTO> getStatistics(@ParameterObject ReceiptFilter filter) {
        return ResponseEntity.ok(receiptService.getStatistics(filter));
    }

    @GetMapping("/divide-receipt")
    public ResponseEntity<List<SmartposReceiptDTO>> divideReceipt(@RequestBody SmartposReceiptDTO smartposReceiptDTO) {
        return ResponseEntity.ok(smartposReceiptService.divideReceipt(smartposReceiptDTO));
    }

    @GetMapping(ApiConstants.paymentTypes)
    public ResponseEntity<EnumSet<ReceiptPaymentType>> getPaymentTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ReceiptPaymentType.class));
    }
}
