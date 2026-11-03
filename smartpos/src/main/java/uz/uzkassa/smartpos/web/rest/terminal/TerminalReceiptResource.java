package uz.uzkassa.smartpos.web.rest.terminal;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.sales.SalesCreateDto;
import uz.uzkassa.smartpos.service.ReceiptService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/terminals/receipts")
public class TerminalReceiptResource {
    private final ReceiptService receiptService;

    @GetMapping("/advance/order")
    public Integer getAdvanceOrderFromOriginUid(@RequestParam String originUid) {
        return receiptService.getAdvanceOrderFromOriginUid(originUid);
    }

    @GetMapping(value = "/origin/{uid}")
    public ResponseEntity<List<ReceiptDTO>> getByOriginUid(@PathVariable("uid") String uid) {
        return ResponseEntity.ok(receiptService.getAllByOriginUid(uid));
    }

    @PostMapping("/batch")
    public void batch(@RequestBody @NotEmpty List<SalesCreateDto> receipts) {
        receiptService.saveReceiptListV2(receipts);
    }
}
