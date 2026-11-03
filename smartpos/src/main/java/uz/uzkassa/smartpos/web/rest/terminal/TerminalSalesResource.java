package uz.uzkassa.smartpos.web.rest.terminal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.sales.TransactionCreatePaymentDto;
import uz.uzkassa.smartpos.service.TransactionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/terminals/sales")
public class TerminalSalesResource {
    private final TransactionService transactionService;

    @PostMapping("/payment")
    public String createPayment(@RequestBody @Valid TransactionCreatePaymentDto dto) {
        return transactionService.createPayment(dto);
    }
}
