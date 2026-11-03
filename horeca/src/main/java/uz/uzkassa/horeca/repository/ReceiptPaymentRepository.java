package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.receipt.ReceiptPayment;

public interface ReceiptPaymentRepository extends BaseRepository<ReceiptPayment, Long> {

    void deleteAllByReceiptId(Long receiptId);
}
