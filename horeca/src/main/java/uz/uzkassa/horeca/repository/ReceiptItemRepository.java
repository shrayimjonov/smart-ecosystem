package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.receipt.ReceiptItem;

public interface ReceiptItemRepository extends BaseRepository<ReceiptItem, Long> {

    @Modifying
    @Query("update ReceiptItem  set deleted=true  where receiptId=:receiptId and deleted=false")
    void deleteAllByReceiptIdAndDeletedIsFalse(@Param("receiptId") Long receiptId);

    boolean existsByProductIdAndDeletedIsFalse(Long menuItemId);
}
