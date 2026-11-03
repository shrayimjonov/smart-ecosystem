package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.domain.order.OrderItem;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.order.kanban.OrderItemStatisticsDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderItemChefStatsMapperDTO;
import uz.uzkassa.horeca.enums.OrderItemStatus;
import uz.uzkassa.horeca.enums.OrderStatus;
import uz.uzkassa.horeca.filters.ChefStatsFilter;
import uz.uzkassa.horeca.filters.OrderItemFilter;
import uz.uzkassa.horeca.repository.OrderItemRepositoryCustom;

import java.util.List;

public class OrderItemRepositoryCustomImpl extends BaseRepositoryImpl implements OrderItemRepositoryCustom {

    @Override
    public OrderItemStatisticsDTO getStatistics(OrderItemFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(" (case when  oi.status = 'PREPARING' then count(distinct oi.id) else 0 end) as preparingCount, ");
        sql.append(" (case when  oi.status = 'READY' then count(distinct oi.id) else 0 end) as readyCount, ");
        sql.append(" (case when  oi.status = 'CANCELLED' then count(distinct oi.id) else 0 end) as cancelledCount, ");
        sql.append(" (case when  oi.status = 'NEW' then count(distinct oi.id) else 0 end) as newCount ");
        sql.append(getBaseSql(filter));
        sql.append(" group by oi.status ");

        TypedQuery<Object[]> query = entityManager.createQuery(sql.toString(), Object[].class);

        query.setParameter("companyId", filter.getCompanyId());
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }

        if (filter.getOrderId() != null) {
            query.setParameter("orderId", filter.getOrderId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", OrderStatus.valueOf(filter.getStatus()));
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getClientId() != null) {
            query.setParameter("clientId", filter.getClientId());
        }
        if (filter.getTableId() != null) {
            query.setParameter("tableId", filter.getTableId());
        }
        if (filter.getWaiterId() != null) {
            query.setParameter("waiterId", filter.getWaiterId());
        }
        if (filter.getChefId() != null) {
            query.setParameter("chefId", filter.getChefId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", filter.getStatus());
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            query.setParameter("statuses", filter.getStatuses());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }

        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
        }

        List<Object[]> result = query.getResultList();

        Long preparingCount = 0L;

        Long readyCount = 0L;

        Long cancelledCount = 0L;

        Long newCount = 0L;

        for (Object[] row : result) {
            preparingCount += (Long) row[0];
            readyCount += (Long) row[1];
            cancelledCount += (Long) row[2];
            newCount += (Long) row[3];
        }
        return new OrderItemStatisticsDTO(preparingCount, readyCount, cancelledCount, newCount);
    }

    private StringBuilder getBaseSql(OrderItemFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from OrderItem oi");
        if (filter.getDepartmentId() != null) {
            sql.append(" inner join MenuItem mi on oi.menuItemId = mi.id ");
            sql.append(" inner join Product p on mi.productId = p.id ");
        }
        sql.append(" inner join Order o on oi.orderId = o.id ");
        sql.append(" join o.branch b ");
        sql.append(" where oi.deleted = false");
        sql.append(" and o.deleted = false");
        sql.append(" and b.companyId =:companyId");
        if (filter.getBranchId() != null) {
            sql.append(" and o.branchId=:branchId");
        }
        if (filter.getOrderId() != null) {
            sql.append(" and o.id=:orderId");
        }

        if (filter.getDepartmentId() != null) {
            sql.append(" and p.departmentId =:departmentId ");
        }
        if (filter.getChefId() != null) {
            sql.append(" and oi.chefId =:chefId ");
        }
        if (filter.getTableId() != null) {
            sql.append(" and o.tableId=:tableId ");
        }
        if (filter.getWaiterId() != null) {
            sql.append(" and oi.creatorId=:waiterId ");
        }
        if (filter.getClientId() != null) {
            sql.append(" and o.clientId=:clientId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and oi.status=:status");
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            sql.append(" and oi.status in (:statuses)");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.orderDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.orderDate<=:toDate");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(o.orderNumber)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<OrderItem> getResultList(OrderItemFilter filter) {
        ResultList<OrderItem> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select oi");
        sql.append(getBaseSql(filter));

        TypedQuery<OrderItem> query = entityManager.createQuery(sql.toString(), OrderItem.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        String countSql = sql.toString().replaceFirst("select oi", "select count(oi.id)");

        sql.append(" order by o.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getOrderId() != null) {
            query.setParameter("orderId", filter.getOrderId());
            countQuery.setParameter("orderId", filter.getOrderId());
        }
        if (filter.getChefId() != null) {
            query.setParameter("chefId", filter.getChefId());
            countQuery.setParameter("chefId", filter.getChefId());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getClientId() != null) {
            query.setParameter("clientId", filter.getClientId());
            countQuery.setParameter("clientId", filter.getClientId());
        }
        if (filter.getTableId() != null) {
            query.setParameter("tableId", filter.getTableId());
            countQuery.setParameter("tableId", filter.getTableId());
        }
        if (filter.getWaiterId() != null) {
            query.setParameter("waiterId", filter.getWaiterId());
            countQuery.setParameter("waiterId", filter.getWaiterId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", OrderItemStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", OrderItemStatus.valueOf(filter.getStatus()));
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            query.setParameter("statuses", filter.getStatuses());
            countQuery.setParameter("statuses", filter.getStatuses());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }

        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }


    @Override
    public List<Order> findFirstOrderByCodeAndStatus(Long companyId, String code, String status, String itemStatus) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select o.*");
        sql.append(" from orders o");
        sql.append(" inner join order_item oi on oi.order_id = o.id");
        sql.append(" inner join branch b on o.branch_id = b.id");
        sql.append(" where oi.deleted = false");
        sql.append(" and b.company_id = :companyId");
        sql.append(" and o.deleted = false");
        sql.append(" and oi.status != :itemStatus");
        sql.append(" and o.status != :status");
        sql.append(" and oi.marks @> to_jsonb(:code)");

        Query query = entityManager.createNativeQuery(sql.toString(), Order.class);
        query.setFirstResult(0);
        query.setMaxResults(1);

        query.setParameter("status", status);
        query.setParameter("itemStatus", itemStatus);
        query.setParameter("code", code);
        query.setParameter("companyId", companyId);

        return query.getResultList();
    }

    @Override
    public OrderItemChefStatsMapperDTO getMobileChefStatistics(ChefStatsFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(oi.id) filter ( where oi.status = :preparing ) as preparing,");
        sql.append(" count(oi.id) filter ( where oi.status = :ready )     as ready,");
        sql.append(" count(oi.id) filter ( where oi.status = :cancelled ) as cancelled");
        sql.append(" from order_item oi");
        sql.append(" inner join orders o on oi.order_id = o.id");
        sql.append(" where oi.deleted is not true");
        if (filter.getBranchId() != null) {
            sql.append(" and o.branch_id = :branchId");
        }
        if (filter.getChefId() != null) {
            sql.append(" and oi.chef_id = :chefId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and oi.updated_date>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and oi.updated_date<=:toDate");
        }
        Query query = entityManager.createNativeQuery(sql.toString(), "OrderItemChefStatsMapper")
                .setParameter("preparing", OrderItemStatus.PREPARING.name())
                .setParameter("ready", OrderItemStatus.READY.name())
                .setParameter("cancelled", OrderItemStatus.CANCELLED.name());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getChefId() != null) {
            query.setParameter("chefId", filter.getChefId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        return (OrderItemChefStatsMapperDTO) query.getSingleResult();
    }
}
