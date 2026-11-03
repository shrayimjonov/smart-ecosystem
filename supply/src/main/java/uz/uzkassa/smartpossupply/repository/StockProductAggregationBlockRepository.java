package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.stock.StockProductAggregationBlock;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockProductAggregationBlockRepository extends BaseRepository<StockProductAggregationBlock, Long>, StockProductAggregationBlockRepositoryCustom {
    @Modifying
    @Query("update StockProductAggregationBlock set deleted=true where stockProductId=:stockProductId ")
    void deleteAllByStockProductId(@Param("stockProductId") Long stockProductId);

    List<StockProductAggregationBlock> findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(Long stockProductId);

    List<StockProductAggregationBlock> findAllByStockProductIdInAndDeletedIsFalseAndParentIsTrue(List<Long> stockProductIds);

    @Query("select spa.code from StockProductAggregationBlock spa where spa.deleted=false and spa.stockProductId =:stockProductId")
    List<String> findAllCodesByStockProductIdAndDeletedIsFalse(@Param("stockProductId") Long stockProductId);

    @Modifying
    @Query("update StockProductAggregationBlock set deleted=true where stockProductId in (:stockProductIds) ")
    void deleteAllByStockProductIds(@Param("stockProductIds") List<Long> stockProductIds);

    @Modifying
    @Query("update StockProductAggregationBlock set deleted=true where deleted = false and parent is false and stockProductId in (:stockProductIds) ")
    void deleteAllByStockProductIdsAndParentIsFalse(@Param("stockProductIds") List<Long> stockProductIds);

    @Query(
            "select spa from StockProductAggregationBlock spa join spa.stockProduct sp join sp.stock s where s.deleted=false and spa.deleted=false and s.transferType!=:transferType and spa.transferType!=:transferType and spa.code in (:codes) and s.companyId=:companyId and s.status!=:status order by spa.createdDate desc"
    )
    List<StockProductAggregationBlock> getByTransferTypeAndCodesAndStatusNot(
            @Param("transferType") TransferType transferType,
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockStatus status
    );

    @Query(
            "select spa.code from StockProductAggregationBlock spa join spa.stockProduct sp join sp.stock s where s.deleted=false and spa.deleted=false and s.transferType!=:transferType and spa.code in (:codes) and s.companyId=:companyId and s.status!=:status"
    )
    List<String> getCodesByTransferTypeAndCodesAndStatusNot(
            @Param("transferType") TransferType transferType,
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockStatus status
    );

    @Query(
            "select spa from StockProductAggregationBlock spa join spa.stockProduct sp join sp.stock s where s.deleted=false and spa.deleted=false and s.transferType=:transferType and spa.code in (:codes) and s.companyId=:companyId and s.status !=:status and spa.stockProductId !=:stockProductId"
    )
    List<StockProductAggregationBlock> getByTransferTypeAndCodesAndCompanyIdAndStatusNotAndStockProductIdNot(
            @Param("transferType") TransferType transferType,
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockStatus status,
            @Param("stockProductId") Long stockProductId
    );

    @Modifying
    @Query("update StockProductAggregationBlock set clickhouseSync =true WHERE id in (:ids)")
    void updateClickhouseSync(@Param(value = "ids") List<Long> ids);

    @Modifying
    @Query("update StockProductAggregationBlock set clickhouseHistorySync =true WHERE id in (:ids)")
    void updateClickhouseHistorySync(@Param(value = "ids") List<Long> ids);

    @Query("select spab.id from StockProductAggregationBlock spab where spab.deleted=false and spab.clickhouseSync=false and spab.stockProduct.stock.status =:status order by spab.id")
    List<Long> getIdsToSyncClickhouse(@Param("status") StockStatus status);

    @Query("select spab.id from StockProductAggregationBlock spab where spab.deleted=false and spab.clickhouseHistorySync=false and spab.stockProduct.stock.status =:status order by spab.id")
    List<Long> getIdsToSyncClickhouseAggregationBlockCodeHistory(@Param("status") StockStatus status);

    Optional<StockProductAggregationBlock> findFirstByCodeAndStockProductIdAndDeletedIsFalse(String code, Long stockProductId);

    @Query("select spab.id from StockProductAggregationBlock  spab where spab.deleted=false and spab.box.id in (:boxIds)")
    List<Long> findAllByBoxIdInAndDeletedIsFalse(@Param("boxIds") List<Long> boxIds);

    @Query("select spab.id from StockProductAggregationBlock  spab where spab.deleted=false and spab.pallet.id in (:palletIds)")
    List<Long> findAllByPalletIdInAndDeletedIsFalse(@Param("palletIds") List<Long> palletIds);

    @Query("select spab from StockProductAggregationBlock  spab join spab.stockProduct sp join sp.stock s where spab.deleted=false and s.status='COMPLETED' and sp.deleted=false and spab.code in (:codes) and spab.productId=:productId and spab.transferType=:transferType")
    List<StockProductAggregationBlock> findAllByCodeAndProductIdAndDeleteIsFalse(@Param("codes") List<String> codes, @Param("productId") Long productId, @Param("transferType") TransferType transferType);

    @Modifying
    @Query("update StockProductAggregationBlock set used=:out, checkedInStock=true where id in (:ids)")
    void updateCodes(@Param("ids") List<Long> ids, @Param("out") boolean out);

    @Query("select coalesce(sum(spab.qty), 0) from StockProductAggregationBlock spab where spab.deleted=false and spab.parent=true and spab.stockProductId=:stockProductId")
    BigDecimal getCount(@Param("stockProductId") Long stockProductId);

    boolean existsByStockProductIdAndDeletedIsFalseAndParentIsTrue(Long stockProductId);

    @Modifying(clearAutomatically = true)
    @Query(value = "call delete_old_block_codes(:fromDate,:toDate,:batchSize)", nativeQuery = true)
    @Transactional
    void deleteOldRows(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("batchSize") int batchSize);

    Optional<StockProductAggregationBlock> findFirstByDeletedIsTrueOrderByCreatedDateAsc();

    @Modifying
    @Query("update StockProductAggregationBlock spab set spab.deleted = false where spab.stockProduct.stockId = :stockId")
    void reverseByStockId(@Param("stockId") Long stockId);
}