package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.warehouse.WarehouseProduct;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.repository.WarehouseProductRepositoryCustom;

import java.math.BigDecimal;

public class WarehouseProductRepositoryImpl
    extends BaseFilterableRepositoryImpl<WarehouseProduct, BalanceReportFilter>
    implements WarehouseProductRepositoryCustom {

    @Override
    protected Class<WarehouseProduct> getEntityClass() {
        return WarehouseProduct.class;
    }

    @Override
    protected String getEntityAlias() {
        return "wp";
    }

    @Override
    public void applyFilter(BalanceReportFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
    }

    @Override
    public String getBaseQuery(BalanceReportFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM WarehouseProduct wp ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH wp.product p ")
                .append("JOIN FETCH wp.warehouse w ")
                .append("JOIN FETCH p.branch b ")
                .append("LEFT JOIN FETCH p.parent parent ");
        } else {
            sql.append("JOIN wp.product p ")
                .append("JOIN wp.warehouse w ")
                .append("JOIN p.branch b ")
                .append("LEFT JOIN p.parent parent ");
        }

        // Base conditions
        sql.append("WHERE p.deleted = false ")
            .append("AND p.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.isHideZero(), "AND wp.qty > 0 ");
        appendCondition(sql, filter.isHideZeroForReservation(), "AND wp.reservationQty > 0 ");

        if (filter.getBranchId() != null) {
            sql.append("AND p.branchId = :branchId ")
                .append("AND w.branchId = :branchId ");
        }

        appendCondition(sql, filter.getWarehouseId() != null, "AND w.id = :warehouseId ");
        appendCondition(sql, filter.getCategoryId() != null, "AND p.categoryId = :categoryId ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(p.name)").append(filter.getSearchKey())
                .append(" OR LOWER(p.barcode)").append(filter.getSearchKey())
                .append(" OR LOWER(p.vatBarcode)").append(filter.getSearchKey())
                .append(" OR LOWER(p.catalogName)").append(filter.getSearchKey())
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

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    /**
     * Get the total quantity sum for the report
     */
    @Override
    public BigDecimal getReportTotalQty(BalanceReportFilter filter) {
        applyFilter(filter);

        String sql = "SELECT SUM(wp.qty) " + getBaseQuery(filter, false);

        TypedQuery<BigDecimal> query = entityManager.createQuery(sql, BigDecimal.class);
        setParameters(query, filter);

        return query.getSingleResult();
    }
}
