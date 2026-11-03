package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransferProductAggregation;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;

import java.util.List;

@Repository
public interface StockTransferProductAggregationRepository extends BaseRepository<StockTransferProductAggregation, Long> {
    @Modifying
    @Query("update StockTransferProductAggregation set deleted=true where stockTransferProductId=:stockTransferProductId ")
    void deleteAllByStockTransferProductId(@Param("stockTransferProductId") Long stockTransferProductId);

    @Modifying
    @Query("update StockTransferProductAggregation set deleted=true where stockTransferProductId in (:stockTransferProductIds) ")
    void deleteAllByStockTransferProductIds(@Param("stockTransferProductIds") List<Long> stockTransferProductIds);

    @Query(
            "select stpa from StockTransferProductAggregation stpa join stpa.stockTransferProduct stp join stp.stockTransfer st where st.deleted=false and stpa.deleted=false  and stpa.code in (:codes) and st.companyId=:companyId and st.status !=:status"
    )
    List<StockTransferProductAggregation> getByCodesAndCompanyIdAndStatusNot(
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockTransferStatus status
    );

    List<StockTransferProductAggregation> findAllByStockTransferProductIdAndDeletedIsFalse(Long stockTransferProductId);
}
