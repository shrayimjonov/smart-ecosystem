package uz.uzkassa.smartpos.web.rest.integration;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.ResponseData;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.dto.filter.TransferFilter;
import uz.uzkassa.smartpos.dto.integration.CheckReturnedReceiptDto;
import uz.uzkassa.smartpos.dto.integration.ReceiptReturnDto;
import uz.uzkassa.smartpos.dto.integration.onec.OneCIncome;
import uz.uzkassa.smartpos.dto.integration.onec.OneCIncomeListDto;
import uz.uzkassa.smartpos.dto.integration.onec.OneCIncomeResponse;
import uz.uzkassa.smartpos.dto.sales.TransactionRailwaysPaymentDto;
import uz.uzkassa.smartpos.dto.terminal.sales.TransactionDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionTransferCreateDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.FashionTransferUpdateStatusDto;
import uz.uzkassa.smartpos.dto.warehouse.fashion.TransferListDto;
import uz.uzkassa.smartpos.filter.onec.OneCIncomeFilter;
import uz.uzkassa.smartpos.service.IncomeOfProductServiceV2;
import uz.uzkassa.smartpos.service.ReceiptService;
import uz.uzkassa.smartpos.service.TransactionService;
import uz.uzkassa.smartpos.service.TransferServiceV2;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/integration/1c")
public class OneCResource {
    private final ReceiptService receiptService;
    private final IncomeOfProductServiceV2 incomeOfProductServiceV2;
    private final TransferServiceV2 transferService;
    private final TransactionService transactionService;

    @PostMapping("/payment/railways")
    public ResponseData<?> createPayment(@RequestBody @Valid TransactionRailwaysPaymentDto dto) {
        return transactionService.createPaymentForRailways(dto);
    }

    @GetMapping("/payment/{uid}")
    public TransactionDto findByUid(@PathVariable String uid) {
        return transactionService.findByUid(uid);
    }

    @PostMapping("/receipt/return")
    public ResponseData<String> receiptReturn(@RequestBody @Valid ReceiptReturnDto dto, @RequestHeader(value = "TERMINAL_SN", required = false) String terminalSn) {
        return ResponseData.ok(receiptService.receiptReturn(dto, terminalSn));
    }

    @GetMapping("/receipt/return/{uid}")
    public CheckReturnedReceiptDto getReceiptReturnStatus(@PathVariable String uid) {
        return receiptService.getReceiptReturnStatus(uid);
    }

    @PostMapping("/warehouse/income")
    public Long warehouseIncome(@RequestBody @Valid OneCIncome oneCIncome) {
        return incomeOfProductServiceV2.oneCIncome(oneCIncome);
    }

    @GetMapping("/warehouse/income/{id}")
    public OneCIncomeResponse getWarehouseIncomeStatus(@PathVariable Long id) {
        return incomeOfProductServiceV2.findByIdFor1C(id);
    }

    @GetMapping("/warehouse/income")
    public PageDto<OneCIncomeListDto> findAllByFilter(@ParameterObject OneCIncomeFilter filter) {
        return incomeOfProductServiceV2.findAllByFilter(filter);
    }

    @PostMapping("/warehouse/transfer")
    public Long create(@RequestBody @Valid FashionTransferCreateDto dto) {
        return transferService.create(dto, false);
    }

    @PutMapping("/warehouse/transfer/{id}")
    public void updateStatus(@PathVariable Long id, @RequestBody FashionTransferUpdateStatusDto dto) {
        transferService.updateStatus(id, dto);
    }

    @DeleteMapping("/warehouse/transfer/{id}")
    public void delete(@PathVariable Long id) {
        transferService.delete(id);
    }

    @GetMapping("/warehouse/transfer")
    public PageDto<TransferListDto> findAllByFilter(@ParameterObject TransferFilter filter) {
        return transferService.findAllByFilter(filter);
    }
}
