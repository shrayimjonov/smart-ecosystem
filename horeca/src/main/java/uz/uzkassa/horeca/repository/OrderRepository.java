package uz.uzkassa.horeca.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.enums.OrderStatus;


public interface OrderRepository extends BaseRepository<Order, Long>, OrderRepositoryCustom {
    @Query("select coalesce(max (o.incrementNumber),0)from Order o where o.branchId=:branchId")
    Long getMaxIncrementNumber(@Param("branchId") Long branchId);

    boolean existsByBranchIdAndOrderNumberAndIdNot(Long branchId, String orderNumber, Long id);

    List<Order> findAllByTableIdAndDeletedIsFalseAndStatusNotIn(Long tableId, List<OrderStatus> status);

    boolean existsByTableIdAndDeletedIsFalseAndStatusNotIn(Long tableId, List<OrderStatus> status);

    boolean existsByTableIdAndDeletedIsFalse(Long tableId);
}
