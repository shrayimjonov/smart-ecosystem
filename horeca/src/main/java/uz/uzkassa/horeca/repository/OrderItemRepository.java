package uz.uzkassa.horeca.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.order.OrderItem;
import uz.uzkassa.horeca.enums.OrderItemStatus;

import java.util.List;

public interface OrderItemRepository extends BaseRepository<OrderItem, Long>, OrderItemRepositoryCustom {

    List<OrderItem> findAllByOrderIdAndDeletedIsFalseAndStatusNotIn(Long orderId, List<OrderItemStatus> status);

    @Query("select count(oi.id) from OrderItem oi where oi.deleted=false and oi.status=:status and oi.orderId=:orderId")
    Integer getCount(@Param("orderId") Long orderId, @Param("status") OrderItemStatus status);

    @Query("select count(oi.id) from OrderItem oi where oi.deleted=false and oi.orderId=:orderId")
    Integer getItemCount(@Param("orderId") Long orderId);
//
//    @Query(value = "select * from orders o inner join order_item oi on oi.order_id = o.id where oi.deleted=false and o.deleted=false and oi.status!=:itemStatus and o.status!=:status and oi.marks @> to_jsonb(:code) limit 1",nativeQuery = true)
//    Optional<Order> findFirstOrderByCodeAndStatus(@Param("code") String code, @Param("status") String status, @Param("itemStatus") String itemStatus);
}
