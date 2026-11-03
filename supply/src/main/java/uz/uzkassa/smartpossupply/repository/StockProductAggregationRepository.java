package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregation;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockProductAggregationRepository
        extends BaseRepository<StockProductAggregation, Long>, StockProductAggregationRepositoryCustom {
    @Modifying
    @Query("update StockProductAggregation set deleted=true where stockProductId=:stockProductId ")
    void deleteAllByStockProductId(@Param("stockProductId") Long stockProductId);

    List<StockProductAggregation> findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(Long stockProductId);

    List<StockProductAggregation> findAllByStockProductIdInAndDeletedIsFalseAndParentIsTrue(List<Long> stockProductIds);

    @Query("select spa.code from StockProductAggregation spa where spa.deleted=false and spa.stockProductId =:stockProductId")
    List<String> findAllCodesByStockProductIdAndDeletedIsFalse(@Param("stockProductId") Long stockProductId);

    @Modifying
    @Query("update StockProductAggregation set deleted=true where stockProductId in (:stockProductIds) ")
    void deleteAllByStockProductIds(@Param("stockProductIds") List<Long> stockProductIds);

    @Modifying
    @Query("update StockProductAggregation set deleted=true where deleted = false and parent = false and stockProductId in (:stockProductIds) ")
    void deleteAllByStockProductIdsAndParentIsFalse(@Param("stockProductIds") List<Long> stockProductIds);

    @Query(
            "select spa from StockProductAggregation spa join spa.stockProduct sp join sp.stock s where s.deleted=false and spa.deleted=false and s.transferType!=:transferType and spa.transferType!=:transferType and spa.code in (:codes) and s.companyId=:companyId and s.status!=:status order by spa.createdDate desc"
    )
    List<StockProductAggregation> getByTransferTypeAndCodesAndStatusNot(
            @Param("transferType") TransferType transferType,
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockStatus status
    );

    @Query(
            "select spa.code from StockProductAggregation spa join spa.stockProduct sp join sp.stock s where s.deleted=false and spa.deleted=false and s.transferType=:transferType and spa.transferType=:transferType and spa.code in (:codes) and s.companyId=:companyId and s.status!=:status"
    )
    List<String> getCodesByTransferTypeAndCodesAndStatusNot(
            @Param("transferType") TransferType transferType,
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockStatus status
    );

    @Query(
            "select spa from StockProductAggregation spa join spa.stockProduct sp join sp.stock s where s.deleted=false and spa.deleted=false and s.transferType=:transferType and spa.code in (:codes) and s.companyId=:companyId and s.status !=:status and spa.stockProductId !=:stockProductId"
    )
    List<StockProductAggregation> getByTransferTypeAndCodesAndCompanyIdAndStatusNotAndStockProductIdNot(
            @Param("transferType") TransferType transferType,
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockStatus status,
            @Param("stockProductId") Long stockProductId
    );

    @Modifying
    @Query("update StockProductAggregation set clickhouseHistorySync=true WHERE id in (:ids)")
    void updateClickhouseHistorySync(@Param(value = "ids") List<Long> ids);

    @Query("select spa.id from StockProductAggregation spa where spa.deleted=false and spa.clickhouseHistorySync=false and spa.stockProduct.stock.status =:status order by spa.id")
    List<Long> getIdsToSyncClickhouseAggregationCodeHistory(@Param("status") StockStatus status);

    Optional<StockProductAggregation> findFirstByCodeAndStockProductIdAndDeletedIsFalse(String code, Long stockProductId);

    @Query("select spa from StockProductAggregation  spa join spa.stockProduct sp join sp.stock s where spa.deleted=false and s.status='COMPLETED' and sp.deleted=false and spa.code in (:codes) and spa.productId=:productId and spa.transferType=:transferType")
    List<StockProductAggregation> findAllByCodeAndProductIdAndDeleteIsFalse(@Param("codes") List<String> codes, @Param("productId") Long productId, @Param("transferType") TransferType transferType);

    @Query("select spa.id from StockProductAggregation  spa where spa.deleted=false and spa.pallet.id in (:palletIds)")
    List<Long> findAllByPalletIdInAndDeletedIsFalse(@Param("palletIds") List<Long> palletIds);

    @Modifying
    @Query("update StockProductAggregation set used=:out, checkedInStock=true where id in (:ids)")
    void updateCodes(@Param("ids") List<Long> ids, @Param("out") boolean out);

    @Query("select coalesce(sum(spa.qty), 0) from StockProductAggregation spa where spa.deleted=false and spa.parent=true and spa.stockProductId=:stockProductId")
    BigDecimal getCount(@Param("stockProductId") Long stockProductId);

    boolean existsByStockProductIdAndDeletedIsFalseAndParentIsTrue(Long stockProductId);

    @Modifying
    @Query(value = "call delete_old_aggregation_codes(:fromDate,:toDate,:batchSize)", nativeQuery = true)
    void deleteOldRows(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("batchSize") int batchSize);

    Optional<StockProductAggregation> findFirstByDeletedIsTrueOrderByCreatedDateAsc();

    @Modifying
    @Query("update StockProductAggregation spa set spa.deleted = false where spa.stockProduct.stockId = :stockId")
    void reverseByStockId(@Param("stockId") Long stockId);
}