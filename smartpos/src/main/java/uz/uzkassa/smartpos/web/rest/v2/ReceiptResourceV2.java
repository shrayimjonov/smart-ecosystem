package uz.uzkassa.smartpos.web.rest.v2;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.filter.receipt.CreditAdvanceReceiptFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.receipt.CreditAdvanceReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.ReceiptCheckMarkingDTO;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.ReceiptSmsRequestDTO;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.enums.Source;
import uz.uzkassa.smartpos.service.ReceiptService;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/v2/receipt")
@Slf4j
@RequiredArgsConstructor
public class ReceiptResourceV2 {
    private final ReceiptService receiptService;

    @GetMapping("/drafts")
    public Page<ReceiptDTO> getDrafts(@ParameterObject ReceiptFilter filter) {
        if (filter.getSource() == null) {
            filter.setSources(List.of(Source.CASHBOX, Source.SUPPLY));
        }
        return receiptService.findAllDraftsByBranch(filter);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ReceiptDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(receiptService.getById(id, false));
    }

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkReceiptMarking(@RequestBody ReceiptCheckMarkingDTO dto) {
        return ResponseEntity.ok(receiptService.checkMarking(dto));
    }

    @GetMapping("/credits")
    public Page<CreditAdvanceReceiptDTO> creditAdvanceReceipts(@ParameterObject CreditAdvanceReceiptFilter filter) {
        return receiptService.findAllCreditAdvanceReceipts(filter);
    }

    @GetMapping
    public Page<ReceiptDTO> getList(@ParameterObject ReceiptFilter receiptFilter) {
        receiptFilter.setStatuses(EnumSet.of(ReceiptStatus.PAID, ReceiptStatus.RETURNED));
        return receiptService.getReceipts(receiptFilter, false);
    }

    @PostMapping("/sms")
    public void sendReceiptDetailViaSmsOrEmail(@RequestBody @Valid ReceiptSmsRequestDTO request) {
        receiptService.sendReceiptDetailViaSms(request);
    }

    @GetMapping("/providers")
    public Object paymentProvers(@RequestParam(required = false) Long branchId) {
        return receiptService.paymentProviders(branchId);
    }
}
