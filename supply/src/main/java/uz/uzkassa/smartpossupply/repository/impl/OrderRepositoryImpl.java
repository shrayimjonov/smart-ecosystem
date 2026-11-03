package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.dto.agent.performancereport.*;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.order.OrderExpeditorReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderExpeditorTotalQtyReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderStatisticsItemDTO;
import uz.uzkassa.smartpossupply.dto.order.PlanRouteExpeditorDTO;
import uz.uzkassa.smartpossupply.dto.order.SalesAndCustomerReportDTO;
import uz.uzkassa.smartpossupply.dto.order.SalesAndCustomerSummaryDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalOrderStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.user.supervisor.AgentStatisticByCategoryRowDTO;
import uz.uzkassa.smartpossupply.dto.user.supervisor.AgentStatisticByProductRowDTO;
import uz.uzkassa.smartpossupply.enums.OrderSource;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.OrderType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.filters.AgentPerformanceReportFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.filters.OrderReportFilter;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;
import uz.uzkassa.smartpossupply.filters.SupervisorOrderFilter;
import uz.uzkassa.smartpossupply.repository.OrderRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class OrderRepositoryImpl extends BaseFilterableRepositoryImpl<Order, OrderFilter> implements OrderRepositoryCustom {

    @Override
    protected Class<Order> getEntityClass() {
        return Order.class;
    }

    @Override
    protected String getEntityAlias() {
        return "o";
    }

    @Override
    public void applyFilter(OrderFilter filter) {
        if (!Objects.equals(filter.getSource(), OrderSource.TELEGRAM.getCode())) {
            filter.setCompanyId(getCompanyId(filter));
        }
        if (!SecurityUtils.isNetworkAgent()) {
            filter.setBranchId(getBranchId(filter, false));
        }
        filter.setWarehouseId(getWarehouseId(filter, false));
        if (SecurityUtils.isAgent()) {
            filter.setCreatorId(SecurityUtils.getCurrentUserId());
        }
    }

    @Override
    public void setParameters(TypedQuery<?> query, OrderFilter filter) {
        query.setParameter("companyId", filter.getCompanyId());
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (SecurityUtils.isNetworkAgent()) {
            query.setParameter("branchIds", SecurityUtils.getCurrentBranchIds());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
        }
        if (filter.getPlanRouteId() != null) {
            query.setParameter("planRouteId", filter.getPlanRouteId());
        }
        if (filter.getTripId() != null) {
            query.setParameter("tripId", filter.getTripId());
        }
        if (filter.getPlanRouteCustomerBranchId() != null) {
            query.setParameter("planRouteCustomerBranchId", filter.getPlanRouteCustomerBranchId());
        }
        if (filter.getExpeditorId() != null) {
            query.setParameter("expeditorId", filter.getExpeditorId());
        } else if (!CollectionUtils.isEmpty(filter.getExpeditors())) {
            query.setParameter("expeditorIds", filter.getExpeditors());
        }
        if (!CollectionUtils.isEmpty(filter.getPlanRouteTemplateIds())) {
            query.setParameter("planRouteTemplateIds", filter.getPlanRouteTemplateIds());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getSupplierId() != null) {
            query.setParameter("supplierId", filter.getSupplierId());
        }
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", OrderStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.hasText(filter.getExcludeStatus())) {
            query.setParameter("excludeStatus", OrderStatus.valueOf(filter.getExcludeStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        if (StringUtils.hasText(filter.getOrderType())) {
            query.setParameter("orderType", OrderType.valueOf(filter.getOrderType()));
        }
        if (StringUtils.hasText(filter.getPaymentType())) {
            query.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()));
        }
        if (StringUtils.hasText(filter.getSource())) {
            query.setParameter("orderSource", OrderSource.valueOf(filter.getSource()));
        }
    }

    @Override
    public ResultList<OrderReportDTO> getResultListForReport(OrderReportFilter filter) {
        filter.setCompanyId(getCompanyId(filter));

        ResultList<OrderReportDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder("""
                        select 
                        o.id,
                        o.branch_id,
                        b.name as branch_name,
                        o.agent_id,
                        concat(u.first_name, ' ', u.last_name) as agent_name,
                        o.status,
                        coalesce(o.total_qty, 0) as total_qty,
                        (
                            select count(distinct op2.product_id)
                            from order_product op2
                            where op2.order_id = o.id and op2.deleted = false
                        ) as product_count,
                        coalesce(sum(pq.quantity_in_package), 0) as package_count,
                        coalesce(sum(pq.quantity), 0) as product_count_in_package,
                        coalesce(sum(p.weight_brutto), 0) as total_brutto,
                        coalesce(sum(p.weight_netto), 0) as total_netto,
                        coalesce(o.total_price, 0) as total_price,
                        o.payment_type
                    from orders o
                    left join order_product op on o.id = op.order_id and op.deleted = false
                    left join product p on p.id = op.product_id
                    left join package_quantity pq 
                        on pq.entity_id = op.id 
                        and pq.entity_type = 'ORDER_PRODUCT'
                    left join branch b on b.id = o.branch_id
                    left join users u on u.id = o.agent_id
                    where o.deleted = false
                """);


        // filters
        if (filter.getCompanyId() != null) {
            sql.append(" and o.company_id = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and o.branch_id = :branchId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.created_date <= :toDate");
        }
        if (filter.getAgentId() != null) {
            sql.append(" and o.agent_id = :agentId");
        }
        if (!CollectionUtils.isEmpty(filter.getAgentIds())) {
            sql.append(" and o.agent_id in (:agentIds)");
        }
        if (filter.getProductId() != null) {
            sql.append(" and op.product_id = :productId");
        }
        if (StringUtils.hasText(filter.getStatus())) {
            sql.append(" and o.status = :status");
        }
        if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            sql.append(" and o.status in (:statuses)");
        }
        if (filter.getPaymentType() != null) {
            sql.append(" and o.payment_type = :paymentType");
        }
        if (filter.getTotalPrice() != null) {
            sql.append(" and o.total_price = :totalPrice");
        }
        if (filter.getOrderCount() != null) {
            sql.append(" and o.total_qty = :orderCount");
        }

        // GROUP BY
        sql.append("""
                    group by
                    o.id, o.branch_id, b.name,
                    o.agent_id, u.first_name, u.last_name,
                    o.status, o.payment_type
                
                """);

        // HAVING filters
        List<String> having = new ArrayList<>();
        if (filter.getProductCount() != null) {
            having.add("""
                        (
                            select count(distinct op3.product_id)
                            from order_product op3
                            where op3.order_id = o.id and op3.deleted = false
                        ) = :productCount
                    """);
        }

        if (filter.getPackageCount() != null) {
            having.add("coalesce(sum(pq.quantity_in_package), 0) = :packageCount");
        }
        if (filter.getProductCountInPackage() != null) {
            having.add("coalesce(sum(pq.quantity), 0) = :productCountInPackage");
        }

        if (!having.isEmpty()) {
            sql.append(" having ").append(String.join(" and ", having));
        }

        sql.append(" order by o.").append(filter.getOrderBy()).append(" ").append(filter.getSortOrder().getName());

        // Count query
        StringBuilder countSql = new StringBuilder("""
                    select count(*) from (
                        select o.id
                        from orders o
                        join order_product op on o.id = op.order_id and op.deleted = false
                        left join package_quantity pq 
                            on pq.entity_id = op.id 
                           and pq.entity_type = 'ORDER_PRODUCT'
                        where o.deleted = false
                """);

        if (filter.getCompanyId() != null) {
            countSql.append(" and o.company_id = :companyId");
        }
        if (filter.getBranchId() != null) {
            countSql.append(" and o.branch_id = :branchId");
        }
        if (filter.getFrom() != null) {
            countSql.append(" and o.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            countSql.append(" and o.created_date <= :toDate");
        }
        if (filter.getAgentId() != null) {
            countSql.append(" and o.agent_id = :agentId");
        }
        if (!CollectionUtils.isEmpty(filter.getAgentIds())) {
            countSql.append(" and o.agent_id in (:agentIds)");
        }
        if (filter.getProductId() != null) {
            countSql.append(" and op.product_id = :productId");
        }
        if (StringUtils.hasText(filter.getStatus())) {
            countSql.append(" and o.status = :status");
        }
        if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            countSql.append(" and o.status in (:statuses)");
        }
        if (filter.getPaymentType() != null) {
            countSql.append(" and o.payment_type = :paymentType");
        }
        if (filter.getTotalPrice() != null) {
            countSql.append(" and o.total_price = :totalPrice");
        }
        if (filter.getOrderCount() != null) {
            countSql.append(" and o.total_qty = :orderCount");
        }

        countSql.append(" group by o.id");
        if (!having.isEmpty()) {
            countSql.append(" having ").append(String.join(" and ", having));
        }
        countSql.append(") count_table");

        // Create queries
        Query query = entityManager.createNativeQuery(sql.toString());
        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        // Set parameters
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.getAgentId() != null) {
            query.setParameter("agentId", filter.getAgentId());
            countQuery.setParameter("agentId", filter.getAgentId());
        }
        if (!CollectionUtils.isEmpty(filter.getAgentIds())) {
            query.setParameter("agentIds", filter.getAgentIds());
            countQuery.setParameter("agentIds", filter.getAgentIds());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", OrderStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", OrderStatus.valueOf(filter.getStatus()));
        }
        if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            query.setParameter("statuses", OrderStatus.toEnumCodes(filter.getStatuses()));
            countQuery.setParameter("statuses", OrderStatus.toEnumCodes(filter.getStatuses()));
        }
        if (filter.getPaymentType() != null) {
            query.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()).name());
            countQuery.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()).name());
        }
        if (filter.getTotalPrice() != null) {
            query.setParameter("totalPrice", filter.getTotalPrice());
            countQuery.setParameter("totalPrice", filter.getTotalPrice());
        }
        if (filter.getOrderCount() != null) {
            query.setParameter("orderCount", filter.getOrderCount());
            countQuery.setParameter("orderCount", filter.getOrderCount());
        }
        if (filter.getProductCount() != null) {
            query.setParameter("productCount", filter.getProductCount());
            countQuery.setParameter("productCount", filter.getProductCount());
        }
        if (filter.getPackageCount() != null) {
            query.setParameter("packageCount", filter.getPackageCount());
            countQuery.setParameter("packageCount", filter.getPackageCount());
        }
        if (filter.getProductCountInPackage() != null) {
            query.setParameter("productCountInPackage", filter.getProductCountInPackage());
            countQuery.setParameter("productCountInPackage", filter.getProductCountInPackage());
        }

        // Pagination
        query.setFirstResult(filter.getStart());
        query.setMaxResults(filter.getSize());

        List<Object[]> rows = query.getResultList();
        Number totalCount = (Number) countQuery.getSingleResult();

        // Mapping
        List<OrderReportDTO> orderReportDTOList = new ArrayList<>();
        for (Object[] row : rows) {
            OrderReportDTO dto = new OrderReportDTO();
            dto.setId(((Number) row[0]).longValue());
            dto.setBranchId(((Number) row[1]).longValue());
            dto.setBranchName((String) row[2]);
            dto.setAgentId(row[3] != null ? ((Number) row[3]).longValue() : null);
            dto.setAgentName((String) row[4]);
            dto.setStatus(row[5] != null ? OrderStatus.valueOf(row[5].toString()) : null);
            dto.setTotalQty(toBigDecimal(row[6]));
            dto.setProductCount(((Number) row[7]).longValue());
            dto.setPackageCount(toBigDecimal(row[8]));
            dto.setProductCountInPackage(toBigDecimal(row[9]));
            dto.setTotalBrutto(toBigDecimal(row[10]));
            dto.setTotalNetto(toBigDecimal(row[11]));
            dto.setTotalPrice(toBigDecimal(row[12]));
            dto.setPaymentType(row[13] != null ? PaymentType.valueOf(row[13].toString()) : null);
            orderReportDTOList.add(dto);
        }

        // Get products
        if (!orderReportDTOList.isEmpty()) {
            List<Long> orderIds = orderReportDTOList.stream().map(OrderReportDTO::getId).toList();
            List<Object[]> productRows = entityManager.createNativeQuery("""
                        select op.order_id, p.id as product_id, p.name as product_name
                        from order_product op
                        join product p on p.id = op.product_id
                        where op.deleted = false and op.order_id in (:orderIds)
                    """).setParameter("orderIds", orderIds).getResultList();

            Map<Long, List<CommonDTO>> productMap = new HashMap<>();
            for (Object[] r : productRows) {
                Long orderId = ((Number) r[0]).longValue();
                Long productId = ((Number) r[1]).longValue();
                String productName = (String) r[2];
                productMap.computeIfAbsent(orderId, k -> new ArrayList<>())
                        .add(new CommonDTO(productId, productName));
            }

            for (OrderReportDTO orderReportDTO : orderReportDTOList) {
                orderReportDTO.setProducts(productMap.getOrDefault(orderReportDTO.getId(), Collections.emptyList()));
            }
        }

        resultList.setList(orderReportDTOList);
        resultList.setCount(totalCount.longValue());
        return resultList;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        return new BigDecimal(value.toString());
    }


    public ResultList<Order> getResultListForExpeditor(OrderFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
        if (SecurityUtils.isAgent()) {
            filter.setCreatorId(SecurityUtils.getCurrentUserId());
        }
        ResultList<Order> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select o.* ");
        sql.append(getBaseSqlForExpeditor(filter));
        String baseSql = sql.toString();

        String countSql = " select count(o.id) ".concat(getBaseSqlForExpeditor(filter).toString());

        String sqlQuery = baseSql.concat(
                " order by " +
                        "(cb.latitude is null or cb.longitude is null), " +
                        "case " +
                        "when cb.latitude is not null and cb.longitude is not null then " +
                        "6372.8 * acos( " +
                        "cos(radians(cast(:givenLat as double precision))) * " +
                        "cos(radians(cast(cb.latitude as double precision))) * " +
                        "cos(radians(cast(cb.longitude as double precision)) - " +
                        "radians(cast(:givenLon as double precision))) + " +
                        "sin(radians(cast(:givenLat as double precision))) * " +
                        "sin(radians(cast(cb.latitude as double precision)))) " +
                        "end asc nulls last"
        );


//        sql.append(" order by ");
//        sql.append(" (cb.latitude is null or cb.longitude is null), ");
//        sql.append(" case ");
//        sql.append(" when cb.latitude is not null and cb.longitude is not null then ");
//        sql.append(" 6372.8 * acos( ");
//        sql.append(" cos(radians(cast(:given_lat as double precision))) * ");
//        sql.append(" cos(radians(cast(cb.latitude as double precision))) * ");
//        sql.append(" cos(radians(cast(cb.longitude as double precision)) - ");
//        sql.append(" radians(cast(:given_lon as double precision))) + ");
//        sql.append(" sin(radians(cast(:given_lat as double precision))) * ");
//        sql.append(" sin(radians(cast(cb.latitude as double precision)))) ");
//        sql.append(" end asc nulls last ");


        Query query = entityManager
                .createNativeQuery(sqlQuery, Order.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        Query countQuery = entityManager.createNativeQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
            countQuery.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
            countQuery.setParameter("customerBranchId", filter.getCustomerBranchId());
        }
        if (filter.getExpeditorId() != null) {
            query.setParameter("expeditorId", filter.getExpeditorId());
            countQuery.setParameter("expeditorId", filter.getExpeditorId());
        } else if (!CollectionUtils.isEmpty(filter.getExpeditors())) {
            query.setParameter("expeditorIds", filter.getExpeditors());
            countQuery.setParameter("expeditorIds", filter.getExpeditors());
        }
        if (filter.getPlanRouteId() != null) {
            query.setParameter("planRouteId", filter.getPlanRouteId());
            countQuery.setParameter("planRouteId", filter.getPlanRouteId());
        } else if (!CollectionUtils.isEmpty(filter.getPlanRouteIds())) {
            query.setParameter("planRouteIds", filter.getPlanRouteIds());
            countQuery.setParameter("planRouteIds", filter.getPlanRouteIds());
        }
        if (!CollectionUtils.isEmpty(filter.getPlanRouteTemplateIds())) {
            query.setParameter("planRouteTemplateIds", filter.getPlanRouteTemplateIds());
            countQuery.setParameter("planRouteTemplateIds", filter.getPlanRouteTemplateIds());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getSupplierId() != null) {
            query.setParameter("supplierId", filter.getSupplierId());
            countQuery.setParameter("supplierId", filter.getSupplierId());
        }
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", filter.getStatus());
            countQuery.setParameter("status", filter.getStatus());
        }
        if (StringUtils.hasText(filter.getExcludeStatus())) {
            query.setParameter("excludeStatus", filter.getExcludeStatus());
            countQuery.setParameter("excludeStatus", filter.getExcludeStatus());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }
        if (StringUtils.hasText(filter.getOrderType())) {
            query.setParameter("orderType", OrderType.valueOf(filter.getOrderType()));
            countQuery.setParameter("orderType", OrderType.valueOf(filter.getOrderType()));
        }
        if (StringUtils.hasText(filter.getSource())) {
            query.setParameter("orderSource", OrderSource.valueOf(filter.getSource()));
            countQuery.setParameter("orderSource", OrderSource.valueOf(filter.getSource()));
        }
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
        }
        query.setParameter("givenLat", filter.getLatitude());

        query.setParameter("givenLon", filter.getLongitude());

        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    public ResultList<Order> getResultListForTerminal(OrderFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
        if (SecurityUtils.isAgent()) {
            filter.setCreatorId(SecurityUtils.getCurrentUserId());
        }
        ResultList<Order> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select o ");
        sql.append(" from Order o ");
        sql.append(" join o.customer c ");
        sql.append(" join o.supplier s ");
        sql.append(" join o.stockTransfer st ");
        sql.append(" where o.companyId =:companyId");
        if (!filter.isFetchDeleted()) {
            sql.append(" and o.deleted =false");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and o.branchId=:branchId");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and o.warehouseId=:warehouseId");
        }
        if (filter.getCarId() != null) {
            sql.append(" and st.toWarehouseId=:carId");
        }
        if (filter.getExpeditorId() != null) {
            sql.append(" and o.expeditorId=:expeditorId");
        } else if (!CollectionUtils.isEmpty(filter.getExpeditors())) {
            sql.append(" and o.expeditorIds in (:expeditorIds)");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and o.customerId=:customerId ");
        }
        if (filter.getSupplierId() != null) {
            sql.append(" and o.supplierId=:supplierId ");
        }
        if (StringUtils.hasText(filter.getStatus())) {
            sql.append(" and o.status=:status ");
        }
        if (StringUtils.hasText(filter.getExcludeStatus())) {
            sql.append(" and o.status!=:excludeStatus ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.orderDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.orderDate<=:toDate");
        }
        if (StringUtils.hasText(filter.getOrderType())) {
            sql.append(" and o.orderType=:orderType");
        }
        if (StringUtils.hasText(filter.getSource())) {
            sql.append(" and o.orderSource=:orderSource");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and o.creatorId=:creatorId");
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(o.orderNumber)").append(filter.getSearchKey());
            sql.append(" or lower(s.tin)").append(filter.getSearchKey());
            sql.append(" or lower(s.name)").append(filter.getSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getSearchKey());
            sql.append(" or lower(c.name)").append(filter.getSearchKey());
            sql.append(") ");
        }

        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)");

        sql.append(" order by o.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Order> query = entityManager
                .createQuery(sql.toString(), Order.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
            countQuery.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getCarId() != null) {
            query.setParameter("carId", filter.getCarId());
            countQuery.setParameter("carId", filter.getCarId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getExpeditorId() != null) {
            query.setParameter("expeditorId", filter.getExpeditorId());
            countQuery.setParameter("expeditorId", filter.getExpeditorId());
        } else if (!CollectionUtils.isEmpty(filter.getExpeditors())) {
            query.setParameter("expeditorIds", filter.getExpeditors());
            countQuery.setParameter("expeditorIds", filter.getExpeditors());
        }
        if (filter.getSupplierId() != null) {
            query.setParameter("supplierId", filter.getSupplierId());
            countQuery.setParameter("supplierId", filter.getSupplierId());
        }
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", OrderStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", OrderStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.hasText(filter.getExcludeStatus())) {
            query.setParameter("excludeStatus", OrderStatus.valueOf(filter.getExcludeStatus()));
            countQuery.setParameter("excludeStatus", OrderStatus.valueOf(filter.getExcludeStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }
        if (StringUtils.hasText(filter.getOrderType())) {
            query.setParameter("orderType", OrderType.valueOf(filter.getOrderType()));
            countQuery.setParameter("orderType", OrderType.valueOf(filter.getOrderType()));
        }
        if (StringUtils.hasText(filter.getSource())) {
            query.setParameter("orderSource", OrderSource.valueOf(filter.getSource()));
            countQuery.setParameter("orderSource", OrderSource.valueOf(filter.getSource()));
        }
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public OrderStatisticsDTO getStatistics(OrderFilter filter) {
        applyFilter(filter);
        String sql = "select " +
            " (case when o.status = 'NEW' then count(distinct o.id) else 0 end) as newTotal," +
            " (case when  o.status = 'DRAFT' then count(distinct o.id) else 0 end) as draftTotal, " +
            " (case when  o.status = 'IN_PROGRESS' then count(distinct o.id) else 0 end) as inProgressTotal, " +
            " (case when  o.status = 'APPROVED' then count(distinct o.id) else 0 end) as approvedTotal, " +
            " (case when  o.status = 'REJECTED' then count(distinct o.id) else 0 end) as rejectedTotal, " +
            " (case when  o.status = 'COMPLETED' then count(distinct o.id) else 0 end) as completedTotal " +
            getBaseQuery(filter, false) +
            " group by o.status ";

        TypedQuery<Object[]> query = entityManager.createQuery(sql, Object[].class);
        setParameters(query, filter);

        List<Object[]> result = query.getResultList();

        Long newTotal = 0L;

        Long draftTotal = 0L;

        Long inProgressTotal = 0L;

        Long approvedTotal = 0L;

        Long rejectedTotal = 0L;

        Long completedTotal = 0L;

        for (Object[] row : result) {
            newTotal += (Long) row[0];
            draftTotal += (Long) row[1];
            inProgressTotal += (Long) row[2];
            approvedTotal += (Long) row[3];
            rejectedTotal += (Long) row[4];
            completedTotal += (Long) row[5];
        }
        return new OrderStatisticsDTO(newTotal, draftTotal, inProgressTotal, approvedTotal, rejectedTotal, completedTotal);
    }

    @Override
    public MobileOrderStatisticsDTO getOrderStatisticsForMobile(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(o.id) as orderTotalCount,");
        sql.append(" sum(o.total_price) as orderTotalAmount, 'TODAY' as type ");
        sql.append(" from orders o");
        sql.append(" where o.deleted =false");
        sql.append(" and o.creator_id=:userId");
        sql.append(" and DATE(o.order_date)=DATE(now())");

        sql.append(" union all\n");
        sql.append(" select count(o.id) as orderTotalCount,");
        sql.append(" sum(o.total_price) as orderTotalAmount, 'TOTAL' as type");
        sql.append(" from orders o");
        sql.append(" where o.deleted =false");
        sql.append(" and o.creator_id=:userId");

        Query query = entityManager.createNativeQuery(sql.toString(), "MobileOrderStatisticsMapper");
        query.setParameter("userId", SecurityUtils.getCurrentUserId());

        MobileOrderStatisticsDTO orderStatisticsDTO = new MobileOrderStatisticsDTO();
        List<OrderStatisticsItemDTO> resultList = query.getResultList();
        resultList.forEach(orderStatistics -> {
            switch (orderStatistics.getType()) {
                case "TODAY": {
                    orderStatisticsDTO.setOrderTotalCountForToday(orderStatistics.getOrderTotalCount());
                    orderStatisticsDTO.setOrderTotalAmountForToday(orderStatistics.getOrderTotalAmount());
                    break;
                }
                case "TOTAL": {
                    orderStatisticsDTO.setOrderTotalCount(orderStatistics.getOrderTotalCount());
                    orderStatisticsDTO.setOrderTotalAmount(orderStatistics.getOrderTotalAmount());
                    break;
                }
            }
        });
        return orderStatisticsDTO;
    }

    @Override
    public List<AgentPerformanceReportOrderPaymentDTO> getAgentPerformanceReportPayments(AgentPerformanceReportFilter filter) {
        boolean filterByPeriod = filter.getFrom() != null && filter.getTo() != null;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.payment_type, SUM(o.total_price) FROM orders o ");
        sql.append(" WHERE o.deleted = false ");
        sql.append(" AND o.payment_type is not null ");
        sql.append(" AND o.agent_id =:agentId ");
        sql.append(" AND o.branch_id =:branchId ");
        sql.append(" AND o.status =:status ");
        if (StringUtils.hasText(filter.getPaymentType())) {
            sql.append(" AND o.payment_type =:paymentType ");
        }
        if (filterByPeriod) {
            sql.append(" AND (o.order_date BETWEEN :from AND :to) ");
        }
        sql.append(" GROUP BY o.payment_type ");

        Query query = entityManager.createNativeQuery(sql.toString(), AgentPerformanceReportOrderPaymentDTO.class);
        query.setParameter("agentId", filter.getAgentId());
        query.setParameter("branchId", filter.getBranchId());
        query.setParameter("status", OrderStatus.COMPLETED.getCode());
        if (StringUtils.hasText(filter.getPaymentType())) {
            query.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()).getCode());
        }
        if (filterByPeriod) {
            query.setParameter("from", filter.getFrom());
            query.setParameter("to", filter.getTo());
        }
        return query.getResultList();
    }

    @Override
    public BigDecimal getAgentPerformanceReportOrderTotalPrice(AgentPerformanceReportFilter filter) {
        boolean filterByPeriod = filter.getFrom() != null && filter.getTo() != null;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT SUM(o.total_price) FROM orders o ");
        sql.append(" WHERE o.deleted = false ");
        sql.append(" AND o.agent_id =:agentId ");
        sql.append(" AND o.branch_id =:branchId ");
        sql.append(" AND o.status =:status ");
        if (StringUtils.hasText(filter.getPaymentType())) {
            sql.append(" AND o.payment_type =:paymentType ");
        }
        if (filterByPeriod) {
            sql.append(" AND (o.order_date BETWEEN :from AND :to) ");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), BigDecimal.class);
        query.setParameter("agentId", filter.getAgentId());
        query.setParameter("branchId", filter.getBranchId());
        query.setParameter("status", OrderStatus.COMPLETED.getCode());
        if (StringUtils.hasText(filter.getPaymentType())) {
            query.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()).getCode());
        }
        if (filterByPeriod) {
            query.setParameter("from", filter.getFrom());
            query.setParameter("to", filter.getTo());
        }
        return (BigDecimal) query.getSingleResult();
    }


    @Override
    public String getBaseQuery(OrderFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM Order o ");
        if (includeFetch) {
            sql.append("LEFT JOIN FETCH o.customer c ");
            sql.append("JOIN o.supplier s ");
            sql.append("LEFT JOIN FETCH o.branch b ");
            sql.append("LEFT JOIN FETCH o.stockOut stockOut ");
            sql.append("LEFT JOIN FETCH o.creator cr ");
            sql.append("LEFT JOIN FETCH o.trip t ");
            sql.append("LEFT JOIN FETCH o.planRoute pr ");
            if (filter.getCustomerBranchId() != null) {
                sql.append("LEFT JOIN o.planRouteCustomerBranch cb ");
            }
        } else {
            sql.append(" JOIN o.customer c ");
            sql.append(" JOIN o.supplier s ");
            if (filter.getCustomerBranchId() != null || filter.getPlanRouteId() != null || !CollectionUtils.isEmpty(filter.getPlanRouteTemplateIds())) {
                sql.append(" JOIN o.planRouteCustomerBranch cb ");
                sql.append(" JOIN o.planRoute pr ");
            }
        }
        sql.append(" WHERE o.companyId =:companyId");
        if (!filter.isFetchDeleted()) {
            sql.append(" AND o.deleted = false");
        }
        if (filter.getBranchId() != null) {
            sql.append(" AND o.branchId = :branchId");
        }
        if (SecurityUtils.isNetworkAgent()) {
            sql.append(" AND o.branchId IN (:branchIds)");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" AND o.warehouseId = :warehouseId");
        }
        if (filter.getCustomerBranchId() != null) {
            sql.append(" AND cb.customerBranchId = :customerBranchId");
        }
        if (filter.getTripId() != null) {
            sql.append(" AND o.tripId = :tripId");
        }
        if (filter.getPlanRouteId() != null) {
            sql.append(" AND pr.id = :planRouteId");
        }
        if (filter.getPlanRouteCustomerBranchId() != null) {
            sql.append(" AND o.planRouteCustomerBranchId = :planRouteCustomerBranchId");
        }
        if (filter.getExpeditorId() != null) {
            sql.append(" AND o.expeditorId = :expeditorId");
        } else if (!CollectionUtils.isEmpty(filter.getExpeditors())) {
            sql.append(" AND o.expeditorId IN (:expeditorIds)");
        }
        if (!CollectionUtils.isEmpty(filter.getPlanRouteTemplateIds())) {
            sql.append(" AND pr.templateId IN (:planRouteTemplateIds)");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" AND o.customerId = :customerId");
        }
        if (filter.getSupplierId() != null) {
            sql.append(" AND o.supplierId = :supplierId");
        }
        if (StringUtils.hasText(filter.getStatus())) {
            sql.append(" AND o.status = :status");
        }
        if (StringUtils.hasText(filter.getExcludeStatus())) {
            sql.append(" AND o.status != :excludeStatus");
        }
        if (filter.getFrom() != null) {
            sql.append(" AND o.orderDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" AND o.orderDate <= :toDate");
        }
        if (StringUtils.hasText(filter.getOrderType())) {
            sql.append(" AND o.orderType = :orderType");
        }
        if (StringUtils.hasText(filter.getSource())) {
            sql.append(" AND o.orderSource = :orderSource");
        }
        if (StringUtils.hasText(filter.getPaymentType())) {
            sql.append(" AND o.paymentType = :paymentType");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" AND o.creatorId = :creatorId");
        }
        if (filter.getUsed() != null) {
            if (filter.getUsed()) {
                sql.append(" AND o.stockOutId IS NOT NULL");
            } else {
                sql.append(" AND o.stockOutId IS NULL");
                sql.append(" AND o.status IN ('NEW','DRAFT','IN_PROGRESS','APPROVED')");
            }
        }
        if (filter.isNotEmpty()) {
            sql.append(" AND (");
            sql.append(" LOWER(o.orderNumber)").append(filter.getSearchKey());
            sql.append(" OR LOWER(s.tin)").append(filter.getSearchKey());
            sql.append(" OR LOWER(s.name)").append(filter.getSearchKey());
            sql.append(" OR LOWER(c.tin)").append(filter.getSearchKey());
            sql.append(" OR LOWER(c.name)").append(filter.getSearchKey());
            sql.append(" OR LOWER(c.brand)").append(filter.getSearchKey());
            sql.append(")");
        }
        return sql.toString();
    }

    private StringBuilder getBaseSqlForExpeditor(OrderFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from orders o ");
        sql.append(" join company c on o.customer_id=c.id ");
        sql.append(" join company s on o.supplier_id=s.id ");
        sql.append(" join plan_route_customer_branch prc on o.plan_route_customer_branch_id=prc.id ");
        sql.append(" join plan_route pr on o.plan_route_id=pr.id ");
        sql.append(" join customer_branch cb on prc.customer_branch_id=cb.id ");
        sql.append(" where o.company_id =:companyId");
        if (!filter.isFetchDeleted()) {
            sql.append(" and o.deleted =false");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and o.branch_id=:branchId");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and o.warehouse_id=:warehouseId");
        }
        if (filter.getCustomerBranchId() != null) {
            sql.append(" and prc.customer_branch_id=:customerBranchId");
        }
        if (filter.getPlanRouteId() != null) {
            sql.append(" and pr.id=:planRouteIds");
        } else if (!CollectionUtils.isEmpty(filter.getPlanRouteIds())) {
            sql.append(" and pr.id in (:planRouteIds)");
        }
        if (filter.getExpeditorId() != null) {
            sql.append(" and o.expeditor_id=:expeditorId");
        } else if (!CollectionUtils.isEmpty(filter.getExpeditors())) {
            sql.append(" and o.expeditor_id in (:expeditorIds)");
        }
        if (!CollectionUtils.isEmpty(filter.getPlanRouteTemplateIds())) {
            sql.append(" and pr.template_id in (:planRouteTemplateIds)");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and o.customer_id=:customerId ");
        }
        if (filter.getSupplierId() != null) {
            sql.append(" and o.supplier_id=:supplierId ");
        }
        if (StringUtils.hasText(filter.getStatus())) {
            sql.append(" and o.status=:status ");
        }
        if (StringUtils.hasText(filter.getExcludeStatus())) {
            sql.append(" and o.status!=:excludeStatus ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.order_date>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.order_date<=:toDate");
        }
        if (StringUtils.hasText(filter.getOrderType())) {
            sql.append(" and o.order_type=:orderType");
        }
        if (StringUtils.hasText(filter.getSource())) {
            sql.append(" and o.order_source=:orderSource");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and o.creator_id=:creatorId");
        }
        if (filter.getUsed() != null) {
            if (filter.getUsed()) {
                sql.append(" and o.stock_out_id is not null ");
            } else {
                sql.append(" and o.stock_out_id is null ");
                sql.append(" and o.status in ('NEW','DRAFT','IN_PROGRESS','APPROVED') ");
            }
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(o.order_number)").append(filter.getSearchKey());
            sql.append(" or lower(s.tin)").append(filter.getSearchKey());
            sql.append(" or lower(s.name)").append(filter.getSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getSearchKey());
            sql.append(" or lower(c.name)").append(filter.getSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public Page<OrderProductQtyDetailListDTO> getQtyDetails(OrderFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter));
        filter.setWarehouseId(getWarehouseId(filter));
        StringBuilder sql = new StringBuilder();
        sql.append("select o.id as id, o.order_number as orderNumber, o.order_date as orderDate, o.status as orderStatus, op.qty as qty, op.price as price, op.price_without_vat as priceWithoutVat, op.vat_price  as vatPrice, op.total_price_without_vat as totalPriceWithoutVat, op.total_price as totalPrice, c.id as companyId, c.name as companyName, c.tin as companyTin, c.brand as companyBrand, a.id as agentId, a.first_name as agentFirstName, a.last_name as agentLastName, o.updated_date as lastModifiedDate ");
        sql.append(getReportBaseSql(filter));
        sql.append(" order by o.id desc");

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(o.id) ");
        countSql.append(getReportBaseSql(filter));

        Query query = entityManager
                .createNativeQuery(sql.toString(), "OrderProductQtyDetailListMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        if (filter.getFrom() != null) {
            countQuery.setParameter("fromDate", filter.getFrom());
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            countQuery.setParameter("toDate", filter.getTo());
            query.setParameter("toDate", filter.getTo());
        }
        long count = (long) countQuery.getSingleResult();
        if (count > 0) {
            return new PageImpl<>(query.getResultList(), filter.getSortedPageable(), count);
        }
        return new PageImpl<>(new ArrayList<>(), filter.getSortedPageable(), count);
    }

    @Override
    public SalesAndCustomerSummaryDTO getSalesAndCustomerSummary(OrderFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));

        StringBuilder sql = new StringBuilder("""
                select
                    coalesce(count(distinct op.product_id), 0) as totalProductsCount,
                    coalesce(sum(op.qty), 0) as totalQuantity,
                    coalesce(sum(op.total_price_without_vat), 0) as totalAmountWithoutVat,
                    coalesce(sum(op.total_vat_price), 0) as totalVatAmount,
                    coalesce(sum(op.total_price), 0) as totalAmountWithVat
                from orders o
                join order_product op on o.id = op.order_id
                """);

        if (filter.getCategoryId() != null) {
            sql.append("""
                    join product p on p.id = op.product_id and p.deleted = false
                    join category c on c.id = p.category_id and c.deleted = false
                    """);
        }

        sql.append("""
        where o.deleted = false
          and op.deleted = false
          and o.order_type = :orderType
          and o.status = :completedStatus
        """);


        if (filter.getFrom() != null) {
            sql.append(" and o.order_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.order_date <= :toDate");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and o.company_id = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and o.branch_id = :branchId");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and o.warehouse_id = :warehouseId");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and o.customer_id = :customerId");
        }
        if (filter.getProductId() != null) {
            sql.append(" and op.product_id = :productId");
        }
        if (filter.getCategoryId() != null) {
            sql.append(" and c.id = :categoryId");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), "SalesAndCustomerSummaryMapper");
        query.setParameter("orderType", OrderType.OUTGOING.name());
        query.setParameter("completedStatus", OrderStatus.COMPLETED.name());
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
        }

        return (SalesAndCustomerSummaryDTO) query.getSingleResult();
    }

    @Override
    public Page<SalesAndCustomerReportDTO> getSalesAndCustomerReport(OrderFilter filter) {
        filter.setCompanyId(getCompanyId(filter));

        StringBuilder sql = new StringBuilder("""
                    select
                        c.name as categoryName,
                        cust.name as customerName,
                        cust.tin as customerTin,
                        cb.name as customerBranch,
                        p.name as productName,
                        p.barcode as barcode,
                        pc.name as contractNumber,
                        pc.price as contractPrice,
                        op.qty as quantity,
                        ps.serial_number as serialNumber,
                        op.base_price as basePrice,
                        op.markup_percent as markupPercent,
                        op.markup_price as markupAmount,
                        op.price_without_vat as priceWithoutVat,
                
                        op.vat_price as vatPrice,
                        op.total_price as priceWithVat,
                        br.name as branch,
                        w.name as warehouse
                
                    from order_product op
                    join orders o on o.id = op.order_id
                    left join product p on p.id = op.product_id and p.deleted = false
                    left join category c on c.id = p.category_id and c.deleted = false
                    left join company cust on cust.id = o.customer_id and cust.deleted = false
                    left join customer_branch cb on cb.id = o.delivery_branch_id and cb.deleted = false
                    left join product_contract pc on pc.id = op.product_contract_id and pc.deleted = false
                    left join product_serial ps on ps.id = op.product_serial_id and ps.deleted = false
                    left join branch br on br.id = o.branch_id and br.deleted = false
                    left join warehouse w on w.id = o.warehouse_id and w.deleted = false
                    where o.deleted = false
                      and op.deleted = false
                      and o.order_type = :orderType
                      and o.status = :completedStatus
                """);



        if (filter.getFrom() != null) {
            sql.append(" and o.order_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.order_date <= :toDate");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and o.company_id = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and o.branch_id = :branchId");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and o.warehouse_id = :warehouseId");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and o.customer_id = :customerId");
        }
        if (filter.getProductId() != null) {
            sql.append(" and op.product_id = :productId");
        }
        if (filter.getCategoryId() != null) {
            sql.append(" and c.id = :categoryId");
        }

        sql.append(" order by ").append("op.id").append(" ").append(filter.getSortOrder().getName());

        StringBuilder countSql = new StringBuilder("""
                select count(*)
                from order_product op
                join orders o on o.id = op.order_id
                left join product p on p.id = op.product_id and p.deleted = false
                left join category c on c.id = p.category_id and c.deleted = false
                where o.deleted = false
                  and op.deleted = false
                  and o.order_type = :orderType
                  and o.status = :completedStatus
                """);

        if (filter.getFrom() != null) {
            countSql.append(" and o.order_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            countSql.append(" and o.order_date <= :toDate");
        }
        if (filter.getCompanyId() != null) {
            countSql.append(" and o.company_id = :companyId");
        }
        if (filter.getBranchId() != null) {
            countSql.append(" and o.branch_id = :branchId");
        }
        if (filter.getWarehouseId() != null) {
            countSql.append(" and o.warehouse_id = :warehouseId");
        }
        if (filter.getCustomerId() != null) {
            countSql.append(" and o.customer_id = :customerId");
        }
        if (filter.getProductId() != null) {
            countSql.append(" and op.product_id = :productId");
        }
        if (filter.getCategoryId() != null) {
            countSql.append(" and c.id = :categoryId");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), "SalesAndCustomerReportMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        query.setParameter("orderType", OrderType.OUTGOING.name());
        countQuery.setParameter("orderType", OrderType.OUTGOING.name());

        query.setParameter("completedStatus", OrderStatus.COMPLETED.name());
        countQuery.setParameter("completedStatus", OrderStatus.COMPLETED.name());

        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
            countQuery.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
            countQuery.setParameter("categoryId", filter.getCategoryId());
        }

        Number totalCount = (Number) countQuery.getSingleResult();
        return new PageImpl<>(query.getResultList(), filter.getSortedPageable(), totalCount.longValue());
    }

    @Override
    public List<AgentStatisticByCategoryRowDTO> getAgentStatisticsByCategory(Collection<Long> agentIds, SimpleFilter filter) {
        String sql = """
                SELECT o.agent_id                                   AS agentId,
                       c.id                                         AS categoryId,
                       c.name                                       AS categoryName,
                       u.id                                         AS unitId,
                       u.name_ru                                    AS unitName,
                       coalesce(sum(op.total_brutto), 0)            AS totalBrutto,
                       coalesce(sum(op.total_netto), 0)             AS totalNetto,
                       coalesce(sum(op.qty), 0)                     AS totalQty,
                       coalesce(sum(op.total_qty_in_package), 0)    AS totalQtyInPackage
                FROM orders o
                         join order_product op on op.order_id = o.id
                         join product p on op.product_id = p.id
                         join category c on p.category_id = c.id
                         join unit u on op.unit_id = u.id
                Where o.agent_id IN (:agentIds)
                  AND o.created_date BETWEEN :from AND :to
                GROUP BY o.agent_id, c.id, u.id
                """;
        Query nativeQuery = entityManager.createNativeQuery(sql, AgentStatisticByCategoryRowDTO.class);
        nativeQuery.setParameter("agentIds", agentIds);
        nativeQuery.setParameter("from", filter.getFrom());
        nativeQuery.setParameter("to", filter.getTo());
        return nativeQuery.getResultList();
    }

    @Override
    public List<AgentStatisticByProductRowDTO> getAgentStatisticsByProduct(Collection<Long> agentIds, SimpleFilter filter) {
        String sql = """
                SELECT o.agent_id                                   AS agentId,
                       p.id                                         AS productId,
                       p.name                                       AS productName,
                       u.id                                         AS unitId,
                       u.name_ru                                    AS unitName,
                       coalesce(sum(op.total_brutto), 0)            AS totalBrutto,
                       coalesce(sum(op.total_netto), 0)             AS totalNetto,
                       coalesce(sum(op.qty), 0)                     AS totalQty,
                       coalesce(sum(op.total_qty_in_package), 0)    AS totalQtyInPackage
                FROM orders o
                         join order_product op on op.order_id = o.id
                         join product p on op.product_id = p.id
                         join category c on p.category_id = c.id
                         join unit u on op.unit_id = u.id
                Where o.agent_id IN (:agentIds)
                  AND o.created_date BETWEEN :from AND :to
                GROUP BY o.agent_id, p.id, u.id
                """;
        Query nativeQuery = entityManager.createNativeQuery(sql, AgentStatisticByProductRowDTO.class);
        nativeQuery.setParameter("agentIds", agentIds);
        nativeQuery.setParameter("from", filter.getFrom());
        nativeQuery.setParameter("to", filter.getTo());
        return nativeQuery.getResultList();
    }

    @Override
    public List<AgentPerformanceReportOrderDTO> getAgentPerformanceReport(AgentPerformanceReportFilter filter) {
        boolean filterByPeriod = filter.getFrom() != null && filter.getTo() != null;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.status, COUNT(o.id) FROM orders o ");
        sql.append(" WHERE o.deleted = false AND o.status !=:status ");
        sql.append(" AND o.agent_id =:agentId");
        sql.append(" AND o.branch_id =:branchId");
        sql.append(" AND o.status !=:status");
        if (StringUtils.hasText(filter.getPaymentType())) {
            sql.append(" AND o.payment_type =:paymentType");
        }
        if (filterByPeriod) {
            sql.append(" AND (o.order_date BETWEEN :from AND :to) ");
        }
        sql.append(" GROUP BY o.status ");
        Query query = entityManager.createNativeQuery(sql.toString(), AgentPerformanceReportOrderDTO.class);
        query.setParameter("agentId", filter.getAgentId());
        query.setParameter("branchId", filter.getBranchId());
        query.setParameter("status", OrderStatus.DELETED.getCode());
        if (StringUtils.hasText(filter.getPaymentType())) {
            query.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()).getCode());
        }
        if (filterByPeriod) {
            query.setParameter("from", filter.getFrom());
            query.setParameter("to", filter.getTo());
        }
        return query.getResultList();
    }

    @Override
    public List<AgentPerformanceReportOrderProductDTO> getAgentPerformanceReportOrderProducts(AgentPerformanceReportFilter filter) {
        boolean filterByPeriod = filter.getFrom() != null && filter.getTo() != null;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT u.id, u.name_ru, u.code, COUNT(o.id) FROM orders o ");
        sql.append(" INNER JOIN order_product op on o.id = op.order_id ");
        sql.append(" INNER JOIN unit u on u.id = op.unit_id ");
        sql.append(" WHERE o.deleted = false ");
        sql.append(" AND o.agent_id =:agentId");
        sql.append(" AND o.branch_id =:branchId");
        sql.append(" AND o.status =:status");
        if (StringUtils.hasText(filter.getPaymentType())) {
            sql.append(" AND o.payment_type =:paymentType");
        }
        if (filterByPeriod) {
            sql.append(" AND (o.order_date BETWEEN :from AND :to) ");
        }
        sql.append(" GROUP BY u.id");
        Query query = entityManager.createNativeQuery(sql.toString(), AgentPerformanceReportOrderProductDTO.class);
        query.setParameter("agentId", filter.getAgentId());
        query.setParameter("branchId", filter.getBranchId());
        query.setParameter("status", OrderStatus.COMPLETED.getCode());
        if (StringUtils.hasText(filter.getPaymentType())) {
            query.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()).getCode());
        }
        if (filterByPeriod) {
            query.setParameter("from", filter.getFrom());
            query.setParameter("to", filter.getTo());
        }
        return query.getResultList();
    }

    @Override
    public List<AgentPerformanceReportOrderCategoryDTO> getAgentPerformanceReportOrderCategories(AgentPerformanceReportFilter filter) {
        boolean filterByPeriod = filter.getFrom() != null && filter.getTo() != null;
        StringBuilder sql = new StringBuilder();
        sql.append("WITH top_categories AS (");
        sql.append(" SELECT c.id AS category_id FROM orders o ");
        sql.append(" JOIN order_product op ON o.id = op.order_id ");
        sql.append(" JOIN product p ON p.id = op.product_id ");
        sql.append(" JOIN category c ON c.id = p.category_id ");
        sql.append(" WHERE o.deleted = false ");
        sql.append(" AND o.agent_id =:agentId ");
        sql.append(" AND o.branch_id =:branchId ");
        sql.append(" AND o.status =:status ");
        if(StringUtils.hasText(filter.getPaymentType())){
            sql.append(" AND o.payment_type =:paymentType ");
        }
        if (filterByPeriod) {
            sql.append(" AND (o.order_date BETWEEN :from AND :to) ");
        }
        sql.append(" GROUP BY c.id ");
        sql.append(" ORDER BY SUM(op.qty) DESC LIMIT :limit");
        sql.append(")");

        sql.append(" SELECT c.id AS category_id, ");
        sql.append(" c.name AS category_name, ");
        sql.append(" u.id AS unit_id, ");
        sql.append(" u.name_ru AS unit_name, ");
        sql.append(" SUM(op.qty) AS total_count ");
        sql.append(" FROM top_categories tc ");
        sql.append(" JOIN category c ON c.id = tc.category_id ");
        sql.append(" JOIN product p ON p.category_id = c.id ");
        sql.append(" JOIN order_product op ON op.product_id = p.id ");
        sql.append(" JOIN orders o ON o.id = op.order_id ");
        sql.append(" JOIN unit u ON u.id = op.unit_id ");
        sql.append(" WHERE o.deleted = false ");
        sql.append(" AND o.agent_id =:agentId ");
        sql.append(" AND o.branch_id =:branchId ");
        sql.append(" AND o.status =:status ");
        if(StringUtils.hasText(filter.getPaymentType())){
            sql.append(" AND o.payment_type =:paymentType ");
        }
        if (filterByPeriod) {
            sql.append(" AND (o.order_date BETWEEN :from AND :to) ");
        }
        sql.append(" GROUP BY c.id, c.name, u.id, u.name_ru ");
        sql.append(" ORDER BY SUM(op.qty) DESC, u.id ");

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("agentId", filter.getAgentId());
        query.setParameter("branchId", filter.getBranchId());
        query.setParameter("status", OrderStatus.COMPLETED.getCode());
        query.setParameter("limit", 5);
        if (StringUtils.hasText(filter.getPaymentType())) {
            query.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()).getCode());
        }
        if (filterByPeriod) {
            query.setParameter("from", filter.getFrom());
            query.setParameter("to", filter.getTo());
        }

        List<Object[]> rows = query.getResultList();
        Map<Long, AgentPerformanceReportOrderCategoryDTO> categoryMap = new LinkedHashMap<>();

        for (Object[] row : rows) {

            Long categoryId     = ((Long) row[0]);
            String categoryName = (String) row[1];
            Long unitId         = ((Long) row[2]);
            String unitName     = (String) row[3];
            BigDecimal totalCount     = ((BigDecimal) row[4]);

            AgentPerformanceReportOrderCategoryDTO categoryDTO =
                    categoryMap.computeIfAbsent(categoryId, id -> {
                        AgentPerformanceReportOrderCategoryDTO dto = new AgentPerformanceReportOrderCategoryDTO();

                        CommonDTO category = new CommonDTO();
                        category.setId(categoryId);
                        category.setName(categoryName);

                        dto.setCategory(category);
                        dto.setUnits(new ArrayList<>());
                        return dto;
                    });

            AgentPerformanceReportOrderCategoryUnitDTO unitDTO = new AgentPerformanceReportOrderCategoryUnitDTO();

            CommonDTO unit = new CommonDTO();
            unit.setId(unitId);
            unit.setName(unitName);

            unitDTO.setUnit(unit);
            unitDTO.setTotalCount(totalCount);

            categoryDTO.getUnits().add(unitDTO);
        }
        return  new ArrayList<>(categoryMap.values());
    }

    @Override
    public ResultList<Order> getSupervisorOrder(SupervisorOrderFilter filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT o FROM Order o
                    JOIN SupervisorEmployee se on  o.agentId = se.employeeId
                WHERE se.supervisorId = :supervisorId
                    AND o.orderDate BETWEEN :from AND :to
                """);
        if (filter.getAgentIds() != null && !filter.getAgentIds().isEmpty()) {
            sql.append(" AND o.agentId IN (:agentIds) ");
        }
        if (filter.getPlanRouteId() != null) {
            sql.append(" AND o.planRouteId = :planRouteId");
        }
        if (filter.getStatus() != null) {
            sql.append(" AND o.status = :status");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" AND o.customerId = :customerId");
        }
        if (filter.getDeliveryFromDate() != null) {
            sql.append(" AND o.deliveryDate = :deliveryFromDate");
        }
        if (filter.getDeliveryToDate() != null) {
            sql.append(" AND o.deliveryDate = :deliveryToDate");
        }
        String countSql = sql.toString().replaceFirst("SELECT o", "SELECT count(o.id)");


        TypedQuery<Order> query = entityManager.createQuery(sql.toString(), Order.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        query.setParameter("supervisorId", filter.getSupervisorId());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);
        countQuery.setParameter("supervisorId", filter.getSupervisorId());
        countQuery.setParameter("from", filter.getFrom());
        countQuery.setParameter("to", filter.getTo());

        if (filter.getAgentIds() != null && !filter.getAgentIds().isEmpty()) {
            query.setParameter("agentIds", filter.getAgentIds());
            countQuery.setParameter("agentIds", filter.getAgentIds());
        }
        if (filter.getPlanRouteId() != null) {
            query.setParameter("planRouteId", filter.getPlanRouteId());
            countQuery.setParameter("planRouteId", filter.getPlanRouteId());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", OrderStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", OrderStatus.valueOf(filter.getStatus()));
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getDeliveryFromDate() != null) {
            query.setParameter("deliveryFromDate", filter.getDeliveryFromDate());
            countQuery.setParameter("deliveryFromDate", filter.getDeliveryFromDate());
        }
        if (filter.getDeliveryToDate() != null) {
            query.setParameter("deliveryToDate", filter.getDeliveryToDate());
            countQuery.setParameter("deliveryToDate", filter.getDeliveryToDate());
        }

        ResultList<Order> resultList = new ResultList<>();
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getReportBaseSql(OrderFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from order_product op ");
        sql.append(" inner join orders o on o.id = op.order_id ");
        sql.append(" left join users a on o.agent_id = a.id ");
        sql.append(" inner join company c on o.company_id=c.id ");
        sql.append(" inner join product p on p.id = op.product_id ");
        sql.append(" where op.deleted =false and p.deleted =false ");
        sql.append(" and p.id = ").append(filter.getProductId());
        sql.append(" and o.warehouse_id=").append(filter.getWarehouseId());
        if (filter.getAgentId() != null) {
            sql.append(" and o.agent_id=").append(filter.getAgentId());
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.order_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.order_date<=:toDate ");
        }
        return sql;
    }

    @Override
    public List<Order> getListForTransfer(OrderFilter filter) {
        filter.setCompanyId(getCompanyId(filter, true));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
        StringBuilder sql = new StringBuilder();
        if (!CollectionUtils.isEmpty(filter.getPlanRouteExpeditors())) {
            for (PlanRouteExpeditorDTO planRouteExpeditor : filter.getPlanRouteExpeditors()) {
                sql.append(" select o.* ");
                filter.setPlanRouteId(planRouteExpeditor.getPlanRouteId());
                filter.setExpeditors(planRouteExpeditor.getExpeditors());
                sql.append(getBaseSqlForTransfer(filter));
                if (!filter.getPlanRouteExpeditors().getLast().equals(planRouteExpeditor)) {
                    sql.append(" union all ");
                }
            }
        } else {
            sql.append(" select o.* ");
            sql.append(getBaseSqlForTransfer(filter));
        }
        Query query = entityManager
                .createNativeQuery(sql.toString(), Order.class);
        return query.getResultList();
    }


    @Override
    public ResultList<OrderExpeditorReportDTO> getOrderExpeditorReport(OrderFilter filter) {
        StringBuilder withQuery = new StringBuilder("""
                WITH aggregated AS (
                SELECT
                    o.branch_id,
                    o.warehouse_id,
                    o.expeditor_id,
                    o.plan_route_id,
                    COUNT(o.id)             AS orderCount,
                    COUNT(o.stock_out_id)   AS stockOutCount,
                    COUNT(CASE WHEN o.status = 'RETURNED' THEN 1 END) AS returnedOrderCount,
                    SUM(o.total_price)      AS totalOrderPrice
                FROM orders o WHERE o.company_id = :companyId
                """);

        if (filter.getExpeditorId() != null) {
            withQuery.append(" AND o.expeditor_id = :expeditorId");
        }
        if (filter.getPlanRouteId() != null) {
            withQuery.append(" AND o.plan_route_id = :planRouteId");
        }
        if (filter.getFrom() != null) {
            withQuery.append(" AND o.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            withQuery.append(" AND o.created_date <= :toDate");
        }


        withQuery.append("""
                 GROUP BY o.branch_id, o.warehouse_id, o.expeditor_id, o.plan_route_id)
                """);

        String sql = withQuery + """
                SELECT
                    a.branch_id         AS branchId,
                    b.name              AS branchName,
                    a.warehouse_id      AS warehouseId,
                    w.name              AS warehouseName,
                    a.expeditor_id      AS expeditorId,
                    e.first_name || e.last_name        AS expeditorName,
                    a.plan_route_id     AS planRouteId,
                    pr.name             AS planRouteName,
                    a.orderCount        AS orderCount,
                    a.stockOutCount     AS stockOutCount,
                    a.returnedOrderCount     AS returnedOrderCount,
                    a.totalOrderPrice   AS totalOrderPrice
                FROM aggregated a
                         LEFT JOIN branch b ON a.branch_id = b.id
                         LEFT JOIN warehouse w ON a.warehouse_id = w.id
                         LEFT JOIN users e ON a.expeditor_id = e.id
                         LEFT JOIN plan_route pr ON a.plan_route_id = pr.id
                """;

        String countSQL = withQuery + """
                SELECT COUNT(*) FROM aggregated
                """;
        ResultList<OrderExpeditorReportDTO> resultList = new ResultList<>();
        Query query = entityManager.createNativeQuery(sql, OrderExpeditorReportDTO.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        Query countQuery = entityManager.createNativeQuery(countSQL, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getExpeditorId() != null) {
            query.setParameter("expeditorId", filter.getExpeditorId());
            countQuery.setParameter("expeditorId", filter.getExpeditorId());
        }
        if (filter.getPlanRouteId() != null) {
            query.setParameter("planRouteId", filter.getPlanRouteId());
            countQuery.setParameter("planRouteId", filter.getPlanRouteId());
        }

        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }

        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public OrderExpeditorTotalQtyReportDTO getOrderExpeditorTotalQtyReport(OrderFilter filter) {
        StringBuilder sql = new StringBuilder("""
                WITH aggregated AS (SELECT o.branch_id,
                           o.warehouse_id,
                           o.expeditor_id,
                           o.plan_route_id,
                           COUNT(o.id)                                        AS orderCount,
                           COUNT(o.stock_out_id)                              AS stockOutCount,
                           COUNT(CASE WHEN o.status = 'COMPLETED' THEN 1 END) AS returnedOrderCount,
                           COUNT(CASE WHEN o.status = 'RETURNED' THEN 1 END)  AS completedOrderCount,
                           SUM(o.total_price)                                 AS totalOrderPrice
                    FROM orders o WHERE o.company_id = :companyId
                """);

        if (filter.getExpeditorId() != null) {
            sql.append(" AND o.expeditor_id = :expeditorId");
        }
        if (filter.getPlanRouteId() != null) {
            sql.append(" AND o.plan_route_id = :planRouteId");
        }
        if (filter.getFrom() != null) {
            sql.append(" AND o.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" AND o.created_date <= :toDate");
        }

        sql.append("""
                 GROUP BY o.branch_id, o.warehouse_id, o.expeditor_id, o.plan_route_id)
                """);

        sql.append("""
                SELECT COUNT(distinct expeditor_id) AS totalExpeditorCount,
                       SUM(completedOrderCount)     AS completedOrderCount,
                       SUM(orderCount)              AS orderCount,
                       SUM(stockOutCount)           AS stockOutCount,
                       SUM(returnedOrderCount)      AS returnedOrderCount,
                       SUM(totalOrderPrice)         AS totalOrderPrice
                FROM aggregated
                """);


        Query query = entityManager.createNativeQuery(sql.toString(), OrderExpeditorTotalQtyReportDTO.class);
        query.setParameter("companyId", filter.getCompanyId());

        if (filter.getExpeditorId() != null) {
            query.setParameter("expeditorId", filter.getExpeditorId());
        }
        if (filter.getPlanRouteId() != null) {
            query.setParameter("planRouteId", filter.getPlanRouteId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        return (OrderExpeditorTotalQtyReportDTO) query.getSingleResult();
    }

    @Override
    public TerminalOrderStatisticsDTO getOrderStatisticsForTerminal(OrderFilter filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(o.id) FILTER (WHERE o.status = 'COMPLETED')    AS countCompletedOrders,
                       COUNT(o.id) FILTER (WHERE o.status != 'DELETED')     AS countAllOrders
                FROM orders o WHERE 1=1
                """);
        if (filter.getExpeditorId() != null) {
            sql.append(" AND o.expeditor_id = :expeditorId");
        }
        if(filter.getCompanyId() != null) {
            sql.append(" AND o.company_id = :companyId");
        }
        if(filter.getFrom() != null) {
            sql.append(" AND o.created_date >= :fromDate");
        }
        if(filter.getTo() != null) {
            sql.append(" AND o.created_date <= :toDate");
        }
        Query nativeQuery = entityManager.createNativeQuery(String.valueOf(sql), TerminalOrderStatisticsDTO.class);
        if(filter.getCompanyId() != null) {
            nativeQuery.setParameter("companyId", filter.getCompanyId());
        }
        if(filter.getExpeditorId() != null) {
            nativeQuery.setParameter("expeditorId", filter.getExpeditorId());
        }
        if(filter.getFrom() != null) {
            nativeQuery.setParameter("fromDate", filter.getFrom());
        }
        if(filter.getTo() != null) {
            nativeQuery.setParameter("toDate", filter.getTo());
        }
        return (TerminalOrderStatisticsDTO) nativeQuery.getSingleResult();
    }


    private StringBuilder getBaseSqlForTransfer(OrderFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from orders o ");
        sql.append(" inner join company c on o.customer_id=c.id ");
        sql.append(" inner join company s on o.supplier_id=s.id ");
        if (filter.getCustomerBranchId() != null || filter.getPlanRouteId() != null) {
            sql.append(" inner join plan_route_customer_branch cb on o.plan_route_customer_branch_id=cb.id");
        }
        if (filter.getPlanRouteId() != null) {
            sql.append(" inner join plan_route pr on o.plan_route_id=pr.id ");
        }
        sql.append(" where o.company_id =").append(filter.getCompanyId());
        if (!filter.isFetchDeleted()) {
            sql.append(" and o.deleted =false");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and o.branch_id=").append(filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and o.warehouse_id=").append(filter.getWarehouseId());
        }
        if (filter.getCustomerBranchId() != null) {
            sql.append(" and cb.customer_branch_id=").append(filter.getCustomerBranchId());
        }
        if (filter.getPlanRouteId() != null) {
            sql.append(" and pr.id=").append(filter.getPlanRouteId());
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and o.customer_id=").append(filter.getCustomerId());
        }
        if (filter.getSupplierId() != null) {
            sql.append(" and o.supplier_id=").append(filter.getSupplierId());
        }
        if (!CollectionUtils.isEmpty(filter.getExpeditors())) {
            sql.append(" and o.expeditor_id in(").append(filter.getExpeditors().stream().map(Object::toString).collect(Collectors.joining(","))).append(")");
        }
        if (StringUtils.hasText(filter.getStatus())) {
            sql.append(" and o.status='").append(filter.getStatus()).append("'");
        }
        if (StringUtils.hasText(filter.getExcludeStatus())) {
            sql.append(" and o.status!='").append(filter.getExcludeStatus()).append("'");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.order_date>='").append(filter.getFrom()).append("'");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.order_date<=").append(filter.getTo()).append("'");
        }
        if (StringUtils.hasText(filter.getOrderType())) {
            sql.append(" and o.order_type='").append(filter.getOrderType()).append("'");
        }
        if (StringUtils.hasText(filter.getSource())) {
            sql.append(" and o.order_source=").append(filter.getSource());
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and o.creator_id=").append(filter.getCreatorId());
        }
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(o.orderNumber) like '").append(filter.getSearchValue()).append("'");
            sql.append(" lower(s.tin) like '").append(filter.getSearchValue()).append("'");
            sql.append(" lower(s.name) like '").append(filter.getSearchValue()).append("'");
            sql.append(" lower(c.tin) like '").append(filter.getSearchValue()).append("'");
            sql.append(" lower(c.name) like '").append(filter.getSearchValue()).append("'");
            sql.append(") ");
        }
        return sql;
    }
}
