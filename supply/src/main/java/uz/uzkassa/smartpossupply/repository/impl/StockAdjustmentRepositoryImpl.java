package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.domain.stockadjustment.StockAdjustment;
import uz.uzkassa.smartpossupply.enums.AdjustmentReason;
import uz.uzkassa.smartpossupply.enums.StockAdjustmentStatus;
import uz.uzkassa.smartpossupply.filters.StockAdjustmentFilter;
import uz.uzkassa.smartpossupply.repository.StockAdjustmentRepositoryCustom;

@SuppressWarnings("unused")
public class StockAdjustmentRepositoryImpl
    extends BaseFilterableRepositoryImpl<StockAdjustment, StockAdjustmentFilter>
    implements StockAdjustmentRepositoryCustom {

    @Override
    protected Class<StockAdjustment> getEntityClass() {
        return StockAdjustment.class;
    }

    @Override
    protected String getEntityAlias() {
        return "sa";
    }

    @Override
    public void applyFilter(StockAdjustmentFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
    }

    @Override
    public String getBaseQuery(StockAdjustmentFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM StockAdjustment sa ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH sa.branch b ")
                .append("JOIN FETCH sa.warehouse w ")
                .append("JOIN FETCH sa.creator creator ");
        } else {
            sql.append("JOIN sa.branch b ")
                .append("JOIN sa.warehouse w ")
                .append("JOIN sa.creator creator ");
        }

        // Base conditions
        sql.append("WHERE sa.deleted = false ")
            .append("AND sa.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getBranchId() != null, "AND sa.branchId = :branchId ");
        appendCondition(sql, filter.getWarehouseId() != null, "AND sa.warehouseId = :warehouseId ");
        appendCondition(sql, filter.getStatus() != null, "AND sa.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND sa.adjustmentDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND sa.adjustmentDate <= :toDate ");
        appendCondition(sql, StringUtils.isNotEmpty(filter.getReason()), "AND sa.adjustmentReason = :adjustmentReason ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(sa.adjustmentNumber) ").append(filter.getSearchKey())
                .append(" OR LOWER(sa.description) ").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, StockAdjustmentFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "warehouseId", filter.getWarehouseId());

        if (filter.getStatus() != null) {
            query.setParameter("status", StockAdjustmentStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (StringUtils.isNotEmpty(filter.getReason())) {
            query.setParameter("adjustmentReason", AdjustmentReason.valueOf(filter.getReason()));
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
