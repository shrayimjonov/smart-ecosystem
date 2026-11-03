package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRoute;
import uz.uzkassa.smartpossupply.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OrderRepository extends BaseRepository<Order, Long>, OrderRepositoryCustom {

    @EntityGraph(attributePaths = {"stockTransfer", "customer", "supplier", "branch", "warehouse", "vehicle", "copyOrder", "contract", "deliveryAddress", "deliveryBranch", "stockOut", "stockIn", "priceList", "expeditor", "products", "products.product", "products.warehouse", "products.unit", "products.discount", "products.discountItemProduct", "products.companyCommittent", "products.productSerial", "products.productContract"})
    @Query("select o from Order o where o.id=:id")
    Optional<Order> getDetail(@Param("id") Long id);

    @Query("SELECT coalesce(max(o.incrementNumber), 0) FROM Order o where o.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);

    @Query("SELECT count(o.id) FROM Order o where o.deleted=false and o.companyId=:companyId and o.status=:status")
    Long getTotalStatistics(@Param("companyId") Long companyId, @Param("status") OrderStatus status);

    @Query("SELECT o.id FROM Order o WHERE o.deleted = FALSE AND o.stockTransferId = :stockTransferId AND o.status IN (:statuses)")
    List<Long> getIdsByStockTransferIdAndStatusIn(@Param("stockTransferId") Long stockTransferId, @Param("statuses") List<OrderStatus> statuses);

    boolean existsByContractIdAndDeletedIsFalse(Long contactId);

    boolean existsByCompanyIdAndOrderNumberAndIdNotAndDeletedIsFalse(Long companyId, String number, Long id);

    boolean existsByCompanyIdAndOrderNumberAndDeletedIsFalse(Long companyId, String orderNumber);

    @Query("select o.id from Order o where o.deleted=false and o.status=:status and o.clickhouseSync=false order by o.id")
    List<Long> getIdsToSyncClickhouse(@Param("status") OrderStatus status);

    @Modifying
    @Query("update Order set clickhouseSync =true WHERE id in (:ids)")
    void updateClickhouseSync(@Param("ids") List<Long> ids);

    @Modifying
    @Query("update Order set stockTransferId =:stockTransferId WHERE id in (:ids)")
    void setStockTransfer(@Param("ids") List<Long> ids, @Param("stockTransferId") Long stockTransferId);

    @Modifying
    @Query("update Order set status =:status WHERE id in (:ids)")
    void updateStatus(@Param("ids") List<Long> ids, @Param("status") OrderStatus status);

    @Modifying
    @Query("update Order set status=:status where stockTransferId=:stockTransferId and deleted=false")
    void updateStatusByStockTransferId(@Param("stockTransferId") Long stockTransferId, @Param("status") OrderStatus status);

    @Query("select o from Order o where o.stockTransferId=:stockTransferId and o.deleted=false")
    List<Order> getAllByStockTransferId(@Param("stockTransferId") Long stockTransferId);

    @Modifying
    @Query("update Order set status=:status, stockTransferId=null where stockTransferId=:stockTransferId and deleted=false")
    void updateStatusAndStockTransferByStockTransferId(@Param("stockTransferId") Long stockTransferId, @Param("status") OrderStatus status);

    @Query("select pr from Order o join o.planRoute pr where o.stockTransferId=:stockTransferId and o.deleted=false")
    List<PlanRoute> getAllPlanRoutesByStockTransferId(@Param("stockTransferId") Long stockTransferId);

    @Query("select o.agent from Order o where o.stockTransferId=:stockTransferId and o.deleted=false")
    List<User> getAllAgentsByStockTransferId(@Param("stockTransferId") Long stockTransferId);

    @Query("select o from Order o where o.deleted=false and o.planRouteCustomerBranchId is not null and o.planRouteId is null")
    List<Order> getOrdersToSync();

    @Query("select count(distinct o.expeditorId) from Order o where o.id in (:orderIds)")
    Long countExpeditors(@Param("orderIds") List<Long> orderIds);

    @EntityGraph(attributePaths = {"agent"})
    Optional<Order> findFirstByCompanyIdAndCustomerIdAndDeletedIsFalseOrderByOrderDateDesc(Long companyId, Long customerId);

    @Query("""
            SELECT coalesce(count(o.id), 0) FROM Order o
                        JOIN PlanRoute pr on o.planRouteId = pr.id
                        JOIN PlanRouteTemplate prt on pr.templateId = prt.id
                                    WHERE o.deleted = false
                                    AND prt.id = :templateId
                                    AND o.companyId = :companyId
                                    AND o.createdDate BETWEEN :from AND :to
            """)
    Long getCountOfOrdersByTemplateId(Long companyId, Long templateId, LocalDateTime from, LocalDateTime to);

    @Query("""
            select o from Order o 
            where o.agentId = :agentId
              and o.createdDate between :from and :to
              and o.deleted = false
            order by o.createdDate desc 
            """)
    List<Order> getAllForOfflineMobile(@Param("agentId") Long agentId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Modifying
    @Query("update Order o set o.tripId=:tripId where o.id in(:ids)")
    void assignTrip(@Param("ids") Set<Long> ids, @Param("tripId") Long tripId);

    @Query("select o.tripId from Order o where o.deleted = false and o.id in (:ids)")
    Set<Long> getTripIdsByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query("update Order o set o.tripId=:tripId where o.tripId is null and o.id in(:ids)")
    void updateTrip(@Param("ids") Set<Long> ids, @Param("tripId") Long tripId);
}