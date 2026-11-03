package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustmentProductAggregation;
import uz.uzkassa.smartpossupply.enums.StockAdjustmentStatus;

import java.util.List;

@Repository
public interface StockAdjustmentProductAggregationRepository extends BaseRepository<StockAdjustmentProductAggregation, Long> {
    @Modifying
    @Query("update StockAdjustmentProductAggregation set deleted=true where stockAdjustmentProductId=:stockAdjustmentProductId")
    void deleteAllByStockAdjustmentProductId(@Param("stockAdjustmentProductId") Long stockAdjustmentProductId);

    @Modifying
    @Query("update StockAdjustmentProductAggregation set deleted=true where stockAdjustmentProductId in (:stockAdjustmentProductIds)")
    void deleteAllByStockAdjustmentProductIds(@Param("stockAdjustmentProductIds") List<Long> stockAdjustmentProductIds);

    @Query(
            "select sapa from StockAdjustmentProductAggregation sapa join sapa.stockAdjustmentProduct sap join sap.stockAdjustment sa where sa.deleted=false and sapa.deleted=false and sapa.code in (:codes) and sa.companyId=:companyId and sa.status!=:status"
    )
    List<StockAdjustmentProductAggregation> getByCodesAndStatusNot(
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockAdjustmentStatus status
    );

    @Modifying
    @Query("update StockAdjustmentProductAggregation set deleted=true where stockAdjustmentProductId in (:stockAdjustmentProductIds) ")
    void deleteAllByStockProductIds(@Param("stockAdjustmentProductIds") List<Long> stockAdjustmentProductIds);

    List<StockAdjustmentProductAggregation> findAllByStockAdjustmentProductIdAndDeletedIsFalse(Long stockAdjustmentProductId);
}
