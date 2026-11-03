package uz.uzkassa.smartpos.web.rest.terminal;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionDynamicsDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionStatisticsDto;
import uz.uzkassa.smartpos.filter.TransactionFilter;
import uz.uzkassa.smartpos.service.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cabinet/transactions")
public class CabinetTransactionResource {
    private final TransactionService transactionService;

    @GetMapping
    public Page<TransactionDto> findAllByFilter(@ParameterObject TransactionFilter filter) {
        return transactionService.findAllByFilter(filter);
    }

    @GetMapping("/statistics")
    public TransactionStatisticsDto statistics(@ParameterObject TransactionFilter filter) {
        return transactionService.statistics(filter);
    }

    @GetMapping("/dynamics")
    public List<TransactionDynamicsDto> transactionDynamics(@ParameterObject TransactionFilter filter) {
        return transactionService.transactionDynamics(filter);
    }
}
