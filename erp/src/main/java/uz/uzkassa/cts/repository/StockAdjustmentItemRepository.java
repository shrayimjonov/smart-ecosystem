package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.stock.StockAdjustmentItem;

@Repository
public interface StockAdjustmentItemRepository extends BaseRepository<StockAdjustmentItem, Long> {

    @Modifying
    @Query("update StockAdjustmentItem set deleted =true where stockAdjustmentId=:stockAdjustmentId")
    void deleteAllByStockAdjustmentId(Long stockAdjustmentId);
}
