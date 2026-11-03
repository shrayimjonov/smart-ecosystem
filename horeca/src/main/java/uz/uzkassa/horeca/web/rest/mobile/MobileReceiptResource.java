package uz.uzkassa.horeca.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalSource;
import uz.uzkassa.horeca.enums.OrderStatus;
import uz.uzkassa.horeca.enums.ReceiptPaymentType;
import uz.uzkassa.horeca.enums.ReceiptStatus;
import uz.uzkassa.horeca.service.ReceiptService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.05.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileReceiptRooApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileReceiptResource {

    ReceiptService receiptService;

    @PostMapping
    public ResponseEntity<TerminalReceiptDTO> create(@RequestBody TerminalReceiptDTO receiptDTO) {
        receiptDTO.setSource(TerminalSource.CASHBOX);
        receiptDTO.setStatus(ReceiptStatus.DRAFT);
        return ResponseEntity.ok(receiptService.createFromMobile(receiptDTO));
    }

    @Operation(operationId = "receiptPrint")
    @PostMapping("/print")
    public ResponseEntity<Void> print(@RequestBody TerminalReceiptDTO terminalReceiptDTO) {
        receiptService.print(terminalReceiptDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(operationId = "receiptDelete")
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        receiptService.delete(id);
        return ResponseEntity.ok().build();
    }
}
