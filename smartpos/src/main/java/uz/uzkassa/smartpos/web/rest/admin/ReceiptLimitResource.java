package uz.uzkassa.smartpos.web.rest.admin;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.ReceiptLimitDto;
import uz.uzkassa.smartpos.service.ReceiptLimitService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/receipt-limit")
public class ReceiptLimitResource {
    private final ReceiptLimitService receiptLimitService;

    @GetMapping
    public List<ReceiptLimitDto> getAllReceiptLimit() {
        return receiptLimitService.getAllReceiptLimit();
    }

    @GetMapping("/for-terminals")
    public ReceiptLimitDto getReceiptLimitForTerminal() {
        return receiptLimitService.getReceiptLimitForTerminal();
    }
}
