package uz.uzkassa.horeca.web.rest.terminal;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.terminal.TerminalReceiptDTO;
import uz.uzkassa.horeca.filters.ReceiptFilter;
import uz.uzkassa.horeca.service.ReceiptService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.05.2024
 */
@RestController
@RequestMapping(ApiConstants.terminalReceiptRooApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TerminalReceiptResource {

    ReceiptService receiptService;

    @GetMapping
    public ResponseEntity<List<TerminalReceiptDTO>> getDraftReceipts(ReceiptFilter filter) {
        return ResponseEntity.ok(receiptService.getListForTerminal(filter));
    }
}
