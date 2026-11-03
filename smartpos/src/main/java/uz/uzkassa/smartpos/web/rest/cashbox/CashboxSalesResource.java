package uz.uzkassa.smartpos.web.rest.cashbox;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDTO;
import uz.uzkassa.smartpos.dto.sales.*;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.Source;
import uz.uzkassa.smartpos.filter.SalesFilter;
import uz.uzkassa.smartpos.service.SalesService;
import uz.uzkassa.smartpos.service.TransactionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cashbox/sales")
public class CashboxSalesResource {
    private final SalesService salesService;
    private final TransactionService transactionService;

    @PostMapping
    public ReceiptDTO save(@RequestBody @Valid SalesCreateDto dto) {
        return salesService.save(dto, Source.CASHBOX);
    }

    @PostMapping("/payment")
    public String createPayment(@RequestBody @Valid TransactionCreatePaymentDto dto) {
        return transactionService.createPayment(dto);
    }

    @GetMapping
    public Page<SalesCabinetListDto> findAllByFilter(@ParameterObject SalesFilter filter) {
        return salesService.findAllByFilter(filter, false);
    }

    @GetMapping("/{id}")
    public SalesDto findById(@PathVariable Long id) {
        return salesService.findById(id);
    }

    @GetMapping("/drafts")
    public Page<SalesCabinetListDto> findDrafts(@ParameterObject SalesFilter filter) {
        return salesService.findAllDraftsByFilter(filter, false);
    }

    @PutMapping("/fiscal")
    public SalesDto fiscal(@RequestBody @Valid SalesFiscalDto dto) {
        return salesService.updateFiscalData(dto);
    }

    @GetMapping("/uid/{uid}")
    public SalesDto findByUid(@PathVariable String uid) {
        return salesService.findByUid(uid);
    }

    @GetMapping("/uid/return/{uid}")
    public SalesDto findByUidForReturn(@PathVariable String uid) {
        return salesService.findByUidForReturn(uid);
    }

    @GetMapping("/payment/types/m1")
    public List<PaymentType> m1PaymentTypes(){
        return PaymentType.m1Types();
    }
}
