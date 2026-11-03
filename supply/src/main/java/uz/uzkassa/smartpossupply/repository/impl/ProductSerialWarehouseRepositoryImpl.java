package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.productserial.ProductSerialWarehouse;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.repository.ProductSerialWarehouseRepositoryCustom;

import java.math.BigDecimal;

public class ProductSerialWarehouseRepositoryImpl
    extends BaseFilterableRepositoryImpl<ProductSerialWarehouse, BalanceReportFilter>
    implements ProductSerialWarehouseRepositoryCustom {

    @Override
    protected Class<ProductSerialWarehouse> getEntityClass() {
        return ProductSerialWarehouse.class;
    }

    @Override
    protected String getEntityAlias() {
        return "psw";
    }

    @Override
    public void applyFilter(BalanceReportFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
    }

    @Override
    public String getBaseQuery(BalanceReportFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM ProductSerialWarehouse psw ");

        if (includeFetch) {
            sql.append("JOIN FETCH psw.productSerial ps ")
                .append("JOIN FETCH psw.warehouse w ")
                .append("JOIN FETCH ps.product p ")
                .append("JOIN FETCH p.branch b ");
        } else {
            sql.append("JOIN psw.productSerial ps ")
                .append("JOIN psw.warehouse w ")
                .append("JOIN ps.product p ")
                .append("JOIN p.branch b ");
        }

        // Base condition
        sql.append("WHERE p.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.isHideZero(), "AND psw.qty > 0 ");
        appendCondition(sql, filter.getBranchId() != null, "AND p.branchId = :branchId ");
        appendCondition(sql, filter.getWarehouseId() != null, "AND w.id = :warehouseId ");
        appendCondition(sql, filter.getCategoryId() != null, "AND p.categoryId = :categoryId ");
        appendCondition(sql, filter.getProductId() != null, "AND p.id = :productId ");
        appendCondition(sql, filter.getProductSerialId() != null, "AND ps.id = :productSerialId ");

        // Search conditions across multiple fields
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(p.name)").append(filter.getSearchKey())
                .append(" OR LOWER(p.barcode)").append(filter.getSearchKey())
                .append(" OR LOWER(p.vatBarcode)").append(filter.getSearchKey())
                .append(" OR LOWER(p.catalogName)").append(filter.getSearchKey())
                .append(" OR LOWER(ps.serialNumber)").append(filter.getSearchKey())
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
        setParameterIfNotNull(query, "productSerialId", filter.getProductSerialId());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public BigDecimal getReportTotalQty(BalanceReportFilter filter) {
        applyFilter(filter);

        String sql = "SELECT SUM(psw.qty) " + getBaseQuery(filter, false);

        TypedQuery<BigDecimal> query = entityManager.createQuery(sql, BigDecimal.class);
        setParameters(query, filter);

        return query.getSingleResult();
    }
}
