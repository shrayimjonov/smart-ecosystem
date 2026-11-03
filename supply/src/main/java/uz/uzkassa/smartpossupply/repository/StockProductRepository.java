package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockProductRepository extends BaseRepository<StockProduct, Long>, StockProductRepositoryCustom {
    @Modifying
    @Query("update StockProduct set deleted=true where id in (:ids) ")
    void deleteByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query("update StockProduct sp set sp.deleted=false where sp.stockId=:stockId ")
    void reverseByStockId(@Param("stockId") Long stockId);

    @Modifying
    @Query("update StockProduct sp set sp.deleted=true where sp.stockId=:stockId ")
    void deleteByStockId(@Param("stockId") Long stockId);

    @Query("select sp.id from StockProduct sp where sp.stockId=:stockId and sp.deleted = false ")
    List<Long> getIdsByStockId(@Param("stockId") Long stockId);

    boolean existsByStockIdAndDeletedIsFalseAndCheckedIsFalse(Long stockId);

    @Modifying
    @Query(" update StockProduct  sp set sp.checked=false where sp.stockId=:stockId")
    void updateStockProduct(@Param("stockId") Long stockId);

    @Query(""" 
              SELECT sp FROM StockProduct sp
              JOIN sp.stock s
              WHERE sp.deleted = false
              AND sp.productId = :productId
              AND s.status = :stockStatus
              AND s.transferType = :stockTransferType
              and s.completedDate >= :fromDate AND s.completedDate <= :toDate
           """
       )
    List<StockProduct> findAllByStock(
            @Param("productId") Long productId,
            @Param("stockStatus") StockStatus stockStatus,
            @Param("stockTransferType") TransferType stockTransferType,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    @Query("select sp from StockProduct sp join Stock s on sp.stockId = s.id join Order o on s.orderId = o.id where sp.deleted is false and sp.productId = :productId and o.stockTransferId = :stockTransferId")
    Optional<StockProduct> findByProductIdAndStockTransferId(@Param("productId") Long productId, @Param("stockTransferId") Long stockTransferId);
}
