package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.receipt.ReceiptStatisticDTO;
import uz.uzkassa.horeca.dto.receipt.cabinet.ReceiptDetailCabinetDTO;
import uz.uzkassa.horeca.dto.receipt.cabinet.ReceiptListCabinetDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptSmsRequestDTO;
import uz.uzkassa.horeca.dto.terminal.TerminalSource;
import uz.uzkassa.horeca.enums.ReceiptStatus;
import uz.uzkassa.horeca.filters.ReceiptFilter;
import uz.uzkassa.horeca.service.ReceiptService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetReceiptRooApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetReceiptResource {

    ReceiptService receiptService;

    @PostMapping
    public ResponseEntity<TerminalReceiptDTO> create(@RequestBody TerminalReceiptDTO receiptDTO) {
        receiptDTO.setSource(TerminalSource.CASHBOX);
        receiptDTO.setStatus(ReceiptStatus.DRAFT);
        return ResponseEntity.ok(receiptService.create(receiptDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody TerminalReceiptDTO receiptDTO) {
        return ResponseEntity.ok(receiptService.update(id, receiptDTO));
    }

    @GetMapping
    public ResponseEntity<Page<ReceiptListCabinetDTO>> getList(@ParameterObject ReceiptFilter filter) {
        return ResponseEntity.ok(receiptService.getListForCabinet(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<ReceiptDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.getForCabinet(id));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<ReceiptStatisticDTO> getStatistics(@ParameterObject ReceiptFilter filter) {
        return ResponseEntity.ok(receiptService.getStatistics(filter));
    }

    @PostMapping("/sms")
    public ResponseEntity<Void> sendSms(@RequestBody TerminalReceiptSmsRequestDTO receiptSmsRequestDTO) {
        receiptService.sendSms(receiptSmsRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/print")
    public ResponseEntity<Void> print(@RequestBody TerminalReceiptDTO terminalReceiptDTO) {
        receiptService.print(terminalReceiptDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        receiptService.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/by-uid/{uid}")
    public ResponseEntity<Void> deleteByUid(@PathVariable String uid) {
        receiptService.delete(uid);
        return ResponseEntity.ok().build();
    }
}
