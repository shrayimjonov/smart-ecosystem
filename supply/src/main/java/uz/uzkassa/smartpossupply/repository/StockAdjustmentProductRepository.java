package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustmentProduct;

import java.util.List;

@Repository
public interface StockAdjustmentProductRepository extends BaseRepository<StockAdjustmentProduct, Long>, StockAdjustmentProductRepositoryCustom {
    @Modifying
    @Query("update StockAdjustmentProduct set deleted=true where stockAdjustmentId in (:stockAdjustmentIds)")
    void deleteAllByStockAdjustmentIds(@Param("stockAdjustmentIds") List<Long> stockAdjustmentIds);

    @Modifying
    @Query("update StockAdjustmentProduct set deleted=true where id in (:ids) ")
    void deleteByIds(@Param("ids") List<Long> ids);
}
