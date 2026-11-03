package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.ledger.request.IncomeExpenseRequestDto;
import uz.uzkassa.cts.dto.ledger.request.LedgerDto;
import uz.uzkassa.cts.dto.ledger.request.LedgerRevertAmountRequestDto;
import uz.uzkassa.cts.dto.ledger.response.*;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.filter.BalanceFilter;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.LedgerService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ledger")
@Tag(name = "Ledger")
@Slf4j
public class LedgerResource {

    private final LedgerService service;

    public LedgerResource(LedgerService ledgerService) {
        this.service = ledgerService;
    }

    @Operation(summary = "top up from bank client")
    @PostMapping
    public ResponseEntity<List<LedgerResponseDto>> topUpFromBankClient(@Valid @RequestBody LedgerDto dto) {
        return ResponseEntity.ok(service.topUpClientBalance(dto));
    }


    @Operation(summary = "get customer account by bank account number")
    @GetMapping("/byAccountNumber/{accountNumber}")
    public ResponseEntity<LedgerResponseDto> getCustomerAccountByAccountNumber(@PathVariable @Size(max = 20, min = 20) String accountNumber) {
        log.debug("REST request to get by Account Number: {}", accountNumber);
        LedgerResponseDto result = service.getByBankAccountNumber(accountNumber);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get customer balance by tin")
    @GetMapping("/byTin/{tin}")
    public ResponseEntity<List<LedgerResponseDto>> getCustomerBalanceByTin(@PathVariable @Size(max = 14, min = 9) String tin) {
        log.debug("REST request to get by tin: {}", tin);
        List<LedgerResponseDto> result = service.getClientBalanceFull(tin);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get customer balance by tin")
    @GetMapping("/allTransactionsByTin/{tin}")
    public ResponseEntity<Page<LedgerResponseDto>> getCustomerTransactionsByTin(@PathVariable @Size(max = 14, min = 9) String tin, BaseFilter filter) {
        log.debug("REST request to get by tin: {}", tin);
        Page<LedgerResponseDto> result = service.getAllTransactionsByTin(tin, filter);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get all transactions by tin and pinfl ")
    @GetMapping("/all-transactions-pinfl-tin/{tin}")
    public ResponseEntity<List<LedgerTinPinflResponseDto>> getCustomerTransactionsByTin(@PathVariable @Size(max = 14, min = 9) String tin) {
        log.debug("REST request to get by tin: {}", tin);
        List<LedgerTinPinflResponseDto> result = service.getAllTransactionsByPinflAndTin(tin);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get customer balance by tin")
    @GetMapping("/allTransactionsByAccountNumber/{accountNumber}")
    public ResponseEntity<Page<LedgerResponseDto>> getCustomerTransactionsByAccountNumber(@PathVariable @Size(max = 20, min = 20) String accountNumber, BaseFilter filter) {
        log.debug("REST request to get by tin: {}", accountNumber);
        Page<LedgerResponseDto> result = service.getAllTransactionsByAccountNumber(accountNumber, filter);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get customer balance")
    @GetMapping("/balance")
    public ResponseEntity<Page<LedgerBalanceDto>> getBalance(BalanceFilter filter) {
        log.debug("REST request to get balance by filter : {}", filter);
        Page<LedgerBalanceDto> result = service.getBalance(filter);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get customer balance")
    @GetMapping("/service")
    public ResponseEntity<Page<LedgerResponseDto>> getService(BaseFilter filter) {
        log.debug("REST request to get balance by filter : {}", filter);
        Page<LedgerResponseDto> result = service.getService(filter);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get customer balance")
    @GetMapping("/sell")
    public ResponseEntity<Page<LedgerResponseDto>> getSell(BaseFilter filter) {
        log.debug("REST request to get balance by filter : {}", filter);
        Page<LedgerResponseDto> result = service.getSell(filter);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get customer balance")
    @GetMapping("/service/{tin}")
    public ResponseEntity<CurrentStatementResponseDto> getCurrentServiceStatement(@PathVariable String tin) {
        log.debug("REST request to get service balance by tin : {}", tin);
        CurrentStatementResponseDto result = service.getNeedAmountAndStatement(tin, ServiceType.SERVICE);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get customer balance")
    @GetMapping("/sell/{tin}")
    public ResponseEntity<CurrentStatementResponseDto> getCurrentSellStatement(@PathVariable String tin) {
        log.debug("REST request to get service balance by tin : {}", tin);
        CurrentStatementResponseDto result = service.getNeedAmountAndStatement(tin, ServiceType.SALE);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get debtors")
    @GetMapping("/get-debtors")
    public ResponseEntity<BigDecimal> getdebtors() {
        log.debug("REST request to get-debtors");
        BigDecimal result = service.getDebtors();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get debtors")
    @GetMapping("/get-debtors-with-count")
    public ResponseEntity<LedgerDebtorsDto> getDebtorsWithCount() {
        return ResponseEntity.ok(service.getDebtorsWithCount());
    }

    @Operation(summary = "get balance with prepaid")
    @GetMapping("/get-balance-prepaid")
    public ResponseEntity<Page<BalanceListResponseDto>> getBalancePrepaidList(BaseFilter filter) {
        log.debug("REST request to get-debtors");
        return ResponseEntity.ok(service.getBalancePrepaidList(filter));
    }

    @Operation(summary = "get current month details")
    @GetMapping("/get-current-month-details")
    public ResponseEntity<BigDecimal> getCurrentMonthDetails() {
        log.debug("REST request to get-debtors");
        BigDecimal result = service.getCurrentMonthDetails();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get current month details")
    @PostMapping("/revert-amount")
    public ResponseEntity<BigDecimal> revertAmount(@Valid @RequestBody LedgerRevertAmountRequestDto dto) {
        log.debug("REST request to get-debtors");
        service.revertMoneyToBank(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "get current month details")
    @PostMapping("/top-up-balace")
    public ResponseEntity<BigDecimal> topUpBalance(@Valid @RequestBody LedgerRevertAmountRequestDto dto) {
        service.topUpBalance(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "get current month details")
    @PostMapping("/income-expense")
    public ResponseEntity<IncomeExpenseResponseDto> getIncomeAndExpense(@Valid @RequestBody IncomeExpenseRequestDto dto) {
        log.debug("REST request to income-expense");

        return ResponseEntity.ok(service.getIncomeAndExpense(dto.getStartDate(), dto.getEndDate()));
    }

    @Operation(summary = "get current month details")
    @PostMapping("/current-month-summary")
    public ResponseEntity<List<CurrentMonthDetailsSummaryDto>> getSummary(@Valid @RequestBody IncomeExpenseRequestDto dto) {
        log.debug("REST request to income-expense");
        return ResponseEntity.ok(service.getSummary(dto.getStartDate(), dto.getEndDate()));
    }

    @Operation(summary = "get detail not created devices for this month")
    @PostMapping("/detail-not-created-devices")
    public ResponseEntity<List<DetailNotCreatedReportDto>> getDetailNotCreatedDevice(@Valid @RequestBody IncomeExpenseRequestDto dto) {
        log.debug("REST request to DetailNotCreatedDeviceDto");
        return ResponseEntity.ok(service.getDetailNotCreatedDeviceList(dto.getStartDate(), dto.getEndDate()));
    }

    @Operation(summary = "Fix balance of customer")
    @PostMapping("/fix-balance")
    public ResponseEntity<Void> fixBalance(@RequestParam(name = "tin", required = false) String tin) {
        service.clientBalanceFix(tin);
        return ResponseEntity.ok().build();
    }
}
