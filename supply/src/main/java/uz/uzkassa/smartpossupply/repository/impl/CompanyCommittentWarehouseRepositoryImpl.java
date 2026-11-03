package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.smartpossupply.domain.CompanyCommittentWarehouse;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stockreport.StockProductStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.repository.CompanyCommittentWarehouseRepositoryCustom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CompanyCommittentWarehouseRepositoryImpl
    extends BaseFilterableRepositoryImpl<CompanyCommittentWarehouse, BalanceReportFilter>
    implements CompanyCommittentWarehouseRepositoryCustom {

    @Override
    protected Class<CompanyCommittentWarehouse> getEntityClass() {
        return CompanyCommittentWarehouse.class;
    }

    @Override
    protected String getEntityAlias() {
        return "ccw";
    }

    @Override
    public void applyFilter(BalanceReportFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
    }

    @Override
    public String getBaseQuery(BalanceReportFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM CompanyCommittentWarehouse ccw ");

        // Add joins (note: multiple levels of joins)
        if (includeFetch) {
            sql.append("JOIN FETCH ccw.companyCommittent cc ")
                .append("JOIN FETCH cc.committent c ")
                .append("JOIN FETCH ccw.warehouse w ")
                .append("JOIN FETCH ccw.product p ")
                .append("JOIN FETCH p.branch b ")
                .append("LEFT JOIN FETCH p.parent parent ");
        } else {
            sql.append("JOIN ccw.companyCommittent cc ")
                .append("JOIN cc.committent c ")
                .append("JOIN ccw.warehouse w ")
                .append("JOIN ccw.product p ")
                .append("JOIN p.branch b ")
                .append("LEFT JOIN p.parent parent ");
        }

        // Base condition
        sql.append("WHERE p.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.isHideZero(), "AND ccw.qty > 0 ");
        appendCondition(sql, filter.getBranchId() != null, "AND p.branchId = :branchId ");
        appendCondition(sql, filter.getWarehouseId() != null, "AND w.id = :warehouseId ");
        appendCondition(sql, filter.getCategoryId() != null, "AND p.categoryId = :categoryId ");
        appendCondition(sql, filter.getProductId() != null, "AND p.id = :productId ");
        appendCondition(sql, filter.getCommittentId() != null, "AND c.id = :committentId ");

        // Search conditions across multiple fields
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(p.name)").append(filter.getSearchKey())
                .append(" OR LOWER(p.barcode)").append(filter.getSearchKey())
                .append(" OR LOWER(p.vatBarcode)").append(filter.getSearchKey())
                .append(" OR LOWER(p.catalogName)").append(filter.getSearchKey())
                .append(" OR LOWER(c.tin)").append(filter.getSearchKey())
                .append(" OR LOWER(c.name)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BalanceReportFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "warehouseId", filter.getWarehouseId());
        setParameterIfNotNull(query, "categoryId", filter.getCategoryId());
        setParameterIfNotNull(query, "productId", filter.getProductId());
        setParameterIfNotNull(query, "committentId", filter.getCommittentId());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public BigDecimal getReportTotalQty(BalanceReportFilter filter) {
        applyFilter(filter);

        String sql = "SELECT SUM(ccw.qty) " + getBaseQuery(filter, false);

        TypedQuery<BigDecimal> query = entityManager.createQuery(sql, BigDecimal.class);
        setParameters(query, filter);

        return query.getSingleResult();
    }

    @Override
    public StockProductQtyStatisticsDTO getQtyStatistics(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        //stock-in
        sql.append(" select coalesce(sum(sp.qty),0) as qty, 'STOCK_IN_TOTAL' as dataType");
        sql.append(getStatisticsBaseSql(filter, TransferType.IN));
        sql.append(" union all");
        // stock-out
        sql.append(" select coalesce(sum(sp.qty),0) as qty, 'STOCK_OUT_TOTAL' as dataType");
        sql.append(getStatisticsBaseSql(filter, TransferType.OUT));
        sql.append(" union all");
        // stock-return
        sql.append(" select coalesce(sum(sp.qty),0) as qty, 'STOCK_RETURN_TOTAL' as dataType");
        sql.append(getStatisticsBaseSql(filter, TransferType.RETURN));

        Query query = entityManager.createNativeQuery(sql.toString(), "StockProductQtyStatisticsMapper");
        List<StockProductStatisticsDTO> statistics = query.getResultList();

        StockProductQtyStatisticsDTO result = new StockProductQtyStatisticsDTO();
        statistics.forEach(stockProductStatisticsDTO -> {
            switch (stockProductStatisticsDTO.getDataType()) {
                case "STOCK_IN_TOTAL":
                    result.setStockInTotal(stockProductStatisticsDTO.getQty());
                    break;
                case "STOCK_OUT_TOTAL":
                    result.setStockOutTotal(stockProductStatisticsDTO.getQty());
                    break;
                case "STOCK_RETURN_TOTAL":
                    result.setStockReturnTotal(stockProductStatisticsDTO.getQty());
                    break;
            }
        });
        return result;
    }

    private StringBuilder getStatisticsBaseSql(StockFilter filter, TransferType transferType) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from stock s ");
        sql.append(" join company c");
        if (TransferType.OUT.equals(filter.getTransferType())) {
            sql.append(" on s.customer_id = c.id ");
        } else if (TransferType.IN.equals(filter.getTransferType())) {
            sql.append(" on s.supplier_id = c.id ");
        } else {
            sql.append(" on s.company_id = c.id ");
        }
        sql.append(" inner join stock_product sp on sp.stock_id = s.id ");
        sql.append(" inner join company_committent cc on cc.id = sp.company_committent_id ");
        sql.append(" where s.deleted = false and sp.deleted = false");
        sql.append(" and s.stock_transfer_id is null");
        sql.append(" and cc.committent_id = ").append(filter.getCompanyCommittentId());
        if (filter.getProductId() != null) {
            sql.append(" and sp.product_id = ").append(filter.getProductId());
        }
        sql.append(" and s.status='").append(StockStatus.COMPLETED.getCode()).append("'");
        if (filter.getWarehouseId() != null) {
            sql.append(" and (s.warehouse_id=").append(filter.getWarehouseId());
            sql.append(" or sp.warehouse_id=").append(filter.getWarehouseId());
            sql.append(" )");
        }
        sql.append(" and s.transfer_type='").append(transferType.name()).append("'");
        if (filter.getFrom() != null) {
            sql.append(" and s.transfer_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.transfer_date<=:toDate ");
        }
        return sql;
    }

    @Override
    public Page<StockProductQtyDetailListDTO> getQtyDetails(StockFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select s.id as id, s.stock_number stockNumber, s.transfer_date as stockDate, sp.qty as qty, c.id as companyId, c.name as companyName, c.tin as companyTin");
        sql.append(getStatisticsBaseSql(filter, filter.getTransferType()));
        sql.append(" order by s.id desc");

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(s.id) ");
        countSql.append(getStatisticsBaseSql(filter, filter.getTransferType()));

        Query query = entityManager
            .createNativeQuery(sql.toString(), "StockProductQtyDetailListMapper")
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
}
