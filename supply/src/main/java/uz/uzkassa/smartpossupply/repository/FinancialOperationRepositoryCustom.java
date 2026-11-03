package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.FinancialOperation;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.customer.AccountReceivableMapDTO;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.FinancialOperationFilter;
import uz.uzkassa.smartpossupply.filters.ReconciliationActFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;

import java.math.BigDecimal;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 09.01.2024
 */
@Repository
public interface FinancialOperationRepositoryCustom {

    ResultList<FinancialOperation> getResultList(FinancialOperationFilter filter);

    FinancialOperationStatisticsDTO getStatistics(FinancialOperationFilter filter);

    ResultList<AccountReceivableMapDTO> getAccountReceivableReport(StockFilter filter);

    ResultList<FinancialOperation> getResultList(ReconciliationActFilter filter);

    BigDecimal calculationPaymentAmount(ReconciliationActFilter filter);

    Object[] calculationDebitAndCredit(ReconciliationActFilter filter);
}
