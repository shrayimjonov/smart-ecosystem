package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustmentProductMark;
import uz.uzkassa.smartpossupply.enums.StockAdjustmentStatus;

import java.util.List;

@Repository
public interface StockAdjustmentProductMarkRepository extends BaseRepository<StockAdjustmentProductMark, Long> {
    @Modifying
    @Query("update StockAdjustmentProductMark set deleted=true where stockAdjustmentProductId=:stockAdjustmentProductId ")
    void deleteAllByStockAdjustmentProductId(@Param("stockAdjustmentProductId") Long stockAdjustmentProductId);

    @Modifying
    @Query("update StockAdjustmentProductMark set deleted=true where stockAdjustmentProductId in (:stockAdjustmentProductIds) ")
    void deleteAllByStockAdjustmentProductIds(@Param("stockAdjustmentProductIds") List<Long> stockAdjustmentProductIds);

    @Query(
            "select sapm from StockAdjustmentProductMark sapm join sapm.stockAdjustmentProduct sap join sap.stockAdjustment sa where sa.deleted=false and sapm.deleted=false  and sapm.code in (:codes) and sa.companyId=:companyId and sa.status !=:status"
    )
    List<StockAdjustmentProductMark> getByCodesAndCompanyIdAndStatusNot(
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockAdjustmentStatus status
    );

    @Modifying
    @Query("update StockAdjustmentProductMark set deleted=true where stockAdjustmentProductId in (:stockAdjustmentProductIds) ")
    void deleteAllByStockProductIds(@Param("stockAdjustmentProductIds") List<Long> stockAdjustmentProductIds);

    List<StockAdjustmentProductMark> findAllByStockAdjustmentProductIdAndDeletedIsFalse(Long stockAdjustmentProductId);
}
