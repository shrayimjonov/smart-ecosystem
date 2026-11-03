package uz.uzkassa.smartpos.repository.postgres.receipt;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.receipt.ReceiptDetail;
import uz.uzkassa.smartpos.enums.ReceiptStatus;

import java.util.List;


@SuppressWarnings("unused")
public interface ReceiptDetailRepository extends JpaRepository<ReceiptDetail, Long> {
    List<ReceiptDetail> findFirst1000ByReceipt_StatusNotAndIdGreaterThanEqualAndIdLessThanOrderById(ReceiptStatus status, Long start, Long end);

    List<ReceiptDetail> findAllByReceipt_IdIn(List<Long> receiptIds);
}
