package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.BillingTransaction;

import java.math.BigDecimal;

public interface BillingTransactionService extends BaseService<BillingTransaction, Long> {

    void create(Long customerId);

    boolean calculateBalance(BigDecimal amount, Long customerId);
}
