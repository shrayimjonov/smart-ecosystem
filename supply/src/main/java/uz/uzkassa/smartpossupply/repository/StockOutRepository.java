package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockOutRepository extends StockBaseRepository, StockOutRepositoryCustom {

    @Query("select s from Stock s where s.id=:id")
    @EntityGraph(attributePaths = {"supplier", "customer", "company", "order", "order.expeditor", "order.planRoute", "contract", "stockIn", "warehouse", "branch", "vehicle", "factura", "order", "oldStock", "priceList", "deliveryBranch", "cashier", "products", "products.product", "products.unit", "receipts", "facturas"})
    Optional<Stock> getDetail(@Param("id") Long id);

    Optional<Stock> findFirstByOrderIdAndTransferTypeAndStatusIsNotAndDeletedIsFalse(Long orderId, TransferType transferType, StockStatus status);

    Optional<Stock> findFirstByStockInIdAndDeletedIsFalse(Long stockInId);

    boolean existsByContractIdAndDeletedIsFalse(Long contractId);

    Optional<Stock> findFirstByReceiptUidAndDeletedIsFalse(String receiptUid);

    @Query("SELECT s.id FROM Stock s WHERE s.deleted = FALSE AND s.orderId IN (:orderIds)")
    List<Long> getIdsByOrderIdIn(@Param("orderIds") List<Long> orderIds);

    Optional<Stock> findByIdAndCompanyIdAndDeletedIsFalse(Long id, Long companyId);
}
