package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.LedgerDetail;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.ledger.response.*;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.filter.BalanceFilter;
import uz.uzkassa.cts.filter.BaseFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LedgerRepositoryCustom {

    List<LedgerDetail> getClientLastTopUp(Long customerId);

    List<LedgerDetail> getClientBalanceFull(String tin, ServiceType serviceType);

    List<Object[]> getAllTransactionsByPinflAndTin(String tin);

    ResultList<LedgerBalanceDto> getBalance(BalanceFilter filter);

    ResultList<LedgerResponseDto> getBalanceByServiceOrSell(BaseFilter filter, ServiceType serviceType);

    List<LedgerDetail> getAllCustomersServiceBalance();

    BigDecimal getClientBalanceByServiceType(String tin, ServiceType serviceType);

    BigDecimal getClientBalanceOnThisDate(String tin, LocalDate createDate);

    BigDecimal getDebtors();

    LedgerDebtorsDto getDebtorsWithCount();

    ResultList<BalanceListResponseDto> getBalancePrepaidList(BaseFilter filter);

    BigDecimal getCurrentMonthDetails();

    IncomeExpenseResponseDto getIncomeAndExpense(LocalDate startDate, LocalDate endDate);

    List<CurrentMonthDetailsSummaryDto> getSummary(LocalDate startDate, LocalDate endDate);

    List<Object[]> getDetailNotCreatedDeviceList(LocalDate startDate, LocalDate endDate);
}
