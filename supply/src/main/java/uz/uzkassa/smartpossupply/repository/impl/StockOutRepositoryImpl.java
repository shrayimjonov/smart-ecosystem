package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalStockOutStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.reconciliationact.CalculationDTO;
import uz.uzkassa.smartpossupply.dto.stock.ActiveCustomerBaseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CommonStockStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.ActiveCustomerFilter;
import uz.uzkassa.smartpossupply.filters.ReconciliationActFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.repository.StockOutRepositoryCustom;

import java.util.List;

@SuppressWarnings("unused")
public class StockOutRepositoryImpl
    extends BaseFilterableRepositoryImpl<Stock, StockFilter>
    implements StockOutRepositoryCustom {

    @Override
    protected Class<Stock> getEntityClass() {
        return Stock.class;
    }

    @Override
    protected String getEntityAlias() {
        return "s";
    }

    @Override
    public void applyFilter(StockFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
    }

    @Override
    public String getBaseQuery(StockFilter filter, boolean includeFetch) {
        boolean advancedSearch = getAdvanceSearch(filter);
        StringBuilder sql = new StringBuilder(" FROM Stock s ");

        // Add base joins
        if (includeFetch) {
            sql.append("JOIN FETCH s.supplier supp ")
                .append("JOIN FETCH s.customer cus ")
                .append("JOIN FETCH s.company c ")
                .append("JOIN FETCH s.branch b ")
                .append("LEFT JOIN FETCH s.warehouse w ")
                .append("LEFT JOIN FETCH s.stockTransfer st ")
                .append("LEFT JOIN FETCH s.oldStock os ")
                .append("LEFT JOIN FETCH s.factura f ")
                .append("LEFT JOIN FETCH s.acceptanceTransferAct act ")
                .append("LEFT JOIN FETCH s.aic aic ")
                .append("LEFT JOIN FETCH s.party party ")
                .append("LEFT JOIN FETCH s.order o ")
                .append("LEFT JOIN FETCH s.vehicle v ");
        } else {
            sql.append("JOIN s.supplier supp ")
                .append("JOIN s.customer cus ")
                .append("JOIN s.company c ")
                .append("JOIN s.branch b ")
                .append("LEFT JOIN s.warehouse w ")
                .append("LEFT JOIN s.stockTransfer st ")
                .append("LEFT JOIN s.oldStock os ")
                .append("LEFT JOIN s.factura f ")
                .append("LEFT JOIN s.acceptanceTransferAct act ")
                .append("LEFT JOIN s.aic aic ")
                .append("LEFT JOIN s.party party ")
                .append("LEFT JOIN s.order o ")
                .append("LEFT JOIN s.vehicle v ");
        }

        // Conditional joins based on filters
        if (filter.getPlanRouteId() != null) {
            sql.append(includeFetch ? "JOIN FETCH s.agent a " : "JOIN s.agent a ");
        }

        // Advanced search joins (never use FETCH for these)
        if (advancedSearch) {
            sql.append("JOIN s.products sp ");
            if (StringUtils.hasText(filter.getAggregationCode())) {
                sql.append("JOIN sp.stockProductAggregations spa ");
            } else if (StringUtils.hasText(filter.getMarkCode())) {
                sql.append("JOIN sp.stockProductMarks spm ");
            } else if (StringUtils.hasText(filter.getAggregationBlockCode())) {
                sql.append("JOIN sp.stockProductAggregationBlocks spt ");
            } else if (StringUtils.hasText(filter.getBarcode()) || StringUtils.hasText(filter.getVatBarcode())) {
                sql.append("JOIN sp.product p ");
            }
        }

        // Base condition - always OUT type for this repository
        sql.append("WHERE s.transferType = :transferType ");

        // Deleted flag handling
        if (!filter.isFetchDeleted()) {
            sql.append("AND s.deleted = false ");
        }

        // Dynamic conditions
        appendCondition(sql, filter.getCompanyId() != null, "AND c.id = :companyId ");
        appendCondition(sql, filter.getCreatorId() != null, "AND s.creatorId = :creatorId ");
        appendCondition(sql, filter.getSupplierId() != null, "AND supp.id = :supplierId ");
        appendCondition(sql, filter.getCustomerId() != null, "AND cus.id = :customerId ");
        appendCondition(sql, filter.getBranchId() != null, "AND b.id = :branchId ");
        appendCondition(sql, filter.getWarehouseId() != null, "AND w.id = :warehouseId ");
        appendCondition(sql, filter.getPlanRouteId() != null, "AND a.planRouteId = :planRouteId ");
        appendCondition(sql, filter.getCashierId() != null, "AND s.cashierId = :cashierId ");
        appendCondition(sql, filter.getStatus() != null, "AND s.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND s.transferDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND s.transferDate <= :toDate ");

        // Boolean condition for hasFactura
        if (filter.getHasFactura() != null) {
            sql.append(filter.getHasFactura() ?
                "AND s.facturaId IS NOT NULL " :
                "AND s.facturaId IS NULL ");
        }

        // Advanced search conditions
        if (advancedSearch) {
            sql.append("AND sp.deleted = false ");

            if (StringUtils.hasText(filter.getAggregationCode())) {
                sql.append("AND spa.code = :code ");
            } else if (StringUtils.hasText(filter.getMarkCode())) {
                sql.append("AND spm.code = :code ");
            } else if (StringUtils.hasText(filter.getAggregationBlockCode())) {
                sql.append("AND spt.code = :code ");
            } else if (StringUtils.hasText(filter.getBarcode())) {
                sql.append("AND p.barcode = :barcode ");
            } else if (StringUtils.hasText(filter.getVatBarcode())) {
                sql.append("AND p.vatBarcode = :vatBarcode ");
            } else if (StringUtils.hasText(filter.getAcceptanceTransferActNumber())) {
                sql.append("AND act.actNo LIKE :acceptanceTransferActNumber ");
            } else if (StringUtils.hasText(filter.getReceiptUid())) {
                sql.append("AND s.receiptUid LIKE :receiptUid ");
            } else if (StringUtils.hasText(filter.getFactura())) {
                sql.append("AND (f.facturaId LIKE :factura OR f.facturaNo LIKE :factura) ");
            }
        } else if (filter.isNotEmpty()) {
            // Standard search
            sql.append("AND (")
                .append("LOWER(s.stockNumber)").append(filter.getSearchKey())
                .append(" OR LOWER(supp.name)").append(filter.getSearchKey())
                .append(" OR LOWER(supp.tin)").append(filter.getSearchKey())
                .append(" OR LOWER(cus.name)").append(filter.getSearchKey())
                .append(" OR LOWER(cus.tin)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, StockFilter filter) {
        // Required parameter - always OUT for this repository
        query.setParameter("transferType", TransferType.OUT);

        // Optional parameters
        setParameterIfNotNull(query, "companyId", filter.getCompanyId());
        setParameterIfNotNull(query, "creatorId", filter.getCreatorId());
        setParameterIfNotNull(query, "supplierId", filter.getSupplierId());
        setParameterIfNotNull(query, "customerId", filter.getCustomerId());
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "warehouseId", filter.getWarehouseId());
        setParameterIfNotNull(query, "planRouteId", filter.getPlanRouteId());
        setParameterIfNotNull(query, "cashierId", filter.getCashierId());

        if (org.springframework.util.StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", StockStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        // Advanced search parameters (mutually exclusive)
        if (getAdvanceSearch(filter)) {
            if (StringUtils.hasText(filter.getAggregationCode())) {
                query.setParameter("code", filter.getAggregationCode());
            } else if (StringUtils.hasText(filter.getMarkCode())) {
                query.setParameter("code", filter.getMarkCode());
            } else if (StringUtils.hasText(filter.getAggregationBlockCode())) {
                query.setParameter("code", filter.getAggregationBlockCode());
            } else if (StringUtils.hasText(filter.getBarcode())) {
                query.setParameter("barcode", filter.getBarcode());
            } else if (StringUtils.hasText(filter.getVatBarcode())) {
                query.setParameter("vatBarcode", filter.getVatBarcode());
            } else if (StringUtils.hasText(filter.getAcceptanceTransferActNumber())) {
                query.setParameter("acceptanceTransferActNumber", filter.getAcceptanceTransferActNumber());
            } else if (StringUtils.hasText(filter.getReceiptUid())) {
                query.setParameter("receiptUid", filter.getReceiptUid());
            } else if (StringUtils.hasText(filter.getFactura())) {
                query.setParameter("factura", filter.getFactura());
            }
        } else if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public CommonStockStatisticsDTO getStatistic(StockFilter filter) {
        applyFilter(filter);

        String baseSql = getBaseQuery(filter, false);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT 'CHECKING_TOTAL' AS dataType, COUNT(DISTINCT s.id) FILTER(WHERE s.status='CHECKING') AS total ");
        sql.append(baseSql);
        sql.append(" UNION ALL ");

        sql.append("SELECT 'CHECKING_ERROR_TOTAL' AS dataType, COUNT(DISTINCT s.id) FILTER(WHERE s.status='CHECKING_ERROR') AS total ");
        sql.append(baseSql);
        sql.append(" UNION ALL ");

        sql.append("SELECT 'DRAFT_TOTAL' AS dataType, COUNT(DISTINCT s.id) FILTER(WHERE s.status='DRAFT') AS total ");
        sql.append(baseSql);
        sql.append(" UNION ALL ");

        sql.append("SELECT 'CANCELLED_TOTAL' AS dataType, COUNT(DISTINCT s.id) FILTER(WHERE s.status='CANCELLED') AS total ");
        sql.append(baseSql);
        sql.append(" UNION ALL ");

        sql.append("SELECT 'COMPLETED_TOTAL' AS dataType, COUNT(DISTINCT s.id) FILTER(WHERE s.status='COMPLETED') AS total ");
        sql.append(baseSql);

        Query query = entityManager.createQuery(sql.toString());
        setParameters((TypedQuery<?>) query, filter);

        CommonStockStatisticsDTO result = new CommonStockStatisticsDTO();
        List<Object[]> resultList = query.getResultList();

        for (Object[] row : resultList) {
            String type = row[0].toString();
            Long count = Long.parseLong(row[1].toString());

            switch (type) {
                case "CHECKING_TOTAL":
                    result.setCheckingTotal(count);
                    break;
                case "CHECKING_ERROR_TOTAL":
                    result.setCheckingErrorTotal(count);
                    break;
                case "DRAFT_TOTAL":
                    result.setDraftTotal(count);
                    break;
                case "CANCELLED_TOTAL":
                    result.setCancelledTotal(count);
                    break;
                case "COMPLETED_TOTAL":
                    result.setCompletedTotal(count);
                    break;
            }
        }

        return result;
    }

    @Override
    public CalculationDTO calculationDeliveryAmount(ReconciliationActFilter filter) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("""
                SELECT  COALESCE(SUM(s.totalPrice), 0) as deliveryAmount,
                        COALESCE(SUM(s.totalQty), 0) as totalQty
                FROM Stock s
                WHERE s.deleted = false AND s.status = COMPLETED AND s.transferType = OUT""");

        if (filter.getCompanyId() != null) {
            jpql.append(" AND s.companyId = :companyId");
        }
        if (filter.getCustomerId() != null) {
            jpql.append(" AND s.customerId = :customerId");
        }
        if (filter.getCustomerBranchId() != null) {
            jpql.append(" AND s.deliveryBranchId = :customerBranchId");
        }
        if (filter.getFrom() != null) {
            jpql.append(" AND s.createdDate >= :fromDate ");
        }
        if (filter.getTo() != null) {
            jpql.append(" AND s.createdDate <= :toDate ");
        }
        if (filter.getStockOutId() != null) {
            jpql.append(" AND s.id = :stockOutId");
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
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        if (filter.getStockOutId() != null) {
            query.setParameter("stockOutId", filter.getStockOutId());
        }
        Object[] result = query.getSingleResult();

        // Manually map to CalculationDTO
        return new CalculationDTO(
                ((Number) result[0]).doubleValue(), // deliveryAmount
                ((Number) result[1]).doubleValue()  // totalQty
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultList<ActiveCustomerBaseDTO> getActiveCustomerBase(ActiveCustomerFilter filter) {
        ResultList<ActiveCustomerBaseDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder("""
                    SELECT
                        s.customer_id           AS customerId,
                        c.name                  AS customerName,
                        array_agg(distinct r.name_ru) AS regions,
                        count(s.id)             AS countTotalStock,
                        MAX(s.created_date)     AS lastCreatedDate
                    FROM stock s JOIN company c ON s.customer_id = c.id
                                 LEFT JOIN customer_branch cb ON s.delivery_branch_id = cb.id
                                 LEFT JOIN region r on cb.region_id = r.id
                    WHERE s.created_date BETWEEN :fromDate AND :toDate AND s.company_id = :companyId AND s.status = 'COMPLETED'
                """);

        StringBuilder countSQL = new StringBuilder("""
                SELECT count(distinct s.customer_id) FROM stock s
                WHERE s.created_date BETWEEN :fromDate AND :toDate AND s.company_id = :companyId AND s.status = 'COMPLETED'
                """);


        if (filter.getBranchId() != null) {
            sql.append(" AND s.branch_id = :branchId");
            countSQL.append(" AND s.branch_id = :branchId");
        }
        if (filter.getTransferType() != null) {
            sql.append(" AND s.transfer_type = :transferType");
            countSQL.append(" AND s.transfer_type = :transferType");
        }

        sql.append(" GROUP BY s.customer_id, c.name");
        sql.append(" ORDER BY lastCreatedDate DESC");

        Query query = entityManager.createNativeQuery(sql.toString(), ActiveCustomerBaseDTO.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        query.setParameter("companyId", filter.getCompanyId());
        query.setParameter("fromDate", filter.getFrom());
        query.setParameter("toDate", filter.getTo());

        Query countQuery = entityManager.createNativeQuery(countSQL.toString());
        countQuery.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("fromDate", filter.getFrom());
        countQuery.setParameter("toDate", filter.getTo());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }

        if (filter.getTransferType() != null) {
            query.setParameter("transferType", filter.getTransferType());
            countQuery.setParameter("transferType", filter.getTransferType());
        }

        long count = ((Number) countQuery.getSingleResult()).longValue();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public TerminalStockOutStatisticsDTO getStockOutStatisticForTerminal(StockFilter filter) {
        StringBuilder sql = new StringBuilder("""
                SELECT COUNT(s.id) FILTER ( WHERE s.status = 'COMPLETED' )                          AS countCompletedStocks,
                       COUNT(s.id) FILTER ( WHERE s.status != 'DELETED' )                           AS countAllStocks,
                       COALESCE(SUM(s.total_price), 0)                                              AS totalPrice,
                       COALESCE(SUM(s.total_price) FILTER (WHERE s.payment_type = 'CASH'), 0)       AS totalPriceInCash,
                       COALESCE(SUM(s.total_price) FILTER (WHERE s.payment_type = 'TRANSFER'), 0)   AS totalPriceInTransfer,
                       COUNT(distinct sp.product_id) FILTER ( WHERE s.transfer_type = 'OUT' )       AS countStockOutProducts
                FROM stock s
                         JOIN stock_product sp on s.id = sp.stock_id
                         JOIN orders o on s.order_id = o.id
                WHERE s.transfer_type = 'OUT'
                """);
        if (filter.getCompanyId() != null) {
            sql.append(" AND s.company_id = :companyId");
        }
        if (filter.getUserId() != null) {
            sql.append(" AND o.expeditor_id = :userId");
        }
        if (filter.getFrom() != null) {
            sql.append(" AND s.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" AND s.created_date <= :toDate");
        }
        Query nativeQuery = entityManager.createNativeQuery(sql.toString(), TerminalStockOutStatisticsDTO.class);
        if (filter.getCompanyId() != null) {
            nativeQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getUserId() != null) {
            nativeQuery.setParameter("userId", filter.getUserId());
        }
        if (filter.getFrom() != null) {
            nativeQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            nativeQuery.setParameter("toDate", filter.getTo());
        }
        return (TerminalStockOutStatisticsDTO) nativeQuery.getSingleResult();
    }

    private boolean getAdvanceSearch(StockFilter filter) {
        return StringUtils.hasText(filter.getAggregationCode()) ||
            StringUtils.hasText(filter.getMarkCode()) ||
            StringUtils.hasText(filter.getAggregationBlockCode()) ||
            StringUtils.hasText(filter.getBarcode()) ||
            StringUtils.hasText(filter.getVatBarcode()) ||
            StringUtils.hasText(filter.getAcceptanceTransferActNumber()) ||
            StringUtils.hasText(filter.getReceiptUid()) ||
            StringUtils.hasText(filter.getFactura());
    }
}
