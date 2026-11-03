package uz.uzkassa.smartpos.web.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.analytics.ReceiptStatDto;
import uz.uzkassa.smartpos.dto.filter.receipt.ReceiptFilter;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.enums.ReceiptStatus;
import uz.uzkassa.smartpos.service.ReceiptService;

import java.util.EnumSet;

import static uz.uzkassa.smartpos.security.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/admin/receipts")
@Secured({SMARTPOS_ADMIN, SUPPORT, SALES_MANAGER})
@RequiredArgsConstructor
public class AdminReceiptResource {
    private final ReceiptService receiptService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable Long id) {
        receiptService.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/draft")
    public ResponseEntity<Void> deleteDraftReceipts(@RequestParam Long branchId) {
        receiptService.deleteDraftReceipts(branchId);
        return ResponseEntity.ok().build();
    }


    @GetMapping
    public Page<ReceiptDTO> getReceipts(@ParameterObject ReceiptFilter receiptFilter) {
        receiptFilter.setStatuses(EnumSet.of(ReceiptStatus.PAID, ReceiptStatus.RETURNED));
        return receiptService.getReceipts(receiptFilter, true);
    }

    @GetMapping("/stat")
    public ReceiptStatDto getReceiptStat(@ParameterObject ReceiptFilter receiptFilter) {
        receiptFilter.setStatuses(EnumSet.of(ReceiptStatus.PAID, ReceiptStatus.RETURNED));
        return receiptService.getReceiptStat(receiptFilter, true);
    }
}
