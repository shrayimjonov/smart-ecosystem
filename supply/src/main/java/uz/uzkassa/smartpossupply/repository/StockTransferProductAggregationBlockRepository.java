package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransferProductAggregationBlock;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;

import java.util.List;

@Repository
public interface StockTransferProductAggregationBlockRepository extends BaseRepository<StockTransferProductAggregationBlock, Long> {
    @Modifying
    @Query("update StockTransferProductAggregationBlock set deleted=true where stockTransferProductId=:stockTransferProductId ")
    void deleteAllByStockTransferProductId(@Param("stockTransferProductId") Long stockTransferProductId);

    @Modifying
    @Query("update StockTransferProductAggregationBlock set deleted=true where stockTransferProductId in (:stockTransferProductIds) ")
    void deleteAllByStockTransferProductIds(@Param("stockTransferProductIds") List<Long> stockTransferProductIds);

    @Query(
            "select stpab from StockTransferProductAggregationBlock stpab join stpab.stockTransferProduct stp join stp.stockTransfer st where st.deleted=false and stpab.deleted=false  and stpab.code in (:codes) and st.companyId=:companyId and st.status !=:status"
    )
    List<StockTransferProductAggregationBlock> getByCodesAndCompanyIdAndStatusNot(
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockTransferStatus status
    );

    List<StockTransferProductAggregationBlock> findAllByStockTransferProductIdAndDeletedIsFalse(Long stockTransferProductId);

}
