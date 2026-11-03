package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.receipt.ReceiptDetail;

import java.util.Optional;

public interface ReceiptDetailRepository extends BaseRepository<ReceiptDetail, Long> {

    Optional<ReceiptDetail> findFirstByReceiptId(Long receiptId);
}
