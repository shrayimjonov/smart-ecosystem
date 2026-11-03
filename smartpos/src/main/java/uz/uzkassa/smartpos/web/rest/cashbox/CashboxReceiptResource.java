package uz.uzkassa.smartpos.web.rest.cashbox;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.receipt.TransitReceiptDTO;
import uz.uzkassa.smartpos.dto.sales.SalesCreateDto;
import uz.uzkassa.smartpos.service.ReceiptService;
import uz.uzkassa.smartpos.service.SalesService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cashbox/receipts")
public class CashboxReceiptResource {
    private final ReceiptService receiptService;
    private final SalesService salesService;

    @PostMapping("/{uid}/send")
    public void sendReceipt(@PathVariable String uid) {
        receiptService.sendReceipt(uid);
    }

    @PostMapping("/transit")
    public void transitReceipt(@RequestBody @Valid SalesCreateDto dto) {
        salesService.transit(dto);
    }

    @GetMapping("/transit/all")
    public List<ReceiptDTO> getAllTransitReceiptsDetail(@RequestParam String facturaId) {
        return receiptService.findAllReceiptsDetailByFacturaId(facturaId);
    }

    @GetMapping("/transit/{facturaId}/all")
    public List<TransitReceiptDTO> getAllTransitReceipts(@PathVariable String facturaId) {
        return receiptService.findAllReceiptsByFacturaId(facturaId);
    }
}
