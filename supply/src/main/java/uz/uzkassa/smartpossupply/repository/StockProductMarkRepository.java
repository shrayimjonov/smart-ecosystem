package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.StockProductMark;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockProductMarkRepository extends BaseRepository<StockProductMark, Long>, StockProductMarkRepositoryCustom {
    @Modifying
    @Query("update StockProductMark set deleted=true where stockProductId=:stockProductId")
    void deleteAllByStockProductId(@Param("stockProductId") Long stockProductId);

    @Modifying
    @Query("update StockProductMark set deleted=true where stockProductId in (:stockProductIds)")
    void deleteAllByStockProductIds(@Param("stockProductIds") List<Long> stockProductIds);

    @Modifying
    @Query("update StockProductMark set deleted=true where deleted = false and parent = false and stockProductId in (:stockProductIds)")
    void deleteAllByStockProductIdsAndParentIsFalse(@Param("stockProductIds") List<Long> stockProductIds);

    List<StockProductMark> findAllByStockProductIdAndDeletedIsFalseAndParentIsTrue(Long stockProductId);

    List<StockProductMark> findAllByStockProductIdInAndDeletedIsFalseAndParentIsTrue(List<Long> stockProductIds);

    @Query("select spm.code from StockProductMark spm where spm.deleted=false  and spm.stockProductId=:stockProductId")
    List<String> findAllCodesByStockProductIdAndDeletedIsFalse(@Param("stockProductId") Long stockProductId);

    @Query(
            "select spm from StockProductMark spm join spm.stockProduct sp join sp.stock s where s.deleted=false and spm.deleted=false and s.transferType!=:transferType and spm.transferType !=:transferType and spm.code in(:codes) and s.companyId=:companyId and s.status!=:status order by spm.createdDate desc"
    )
    List<StockProductMark> getByTransferTypeAndCodesAndStatusNot(
            @Param("transferType") TransferType transferType,
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockStatus status
    );

    @Query(
            "select spm.code from StockProductMark spm join spm.stockProduct sp join sp.stock s where s.deleted=false and spm.deleted=false and s.transferType=:transferType and spm.transferType=:transferType and spm.code in(:codes) and s.companyId=:companyId and s.status!=:status"
    )
    List<String> getCodesByTransferTypeAndCodesAndStatusNot(
            @Param("transferType") TransferType transferType,
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockStatus status
    );

    @Query(
            "select spm from StockProductMark spm join spm.stockProduct sp join sp.stock s where s.deleted=false and spm.deleted=false and s.transferType=:transferType and spm.code in (:codes) and s.companyId=:companyId and s.status !=:status and spm.stockProductId !=:stockProductId"
    )
    List<StockProductMark> getByTransferTypeAndCodesAndCompanyIdAndStatusNotAndStockProductIdNot(
            @Param("transferType") TransferType transferType,
            @Param("codes") List<String> codes,
            @Param("companyId") Long companyId,
            @Param("status") StockStatus status,
            @Param("stockProductId") Long stockProductId
    );

    @Modifying
    @Query("update StockProductMark set clickhouseHistorySync =true WHERE id in (:ids)")
    void updateClickhouseHistorySync(@Param(value = "ids") List<Long> ids);

    @Query("select spm.id from StockProductMark spm where spm.deleted=false and spm.clickhouseHistorySync=false and spm.stockProduct.stock.status =:status order by spm.id")
    List<Long> getIdsToSyncClickhouseMarkCodeHistory(@Param("status") StockStatus status);

    Optional<StockProductMark> findFirstByCodeAndStockProductIdAndDeletedIsFalse(String code, Long stockProductId);

    @Query("select spm.id from StockProductMark  spm where spm.deleted=false and spm.box.id in (:boxIds)")
    List<Long> findAllByBoxIdInAndDeletedIsFalse(@Param("boxIds") List<Long> boxIds);

    @Query("select spm.id from StockProductMark  spm where spm.deleted=false and spm.pallet.id in (:palletIds)")
    List<Long> findAllByPalletIdInAndDeletedIsFalse(@Param("palletIds") List<Long> palletIds);

    @Query("select spm.id from StockProductMark  spm where spm.deleted=false and spm.block.id in (:blockIds)")
    List<Long> findAllByBlockIdInAndDeletedIsFalse(@Param("blockIds") List<Long> blockIds);

    @Query("select spm from StockProductMark  spm join spm.stockProduct sp join sp.stock s where spm.deleted=false and s.status='COMPLETED' and sp.deleted=false and spm.code in (:codes) and spm.productId=:productId and spm.transferType=:transferType")
    List<StockProductMark> findAllByCodeAndProductIdAndDeleteIsFalse(@Param("codes") List<String> codes, @Param("productId") Long productId, @Param("transferType") TransferType transferType);

    @Modifying
    @Query("update StockProductMark set used=:out, checkedInStock=true where id in (:ids)")
    void updateCodes(@Param("ids") List<Long> ids, @Param("out") boolean out);

    @Query("select coalesce(sum(spm.qty), 0) from StockProductMark spm where spm.deleted=false and spm.parent=true and spm.stockProductId=:stockProductId")
    BigDecimal getCount(@Param("stockProductId") Long stockProductId);

    boolean existsByStockProductIdAndDeletedIsFalseAndParentIsTrue(Long stockProductId);

    @Modifying
    @Query(value = "call delete_old_mark_codes(:fromDate,:toDate,:batchSize)", nativeQuery = true)
    void deleteOldRows(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("batchSize") int batchSize);

    Optional<StockProductMark> findFirstByDeletedIsTrueOrderByCreatedDateAsc();

    @Modifying
    @Query("update StockProductMark spm set spm.deleted = false where spm.stockProduct.stockId = :stockId")
    void reverseByStockId(@Param("stockId") Long stockId);
}