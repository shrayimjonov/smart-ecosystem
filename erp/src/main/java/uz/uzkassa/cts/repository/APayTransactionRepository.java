package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.APayTransaction;

@Repository
public interface APayTransactionRepository extends BaseRepository<APayTransaction, Long> {
    APayTransaction findByTransactionId(String transactionId);
}
