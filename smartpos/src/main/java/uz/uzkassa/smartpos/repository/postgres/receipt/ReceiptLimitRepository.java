package uz.uzkassa.smartpos.repository.postgres.receipt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptLimit;

import java.util.Optional;

@Repository
public interface ReceiptLimitRepository extends JpaRepository<ReceiptLimit, Long> {
    Optional<ReceiptLimit> findByCode(String code);
}