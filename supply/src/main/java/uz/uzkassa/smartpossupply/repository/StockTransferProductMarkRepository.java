package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransferProductMark;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;

import java.util.List;

@Repository
public interface StockTransferProductMarkRepository extends BaseRepository<StockTransferProductMark, Long> {
    @Modifying
    @Query("update StockTransferProductMark set deleted=true where stockTransferProductId=:stockTransferProductId ")
    void deleteAllByStockTransferProductId(@Param("stockTransferProductId") Long stockTransferProductId);

    @Modifying
    @Query("update StockTransferProductMark set deleted=true where stockTransferProductId in (:stockTransferProductIds) ")
    void deleteAllByStockTransferProductIds(@Param("stockTransferProductIds") List<Long> stockTransferProductIds);

    @Query(
            "select stpm from StockTransferProductMark stpm join stpm.stockTransferProduct stp join stp.stockTransfer st where st.deleted=false and stpm.deleted=false  and stpm.code in (:codes) and st.companyId=:companyId and st.status !=:status"
    )
    List<StockTransferProductMark> getByCodesAndCompanyIdAndStatusNot(
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockTransferStatus status
    );

    List<StockTransferProductMark> findAllByStockTransferProductIdAndDeletedIsFalse(Long stockTransferProductId);

}
