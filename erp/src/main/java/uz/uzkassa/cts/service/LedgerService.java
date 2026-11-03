package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.BankClientDetail;
import uz.uzkassa.cts.domain.Ledger;
import uz.uzkassa.cts.domain.LedgerDetail;
import uz.uzkassa.cts.domain.ServicePaymentHistory;
import uz.uzkassa.cts.dto.invoice.request.LedgerInvoiceRequestDto;
import uz.uzkassa.cts.dto.ledger.request.LedgerDto;
import uz.uzkassa.cts.dto.ledger.request.LedgerRevertAmountRequestDto;
import uz.uzkassa.cts.dto.ledger.response.*;
import uz.uzkassa.cts.enums.InvoiceType;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.filter.BalanceFilter;
import uz.uzkassa.cts.filter.BaseFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface LedgerService extends BaseService<Ledger, Long> {
    LedgerDto create(LedgerDto dto);

    List<LedgerResponseDto> topUpClientBalance(LedgerDto dto);

    void importFromJournalDetail(BankClientDetail detail);

    void importFromJournalDetails(Set<BankClientDetail> details);

    LedgerResponseDto getByBankAccountNumber(String accountNumber);

    List<LedgerResponseDto> getClientBalanceFull(String tin);

    LedgerResponseDto getClientBalance(String tin, ServiceType serviceType);

    LedgerResponseDto getClientLastTopUp(Long customerId);

    BigDecimal getSumOfCreditPart(String tin);

    BigDecimal calculateUnpaidAndWithoutInvoice(List<ServicePaymentHistory> histories);

    Page<LedgerResponseDto> getAllTransactionsByTin(String tin, BaseFilter filter);

    List<LedgerTinPinflResponseDto> getAllTransactionsByPinflAndTin(String tin);

    Page<LedgerResponseDto> getAllTransactionsByAccountNumber(String accountNumber, BaseFilter filter);

    BigDecimal getCustomerBalanceByCustomerId(Long customerId);

    BigDecimal getCustomerBalance(String tin);

    Page<LedgerBalanceDto> getBalance(BalanceFilter filter);

    Page<LedgerResponseDto> getService(BaseFilter filter);

    Page<LedgerResponseDto> getSell(BaseFilter filter);

    void payForOrder(LedgerInvoiceRequestDto dto);

    List<LedgerDetail> getAllCustomersServiceBalance();

    void revertByInvoiceId(Long invoiceId, InvoiceType type);

    CurrentStatementResponseDto getNeedAmountAndStatement(String tin, ServiceType service);

    CurrentStatementResponseDto getNeedAmountAndStatement(Long customerId);

    boolean isBalanceEnoughForRent(String tin, BigDecimal amount);

    BigDecimal getClientBalanceByServiceType(String tin, ServiceType service);

    BigDecimal getClientBalanceOnThisDate(String tin, LocalDate createDate);

    BigDecimal getDebtors();

    LedgerDebtorsDto getDebtorsWithCount();

    Page<BalanceListResponseDto> getBalancePrepaidList(BaseFilter filter);

    BigDecimal getCurrentMonthDetails();

    void revertMoneyToBank(LedgerRevertAmountRequestDto dto);

    void topUpBalance(LedgerRevertAmountRequestDto dto);

    void topUpBalance(LedgerRevertAmountRequestDto dto);

    IncomeExpenseResponseDto getIncomeAndExpense(LocalDate startDate, LocalDate endDate);

    List<CurrentMonthDetailsSummaryDto> getSummary(LocalDate startDate, LocalDate endDate);

    List<DetailNotCreatedReportDto> getDetailNotCreatedDeviceList(LocalDate startDate, LocalDate endDate);

    void clientBalanceFix(String tin);
}
