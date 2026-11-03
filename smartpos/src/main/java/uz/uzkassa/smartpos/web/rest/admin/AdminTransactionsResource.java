package uz.uzkassa.smartpos.web.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionAdminStatisticsDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionDto;
import uz.uzkassa.smartpos.enums.TransactionStatus;
import uz.uzkassa.smartpos.filter.TransactionFilter;
import uz.uzkassa.smartpos.service.TransactionService;

import java.util.List;

@RequestMapping("/api/v1/admin/transactions")
@RestController
@RequiredArgsConstructor
public class AdminTransactionsResource {
    private final TransactionService transactionService;

    @GetMapping
    public Page<TransactionDto> findAllByFilter(@ParameterObject TransactionFilter filter) {
        filter.setStatus(TransactionStatus.SUCCESS);
        return transactionService.findAllByFilter(filter);
    }

    @GetMapping("/statistics")
    public List<TransactionAdminStatisticsDto> statistics(@ParameterObject TransactionFilter filter) {
        filter.setStatus(TransactionStatus.SUCCESS);
        return transactionService.adminStatistics(filter);
    }
}
