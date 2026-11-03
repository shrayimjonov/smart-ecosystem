package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.receipt.ReceiptPayment;
import uz.uzkassa.smartpossupply.enums.ReceiptPaymentType;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 09.10.2024
 */
@Repository
public interface ReceiptPaymentRepository extends BaseRepository<ReceiptPayment, Long> {

    Optional<ReceiptPayment> findFirstByReceiptIdAndPaymentType(Long receiptId, ReceiptPaymentType paymentType);
}
