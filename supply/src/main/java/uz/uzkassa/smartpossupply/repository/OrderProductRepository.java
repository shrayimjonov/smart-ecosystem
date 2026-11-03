package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.order.OrderProduct;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderProductRepository extends BaseRepository<OrderProduct, Long> {

    @Modifying
    @Query("update OrderProduct op set op.deleted=false where op.orderId=:orderId")
    void reverseByOrderId(@Param("orderId") Long orderId);

    Optional<OrderProduct> findFirstByOrder_Company_IdAndProductIdOrderByOrderIdDesc(Long companyId, Long productId);

    List<OrderProduct> findAllByOrderId(Long orderId);

    @Modifying
    void deleteAllByOrderId(Long orderId);

    @Query("""
    SELECT SUM(op.qty) 
    FROM OrderProduct op 
    JOIN op.order o
    WHERE op.deleted = false 
      AND op.orderId = :orderId 
      AND op.productId = :productId
      AND o.stockOutId IS NOT NULL
""")
    BigDecimal getSoldQty(@Param("orderId") Long orderId, @Param("productId") Long productId);

    List<OrderProduct> findAllByOrderIdInAndOrder_ExpeditorIdIsNotNullOrderByOrderIdDesc(List<Long> orderId);

    @Query("select coalesce(sum(op.qty),0) from OrderProduct  op inner join Order  o on op.orderId=o.id join StockTransfer st on o.stockTransferId=st.id where op.productId=:productId and op.deleted=false and o.status in(:statuses) and o.expeditorId=:expeditorId  and o.id!=:orderId and st.toWarehouseId=:warehouseId")
    BigDecimal getQty(@Param("expeditorId") Long expeditorId, @Param("productId") Long productId, @Param("statuses") List<OrderStatus> statuses, @Param("orderId") Long orderId, @Param("warehouseId") Long warehouseId);

    @Query("select coalesce(sum(op.qty),0) from OrderProduct  op inner join Order  o on op.orderId=o.id join StockTransfer st on o.stockTransferId=st.id where op.productId=:productId and op.deleted=false and o.status in (:statuses) and o.expeditorId=:expeditorId and st.toWarehouseId=:warehouseId")
    BigDecimal getQty(@Param("expeditorId") Long expeditorId, @Param("productId") Long productId, @Param("statuses") List<OrderStatus> statuses, @Param("warehouseId") Long warehouseId);
}