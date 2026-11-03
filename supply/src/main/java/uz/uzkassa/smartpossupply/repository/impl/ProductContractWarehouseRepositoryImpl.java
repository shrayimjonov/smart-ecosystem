package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.product.ProductContractWarehouse;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.repository.ProductContractWarehouseRepositoryCustom;

import java.math.BigDecimal;

public class ProductContractWarehouseRepositoryImpl
    extends BaseFilterableRepositoryImpl<ProductContractWarehouse, BalanceReportFilter>
    implements ProductContractWarehouseRepositoryCustom {

    @Override
    protected Class<ProductContractWarehouse> getEntityClass() {
        return ProductContractWarehouse.class;
    }

    @Override
    protected String getEntityAlias() {
        return "pcw";
    }

    @Override
    public void applyFilter(BalanceReportFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
    }

    @Override
    public String getBaseQuery(BalanceReportFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM ProductContractWarehouse pcw ");

        // Add joins (never use FETCH for filter-based joins)
        if (includeFetch) {
            sql.append("JOIN FETCH pcw.productContract pc ")
                .append("JOIN FETCH pcw.warehouse w ")
                .append("JOIN FETCH pc.product p ");
        } else {
            sql.append("JOIN pcw.productContract pc ")
                .append("JOIN pcw.warehouse w ")
                .append("JOIN pc.product p ");
        }

        // Base condition
        sql.append("WHERE p.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.isHideZero(), "AND pcw.qty > 0 ");
        appendCondition(sql, filter.getBranchId() != null, "AND p.branchId = :branchId ");
        appendCondition(sql, filter.getWarehouseId() != null, "AND w.id = :warehouseId ");
        appendCondition(sql, filter.getCategoryId() != null, "AND p.categoryId = :categoryId ");

        // Search conditions across multiple fields
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

    @Override
    public BigDecimal getReportTotalQty(BalanceReportFilter filter) {
        applyFilter(filter);

        String sql = "SELECT SUM(pcw.qty) " + getBaseQuery(filter, false);

        TypedQuery<BigDecimal> query = entityManager.createQuery(sql, BigDecimal.class);
        setParameters(query, filter);

        return query.getSingleResult();
    }
}
