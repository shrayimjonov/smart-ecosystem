package uz.uzkassa.smartpos.repository.clickhouse.impl.receiptdetail;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.dto.analytics.ProductStatsDTO;
import uz.uzkassa.smartpos.dto.analytics.SalesStatsDTO;
import uz.uzkassa.smartpos.dto.analytics.SalesStatsProductsHeader;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesDynamicsFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.dto.scheduling.request.ProductScheduleRequest;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.enums.TimeInterval;
import uz.uzkassa.smartpos.repository.clickhouse.ClickhouseUtils;
import uz.uzkassa.smartpos.repository.clickhouse.receiptdetail.ClickhouseReceiptDetailRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ClickhouseReceiptDetailRepositoryImpl implements ClickhouseReceiptDetailRepository {
    private final JdbcClient jdbcClient;
    @Value("${spring.clickhouse.receipt-detail}")
    private String RECEIPT_DETAIL;

    public ClickhouseReceiptDetailRepositoryImpl(@Qualifier("clickhouseJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }


    @Override
    public Page<SalesStatsDTO> getProductSalesStatsByBranch(SalesStatsFilter filter, boolean forExcel) {
        StringBuilder query = new StringBuilder(" FROM ").append(this.RECEIPT_DETAIL).append(" rd WHERE 1=1 ");
        StringBuilder countQuery = new StringBuilder("SELECT count() FROM (SELECT 1 ");
        StringBuilder selectQuery = new StringBuilder("""
                select any(productId) AS id,
                any(productName) AS name,
                any(vatBarcode) AS vatBarcode,
                productBarcode AS barcode,
                any(unitName) AS unit,
                any(categoryName) AS category,
                max(price) AS price,
                sum(qty) AS salesCount,
                sum(amount) AS salesTotal,
                sum(nds) AS nds,
                any(ndsPercent) AS ndsPercent,
                sum(discount) AS discount,
                count() AS receiptCount,
                branchId,
                any(branchName) AS branchName\s""");
        var params = new HashMap<String, Object>();
        setBaseQueryFilters(query, filter, params);
        if (filter.getProductId() != null) {
            query.append(" and rd.productId = :productId");
            params.put("productId", filter.getProductId());
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            query.append(" and (lowerUTF8(rd.productName) like :search or rd.productBarcode like :search)");
            params.put("search", "%" + filter.getSearch().toLowerCase() + "%");
        }
        if (filter.isOnlySocial()) {
            query.append(" and rd.ndsPercent = 0");
        }
        if (CollectionUtils.isNotEmpty(filter.getCategoryIds())) {
            query.append(" and categoryId in (:categoryIds)");
            params.put("categoryIds", filter.getCategoryIds());
        }
        query.append(" GROUP BY branchId, productBarcode, unitId ");
        query.append(" HAVING 1=1 ");
        if (filter.getFromSalesTotal() != null) {
            query.append(" and sum(amount) >= :fromSalesTotal");
            params.put("fromSalesTotal", filter.getFromSalesTotal());
        }
        if (filter.getToSalesTotal() != null) {
            query.append(" and sum(amount) <= :toSalesTotal");
            params.put("toSalesTotal", filter.getToSalesTotal());
        }
        if (filter.getQtyTo() != null) {
            query.append(" and sum(qty) <= :qtyTo");
            params.put("qtyTo", filter.getQtyTo());
        }
        if (filter.getQtyFrom() != null) {
            query.append(" and sum(qty) >= :qtyFrom");
            params.put("qtyFrom", filter.getQtyFrom());
        }
        countQuery.append(query).append(")");
        if (StringUtils.isNotBlank(filter.getOrderBy()) && Arrays.asList("salesCount", "salesTotal", "discount", "receiptCount", "nds", "price")
                .contains(filter.getOrderBy())) {
            query.append(" ORDER BY ").append(filter.getOrderBy());
            if (SortTypeEnum.desc.equals(filter.getSortOrder())) {
                query.append(" ").append(SortTypeEnum.desc.name());
            }
        }
        if (forExcel) {
            filter.setPage(0);
            filter.setSize(20_000);
        }
        ClickhouseUtils.setLimits(query, filter.getPage(), filter.getSize());
        selectQuery.append(query);
        Long count = jdbcClient.sql(countQuery.toString()).params(params).query(Long.class).single();
        return new PageImpl<>(jdbcClient.sql(selectQuery.toString()).params(params).query(SalesStatsDTO.class).list(), filter.getPageable(), count);
    }

    @Override
    public Page<SalesStatsDTO> getProductSalesStatsByCompany(SalesStatsFilter filter, boolean forExcel) {
        StringBuilder query = new StringBuilder(" FROM ").append(this.RECEIPT_DETAIL).append(" rd WHERE 1=1 ");
        StringBuilder countQuery = new StringBuilder("SELECT count() FROM (SELECT 1 ");
        StringBuilder selectQuery = new StringBuilder("""
                select any(productId) AS id,
                any(productName) AS name,
                any(vatBarcode) AS vatBarcode,
                productBarcode AS barcode,
                any(unitName) AS unit,
                any(categoryName) AS category,
                max(price) AS price,
                sum(qty) AS salesCount,
                sum(amount) AS salesTotal,
                sum(nds) AS nds,
                any(ndsPercent) AS ndsPercent,
                sum(discount) AS discount,
                count() AS receiptCount\s""");
        var params = new HashMap<String, Object>();
        setBaseQueryFilters(query, filter, params);
        if (filter.getProductId() != null) {
            query.append(" and rd.productId = :productId");
            params.put("productId", filter.getProductId());
        }
        if (StringUtils.isNotBlank(filter.getSearch())) {
            query.append(" and lowerUTF8(rd.productName) like :search");
            params.put("search", "%" + filter.getSearch().toLowerCase() + "%");
        }
        if (filter.isOnlySocial()) {
            query.append(" and rd.ndsPercent = 0");
        }
        if (!CollectionUtils.isEmpty(filter.getCategoryIds())) {
            query.append(" and categoryId in (:categoryIds)");
            params.put("categoryIds", filter.getCategoryIds());
        }
        query.append(" GROUP BY productBarcode, unitId ");
        query.append(" HAVING 1=1 ");
        if (filter.getFromSalesTotal() != null) {
            query.append(" and sum(amount) >= :fromSalesTotal");
            params.put("fromSalesTotal", filter.getFromSalesTotal());
        }
        if (filter.getToSalesTotal() != null) {
            query.append(" and sum(amount) <= :toSalesTotal");
            params.put("toSalesTotal", filter.getToSalesTotal());
        }
        countQuery.append(query).append(")");
        if (StringUtils.isNotBlank(filter.getOrderBy()) && Arrays.asList("salesCount", "salesTotal", "discount", "receiptCount", "nds", "price")
                .contains(filter.getOrderBy())) {
            query.append(" ORDER BY ").append(filter.getOrderBy());
            if (SortTypeEnum.desc.equals(filter.getSortOrder())) {
                query.append(" ").append(SortTypeEnum.desc.name());
            }
        }
        if (forExcel) {
            filter.setPage(0);
            filter.setSize(20_000);
        }
        ClickhouseUtils.setLimits(query, filter.getPage(), filter.getSize());
        selectQuery.append(query);
        Long count = jdbcClient.sql(countQuery.toString()).params(params).query(Long.class).single();
        return new PageImpl<>(jdbcClient.sql(selectQuery.toString()).params(params).query(SalesStatsDTO.class).list(), filter.getPageable(), count);
    }

    @Override
    public SalesStatsProductsHeader getProductSalesStatsHeader(SalesStatsFilter filter) {
        StringBuilder query = new StringBuilder("SELECT SUM(t.totalProductCount) AS totalProductCount, " +
                "sum(t.totalSum) AS totalSum, " +
                "sum(t.totalNds) AS totalNds, " +
                "sum(t.totalDiscount) AS totalDiscount FROM (");
        query.append("SELECT uniq(productBarcode) AS totalProductCount," +
                "sum(amount) AS totalSum," +
                "sum(nds) AS totalNds," +
                "sum(discount) AS totalDiscount FROM ").append(this.RECEIPT_DETAIL).append(" rd WHERE 1=1 ");
        var params = new HashMap<String, Object>();
        setSalesStatsFilter(filter, query, params);
        query.append(" GROUP BY rd.branchId, rd.productBarcode, rd.unitId ) t");
        return jdbcClient.sql(query.toString()).params(params).query(SalesStatsProductsHeader.class).single();
    }

    @Override
    public Page<SalesStatsDTO> getProductSalesDynamics(SalesDynamicsFilter filter) {
        if ("id".equals(filter.getOrderBy())) {
            filter.setOrderBy(null);
        }
        String select, groupBy;
        switch (filter.getGranularity()) {
            case MONTH:
            case month:
                filter.setGranularity(TimeInterval.MONTH);
                select = "toStartOfMonth(rd.receiptDateTime) AS dateTime";
                groupBy = "toStartOfMonth(rd.receiptDateTime)";
                break;
            case WEEK:
            case week:
                filter.setGranularity(TimeInterval.WEEK);
                select = "toMonday(rd.receiptDateTime) AS dateTime";
                groupBy = "toMonday(rd.receiptDateTime)";
                break;
            case HOUR:
            case hour:
                filter.setGranularity(TimeInterval.HOUR);
                select = "toStartOfHour(rd.receiptDateTime) AS dateTime";
                groupBy = "toStartOfHour(rd.receiptDateTime)";
                break;
            default:
                filter.setGranularity(TimeInterval.DAY);
                select = "toDate(rd.receiptDateTime) AS dateTime";
                groupBy = "toDate(rd.receiptDateTime)";
        }
        StringBuilder query = new StringBuilder(" FROM ").append(this.RECEIPT_DETAIL).append(" rd WHERE 1=1 ");
        var params = new HashMap<String, Object>();
        setSalesStatsFilter(filter, query, params);
        StringBuilder selectQuery = new StringBuilder("SELECT " + select + ", " +
                "any(rd.productName) AS name," +
                "any(rd.unitName) AS unit," +
                "sum(rd.qty) AS salesCount," +
                "sum(rd.amount) AS salesTotal," +
                "sum(rd.nds) AS nds," +
                "sum(rd.discount) AS discount," +
                "count() AS receiptCount," +
                "any(rd.price) AS price ");
        query.append(" GROUP BY rd.productId, rd.unitId,").append(groupBy);
        Long count = jdbcClient.sql("SELECT count() FROM (SELECT 1 " + query + ") AS countTable").params(params).query(Long.class).single();
        query.append(" ORDER BY ");
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            query.append(filter.getOrderBy());
        } else {
            query.append("dateTime");
        }
        query.append(" ");
        if (filter.getSortOrder() != null) {
            query.append(filter.getSortOrder().name());
        }
        ClickhouseUtils.setLimits(query, filter.getPage(), filter.getSize());
        selectQuery.append(query);
        return new PageImpl<>(jdbcClient.sql(selectQuery.toString()).params(params).query(SalesStatsDTO.class).list(), filter.getPageable(), count);
    }

    private void setSalesStatsFilter(SalesStatsFilter filter, StringBuilder query, Map<String, Object> params) {
        setBaseQueryFilters(query, filter, params);
        if (filter.getProductId() != null) {
            query.append(" and productId = :productId");
            params.put("productId", filter.getProductId());
        }
        if (!StringUtils.isEmpty(filter.getSearch())) {
            query.append(" and (lowerUTF8(productName) like :search or rd.productBarcode like :search)");
            params.put("search", "%" + filter.getSearch().toLowerCase() + "%");
        }
        if (filter.isOnlySocial()) {
            query.append(" and ndsPercent = 0");
        }
    }

    @Override
    public List<ProductStatsDTO> getTopProducts(SalesStatsFilter filter) {
        StringBuilder query = new StringBuilder("SELECT productId as id, any(productName) AS name, sum(amount) AS salesTotal FROM ").append(this.RECEIPT_DETAIL)
                .append(" WHERE 1=1 ");
        var params = new HashMap<String, Object>();
        setBaseQueryFilters(query, filter, params);
        query.append(" GROUP BY productId ORDER BY salesTotal DESC LIMIT 20");
        return jdbcClient.sql(query.toString()).params(params).query(ProductStatsDTO.class).list();
    }

    @Override
    public List<SalesStatsDTO> getCategorySalesStats(SalesStatsFilter filter) {
        StringBuilder query = new StringBuilder("SELECT categoryId as id, sum(qty) AS salesCount, sum(amount) AS salesTotal, sum(nds) AS nds, sum(discount) AS discount, count() AS receiptCount FROM ")
                .append(this.RECEIPT_DETAIL).append(" WHERE status = 'PAID' and branchId = :branchId");
        var params = new HashMap<String, Object>();
        params.put("branchId", filter.getBranchId());
        setDateTimeFilters(query, filter, params);
        query.append(" GROUP BY categoryId");
        return jdbcClient.sql(query.toString()).params(params).query(SalesStatsDTO.class).list();
    }

    @Override
    public List<ProductScheduleRequest> findCreditDetails(Long receiptId) {
        return jdbcClient.sql("select productId as id," +
                        "       qty," +
                        "       price," +
                        "       ndsPercent as vatPercent" +
                        " from " + RECEIPT_DETAIL +
                        " where receiptId = :receiptId")
                .param("receiptId", receiptId).query(ProductScheduleRequest.class).list();
    }

    @Override
    public List<ProductScheduleRequest> findAdvanceDetails(Long receiptId) {
        return jdbcClient.sql("select productId as id," +
                        "       qty," +
                        "       JSONExtract(extraDetail, 'advancePrice', 'Decimal(25, 4)') as price," +
                        "       JSONExtract(extraDetail, 'advancePercent', 'Decimal(25, 4)') as advancePercent," +
                        "       ndsPercent as vatPercent" +
                        " from " + RECEIPT_DETAIL +
                        " where receiptId = :receiptId")
                .param("receiptId", receiptId).query(ProductScheduleRequest.class).list();
    }


    private void setBaseQueryFilters(StringBuilder query, SalesStatsFilter filter, Map<String, Object> params) {
        setDateTimeFilters(query, filter, params);
        query.append(" and status = 'PAID'");
        if (CollectionUtils.isNotEmpty(filter.getBranchIdsWithBranchId())) {
            query.append(" and branchId in (:branchIds)");
            params.put("branchIds", filter.getBranchIdsWithBranchId());
        } else if (filter.getCompanyId() != null) {
            query.append(" and companyId = :companyId");
            params.put("companyId", filter.getCompanyId());
        }
        if (filter.getUserId() != null) {
            query.append(" and createdById = :userId");
            params.put("userId", filter.getUserId());
        }
    }

    private void setDateTimeFilters(StringBuilder query, BaseFilter filter, Map<String, Object> params) {
        if (filter.getFrom() != null) {
            query.append(" and receiptDateTime >= :from");
            params.put("from", filter.getFrom().withNano(0));
        }
        if (filter.getTo() != null) {
            query.append(" and receiptDateTime <= :to");
            params.put("to", filter.getTo().withNano(0));
        }
    }

}
