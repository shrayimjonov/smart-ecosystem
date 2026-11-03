package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.BillingTransaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingTransactionRepository extends BaseRepository<BillingTransaction, Long> {

    Optional<BillingTransaction> findTopByCustomerIdOrderByTransactionDateDesc(Long customerId);

    List<BillingTransaction> findAllByCustomerIdAndCalculatedOrderByTransactionDate(Long customerId, boolean calculated);
}
