package uz.uzkassa.smartpos.repository.postgres.sales;

import uz.uzkassa.smartpos.dto.terminal.sales.TransactionAdminStatisticsDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionDynamicsDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionStatisticsDto;
import uz.uzkassa.smartpos.filter.TransactionFilter;

import java.util.List;

public interface TransactionRepositoryCustom {
    TransactionStatisticsDto statistics(TransactionFilter filter);

    List<TransactionDynamicsDto> transactionDynamics(TransactionFilter filter);

    List<TransactionAdminStatisticsDto> adminStatistics(TransactionFilter filter);
}
