package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustmentProductAggregationBlock;
import uz.uzkassa.smartpossupply.enums.StockAdjustmentStatus;

import java.util.List;

@Repository
public interface StockAdjustmentProductAggregationBlockRepository extends BaseRepository<StockAdjustmentProductAggregationBlock, Long> {
    @Modifying
    @Query("update StockAdjustmentProductAggregationBlock set deleted=true where stockAdjustmentProductId=:stockAdjustmentProductId ")
    void deleteAllByStockAdjustmentProductId(@Param("stockAdjustmentProductId") Long stockAdjustmentProductId);

    @Modifying
    @Query("update StockAdjustmentProductAggregationBlock set deleted=true where stockAdjustmentProductId in (:stockAdjustmentProductIds) ")
    void deleteAllByStockAdjustmentProductIds(@Param("stockAdjustmentProductIds") List<Long> stockAdjustmentProductIds);

    @Query(
            "select sapab from StockAdjustmentProductAggregationBlock sapab join sapab.stockAdjustmentProduct sap join sap.stockAdjustment sa where sa.deleted=false and sapab.deleted=false  and sapab.code in (:codes) and sa.companyId=:companyId and sa.status !=:status"
    )
    List<StockAdjustmentProductAggregationBlock> getByCodesAndCompanyIdAndStatusNot(
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockAdjustmentStatus status
    );

    @Modifying
    @Query("update StockAdjustmentProductAggregationBlock set deleted=true where stockAdjustmentProductId in (:stockAdjustmentProductIds) ")
    void deleteAllByStockProductIds(@Param("stockAdjustmentProductIds") List<Long> stockAdjustmentProductIds);

    List<StockAdjustmentProductAggregationBlock> findAllByStockAdjustmentProductIdAndDeletedIsFalse(Long stockAdjustmentProductId);
}
