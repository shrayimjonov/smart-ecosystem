package uz.uzkassa.smartpossupply.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockBaseRepository extends BaseRepository<Stock, Long> {

    @Query("SELECT coalesce(max(s.incrementNumber), 0) FROM Stock s where s.companyId=:companyId and s.transferType=:transferType")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId, @Param("transferType") TransferType transferType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "30000")})
    @Query("select s from Stock s where s.id=:id")
    Optional<Stock> get(@Param("id") Long id);

    boolean existsStockByStockNumberAndCompanyIdAndTransferTypeAndIdNot(
            String invoiceNumber,
            Long companyId,
            TransferType transferType,
            Long id
    );

    @Query("SELECT count(s.id) FROM Stock s where s.deleted=false and s.companyId=:companyId and s.status=:status and s.transferType=:transferType")
    Long getTotalStatistics(
            @Param("companyId") Long companyId,
            @Param("status") StockStatus status,
            @Param("transferType") TransferType transferType
    );

    @Query("select s.id from Stock s where s.deleted=false and s.status=:status and s.clickhouseSync=false order by s.id")
    List<Long> getIdsToSyncClickhouse(@Param("status") StockStatus status);

    @Modifying
    @Query("update Stock set clickhouseSync =true WHERE id in (:ids)")
    void updateClickhouseSync(@Param(value = "ids") List<Long> ids);

    Optional<Stock> findFirstByCompanyIdAndFacturaIdAndTransferTypeAndDeletedIsFalse(
            Long companyId,
            Long facturaId,
            TransferType transferType
    );

    @Query(nativeQuery = true, value = "SELECT count(sf.factura_id) FROM stock_factura sf WHERE sf.stock_id IN (:stockIds)")
    Long getInvoiceCountByStockIds(@Param("stockIds") List<Long> stockIds);

    @Query(nativeQuery = true, value = "select COALESCE(SUM(ARRAY_LENGTH(STRING_TO_ARRAY(s.receipt_uid, ','), 1)), 0) FROM stock s WHERE s.deleted = FALSE AND id IN (:stockIds)")
    Long getReceiptCountByStockIds(@Param("stockIds") List<Long> stockIds);
}
