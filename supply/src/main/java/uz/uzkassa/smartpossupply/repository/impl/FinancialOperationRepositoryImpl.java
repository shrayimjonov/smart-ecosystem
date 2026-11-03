package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.FinancialOperation;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.customer.AccountReceivableMapDTO;
import uz.uzkassa.smartpossupply.dto.financialoperation.FinancialOperationStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.FinancialOperationType;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.filters.FinancialOperationFilter;
import uz.uzkassa.smartpossupply.filters.ReconciliationActFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.repository.FinancialOperationRepositoryCustom;

import java.math.BigDecimal;
import java.util.List;

public class FinancialOperationRepositoryImpl
    extends BaseFilterableRepositoryImpl<FinancialOperation, FinancialOperationFilter>
    implements FinancialOperationRepositoryCustom {

    @Override
    protected Class<FinancialOperation> getEntityClass() {
        return FinancialOperation.class;
    }

    @Override
    protected String getEntityAlias() {
        return "fo";
    }

    @Override
    public void applyFilter(FinancialOperationFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
    }

    @Override
    public String getBaseQuery(FinancialOperationFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM FinancialOperation fo ");

        // Add joins
        if (includeFetch) {
            sql.append("INNER JOIN FETCH fo.customer c ")
                .append("LEFT JOIN FETCH fo.customerBranch cb ")
                .append("LEFT JOIN FETCH fo.stockOut s ")
                .append("LEFT JOIN FETCH fo.creator creator ");
        } else {
            sql.append("INNER JOIN fo.customer c ")
                .append("LEFT JOIN fo.customerBranch cb ")
                .append("LEFT JOIN fo.stockOut s ")
                .append("LEFT JOIN fo.creator creator ");
        }

        // Base conditions
        sql.append("WHERE fo.deleted = false ")
            .append("AND fo.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getCustomerId() != null, "AND c.id = :customerId ");
        appendCondition(sql, filter.getCustomerBranchId() != null, "AND cb.id = :customerBranchId ");
        appendCondition(sql, filter.getBranchId() != null, "AND s.branchId = :branchId ");
        appendCondition(sql, StringUtils.hasText(filter.getPaymentType()), "AND fo.paymentType = :paymentType ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND fo.status = :status ");
        appendCondition(sql, filter.getOperationType() != null, "AND fo.operationType = :operationType ");
        appendCondition(sql, filter.getFrom() != null, "AND fo.createdDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND fo.createdDate <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(fo.operationNumber)").append(filter.getSearchKey())
                .append(" OR LOWER(c.tin)").append(filter.getSearchKey())
                .append(" OR LOWER(s.stockNumber)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, FinancialOperationFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "customerId", filter.getCustomerId());
        setParameterIfNotNull(query, "customerBranchId", filter.getCustomerBranchId());
        setParameterIfNotNull(query, "branchId", filter.getBranchId());

        if (StringUtils.hasText(filter.getOperationType())) {
            query.setParameter("operationType", FinancialOperationType.valueOf(filter.getOperationType()));
        }

        if (StringUtils.hasText(filter.getPaymentType())) {
            query.setParameter("paymentType", PaymentType.valueOf(filter.getPaymentType()));
        }

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public FinancialOperationStatisticsDTO getStatistics(FinancialOperationFilter filter) {
        applyFilter(filter);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("COUNT(DISTINCT fo.id) AS totalNumberOfOperations, ");
        sql.append("SUM(fo.amount) AS sumAllOperations, ");
        sql.append("(CASE WHEN fo.paymentType = 'CASH' THEN SUM(fo.amount) ELSE 0 END) AS sumCashOperations, ");
        sql.append("(CASE WHEN fo.paymentType = 'CARD' THEN SUM(fo.amount) ELSE 0 END) AS sumCardOperations, ");
        sql.append("(CASE WHEN fo.paymentType = 'TRANSFER' THEN SUM(fo.amount) ELSE 0 END) AS sumTransferOperations, ");
        sql.append("(CASE WHEN fo.operationType = 'DEBIT' THEN SUM(fo.amount) ELSE 0 END) AS sumDebitOperations, ");
        sql.append("(CASE WHEN fo.operationType = 'CREDIT' THEN SUM(fo.amount) ELSE 0 END) AS sumCreditOperations ");
        sql.append(getBaseQuery(filter, false));
        sql.append(" GROUP BY fo.paymentType, fo.operationType");

        TypedQuery<Object[]> query = entityManager.createQuery(sql.toString(), Object[].class);
        setParameters(query, filter);

        List<Object[]> result = query.getResultList();

        Long totalNumberOfOperations = 0L;
        BigDecimal sumAllOperations = BigDecimal.ZERO;
        BigDecimal sumCashOperations = BigDecimal.ZERO;
        BigDecimal sumCardOperations = BigDecimal.ZERO;
        BigDecimal sumTransferOperations = BigDecimal.ZERO;
        BigDecimal sumCreditOperations = BigDecimal.ZERO;
        BigDecimal sumDebitOperations = BigDecimal.ZERO;

        for (Object[] row : result) {
            totalNumberOfOperations += (Long) row[0];
            sumAllOperations = sumAllOperations.add((BigDecimal) row[1]);
            sumCashOperations = sumCashOperations.add((BigDecimal) row[2]);
            sumCardOperations = sumCardOperations.add((BigDecimal) row[3]);
            sumTransferOperations = sumTransferOperations.add((BigDecimal) row[4]);
            sumDebitOperations = sumDebitOperations.add((BigDecimal) row[5]);
            sumCreditOperations = sumCreditOperations.add((BigDecimal) row[6]);
        }

        return new FinancialOperationStatisticsDTO(
            totalNumberOfOperations,
            sumAllOperations,
            sumCashOperations,
            sumCardOperations,
            sumTransferOperations,
            sumDebitOperations,
            sumCreditOperations
        );
    }

    @Override
    public ResultList<AccountReceivableMapDTO> getAccountReceivableReport(StockFilter filter) {
        ResultList<AccountReceivableMapDTO> resultList = new ResultList<>();
        filter.setCompanyId(getCompanyId(filter));
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append("fo.id as operationId,");
        sql.append("fo.operation_number as operationNumber,");
        sql.append("fo.operation_type as operationType,");
        sql.append("s.id as stockOutId,");
        sql.append("s.stock_number as stockOutNumber,");
        sql.append("fo.created_date as operationDate,");
        sql.append("fo.amount as totalPrice,");
        sql.append("customer.id as customerId,");
        sql.append("customer.name as customerName,");
        sql.append("customer.tin as customerTin,");
        sql.append("customer.brand as customerBrand,");
        sql.append("pr.id as planRouteId,");
        sql.append("pr.plan_route_number as planRouteNumber,");
        sql.append("a.id as agentId,");
        sql.append("a.first_name as agentFirstName,");
        sql.append("a.last_name as agentLastName,");
        sql.append("e.id as expeditorId,");
        sql.append("e.first_name as expeditorFirstName,");
        sql.append("e.last_name as expeditorLastName,");
        sql.append("f.id as facturaId,");
        sql.append("f.factura_no as facturaNo,");
        sql.append("f.status as facturaStatus,");
        sql.append("f.factura_date as facturaDate");
        StringBuilder baseSql = new StringBuilder();
        baseSql.append(" from financial_operation fo ");
        baseSql.append(" inner join company customer on fo.customer_id = customer.id ");
        baseSql.append(" left join stock s on s.id = fo.stock_out_id ");
        baseSql.append(" left join factura f on f.id = s.factura_id ");
        baseSql.append(" left join orders o on o.stock_out_id = s.id ");
        baseSql.append(" left join users a on a.id = o.agent_id ");
        baseSql.append(" left join users e on e.id = o.expeditor_id ");
        baseSql.append(" left join plan_route_customer_branch prcb on o.id = prcb.order_id ");
        baseSql.append(" left join plan_route pr on o.plan_route_id = pr.id ");
        baseSql.append(" where fo.deleted =false ");
        baseSql.append(" and fo.company_id =:companyId ");
        if (!CollectionUtils.isEmpty(filter.getPlanRouteIds())) {
            baseSql.append(" and pr.id in(:planRouteIds) ");
        }
        if (!CollectionUtils.isEmpty(filter.getPlanRouteTemplateIds())) {
            baseSql.append(" and pr.template_id in(:planRouteTemplateIds) ");
        }
        if (!CollectionUtils.isEmpty(filter.getCustomerIds())) {
            baseSql.append(" and customer.id in(:customerIds) ");
        }
        if (!CollectionUtils.isEmpty(filter.getExpeditorIds())) {
            baseSql.append(" and o.expeditor_id in(:expeditorIds) ");
        }
        if (filter.getFrom() != null) {
            baseSql.append(" and fo.created_date>=:fromDate");
        }
        if (filter.getTo() != null) {
            baseSql.append(" and fo.created_date<=:toDate");
        }
        if (filter.isNotEmpty()) {
            baseSql.append(" and (");
            baseSql.append(" or lower(customer.tin)").append(filter.getSearchKey());
            baseSql.append(") ");
        }
        String countSql = "select count(fo.id)".concat(baseSql.toString());
        sql.append(baseSql).append(" order by customer.id desc");

        Query query = entityManager.createNativeQuery(sql.toString(), "AccountReceivableReportMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        Query countQuery = entityManager.createNativeQuery(countSql, Long.class);
        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (!CollectionUtils.isEmpty(filter.getPlanRouteIds())) {
            query.setParameter("planRouteIds", filter.getPlanRouteIds());
            countQuery.setParameter("planRouteIds", filter.getPlanRouteIds());
        }

        if (!CollectionUtils.isEmpty(filter.getPlanRouteTemplateIds())) {
            query.setParameter("planRouteTemplateIds", filter.getPlanRouteTemplateIds());
            countQuery.setParameter("planRouteTemplateIds", filter.getPlanRouteTemplateIds());
        }

        if (!CollectionUtils.isEmpty(filter.getCustomerIds())) {
            query.setParameter("customerIds", filter.getCustomerIds());
            countQuery.setParameter("customerIds", filter.getCustomerIds());
        }

        if (!CollectionUtils.isEmpty(filter.getExpeditorIds())) {
            query.setParameter("expeditorIds", filter.getExpeditorIds());
            countQuery.setParameter("expeditorIds", filter.getExpeditorIds());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }

        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }

        return resultList;
    }

    @Override
    public ResultList<FinancialOperation> getResultList(ReconciliationActFilter filter) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("""
                SELECT fo FROM FinancialOperation fo
                    LEFT JOIN FETCH fo.customerBranch
                WHERE fo.deleted = false AND fo.status = ACTIVE""");

        StringBuilder countSQL = new StringBuilder("""
                SELECT COUNT(fo.id) FROM FinancialOperation fo WHERE fo.deleted = false AND fo.status = ACTIVE
                """);

        if (filter.getCompanyId() != null) {
            jpql.append(" AND fo.companyId = :companyId");
            countSQL.append(" AND fo.companyId = :companyId");
        }
        if (filter.getCustomerId() != null) {
            jpql.append(" AND fo.customerId = :customerId");
            countSQL.append(" AND fo.customerId = :customerId");
        }
        if (filter.getCustomerBranchId() != null) {
            jpql.append(" AND fo.customerBranchId = :customerBranchId");
            countSQL.append(" AND fo.customerBranchId = :customerBranchId");
        }
        if (filter.getCurrencyType() != null) {
            jpql.append(" AND fo.currencyType = :currencyType");
            countSQL.append(" AND fo.currencyType = :currencyType");
        }
        if (filter.getFrom() != null) {
            jpql.append(" AND fo.createdDate >= :fromDate");
            countSQL.append(" AND fo.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            jpql.append(" AND fo.createdDate <= :toDate");
            countSQL.append(" AND fo.createdDate <= :toDate");
        }
        if (filter.getStockOutId() != null) {
            jpql.append(" AND fo.stockOutId = :stockOutId");
            countSQL.append(" AND fo.stockOutId = :stockOutId");
        }

        jpql.append(" order by fo.").append(filter.getOrderBy());
        jpql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<FinancialOperation> query = entityManager.createQuery(jpql.toString(), FinancialOperation.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSQL.toString(), Long.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
            countQuery.setParameter("customerBranchId", filter.getCustomerBranchId());
        }
        if (filter.getCurrencyType() != null) {
            query.setParameter("currencyType", filter.getCurrencyType());
            countQuery.setParameter("currencyType", filter.getCurrencyType());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.getStockOutId() != null) {
            query.setParameter("stockOutId", filter.getStockOutId());
            countQuery.setParameter("stockOutId", filter.getStockOutId());
        }
        ResultList<FinancialOperation> resultList = new ResultList<>();
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public BigDecimal calculationPaymentAmount(ReconciliationActFilter filter) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("""
                SELECT COALESCE(SUM(fo.amount), 0) FROM FinancialOperation fo
                WHERE fo.deleted = false AND fo.operationType = DEBIT AND fo.status=ACTIVE""");

        if (filter.getCompanyId() != null) {
            jpql.append(" AND fo.companyId = :companyId");
        }
        if (filter.getCustomerId() != null) {
            jpql.append(" AND fo.customerId = :customerId");
        }
        if (filter.getCustomerBranchId() != null) {
            jpql.append(" AND fo.customerBranchId = :customerBranchId");
        }
        if (filter.getCurrencyType() != null) {
            jpql.append(" AND fo.currencyType = :currencyType");
        }
        if (filter.getFrom() != null) {
            jpql.append(" AND fo.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            jpql.append(" AND fo.createdDate <= :toDate");
        }
        if (filter.getStockOutId() != null) {
            jpql.append(" AND fo.stockOutId = :stockOutId");
        }

        TypedQuery<BigDecimal> query = entityManager.createQuery(jpql.toString(), BigDecimal.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
        }
        if (filter.getCurrencyType() != null) {
            query.setParameter("currencyType", filter.getCurrencyType());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        if (filter.getStockOutId() != null) {
            query.setParameter("stockOutId", filter.getStockOutId());
        }

        return query.getSingleResult();
    }

    @Override
    public Object[] calculationDebitAndCredit(ReconciliationActFilter filter) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("""
                SELECT  COALESCE(SUM(CASE WHEN fo.operationType = DEBIT THEN fo.amount ELSE 0 END), 0),
                        COALESCE(SUM(CASE WHEN fo.operationType = CREDIT THEN fo.amount ELSE 0 END), 0)
                FROM FinancialOperation fo
                        WHERE fo.deleted = false and fo.status = ACTIVE""");

        if (filter.getCompanyId() != null) {
            jpql.append(" AND fo.companyId = :companyId");
        }
        if (filter.getCustomerId() != null) {
            jpql.append(" AND fo.customerId = :customerId");
        }
        if (filter.getCustomerBranchId() != null) {
            jpql.append(" AND fo.customerBranchId = :customerBranchId");
        }
        if (filter.getCurrencyType() != null) {
            jpql.append(" AND fo.currencyType = :currencyType");
        }
        if (filter.getFrom() != null) {
            jpql.append(" AND fo.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            jpql.append(" AND fo.createdDate <= :toDate");
        }
        if (filter.getStockOutId() != null) {
            jpql.append(" AND fo.stockOutId = :stockOutId");
        }

        TypedQuery<Object[]> query = entityManager.createQuery(jpql.toString(), Object[].class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
        }
        if (filter.getCurrencyType() != null) {
            query.setParameter("currencyType", filter.getCurrencyType());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        if (filter.getStockOutId() != null) {
            query.setParameter("stockOutId", filter.getStockOutId());
        }

        return query.getSingleResult();
    }
}
