package uz.uzkassa.smartpos.web.rest.terminal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.sales.TransactionUpdatePaymentDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionCreateDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionStatisticsDto;
import uz.uzkassa.smartpos.filter.TransactionFilter;
import uz.uzkassa.smartpos.service.TransactionService;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/terminals/transactions")
public class TerminalTransactionResource {
    private final TransactionService transactionService;

    @PutMapping("/payment/{uid}")
    public void receivedPayment(@PathVariable String uid) {
        transactionService.receivedPayment(uid);
    }

    @PutMapping("/payment")
    public void updatePayment(@RequestBody @Valid TransactionUpdatePaymentDto dto) {
        transactionService.updatePayment(dto);
    }

    @PostMapping("/batch")
    public void batchSave(@RequestBody @Valid Set<TransactionCreateDto> transactions) {
        transactionService.batchSave(transactions);
    }

    @GetMapping
    public Page<TransactionDto> findAllByFilter(@ParameterObject TransactionFilter filter) {
        return transactionService.findAllByFilter(filter);
    }

    @GetMapping("/statistics")
    public TransactionStatisticsDto statistics(@ParameterObject TransactionFilter filter) {
        return transactionService.statistics(filter);
    }

    @GetMapping("/{uid}")
    public TransactionDto findByUid(@PathVariable String uid) {
        return transactionService.findByUid(uid);
    }
}
