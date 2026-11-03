package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;
import uz.uzkassa.smartpossupply.filters.StockTransferFilter;
import uz.uzkassa.smartpossupply.repository.StockTransferRepositoryCustom;

public class StockTransferRepositoryImpl extends BaseFilterableRepositoryImpl<StockTransfer, StockTransferFilter> implements StockTransferRepositoryCustom {

    @Override
    protected Class<StockTransfer> getEntityClass() {
        return StockTransfer.class;
    }

    @Override
    protected String getEntityAlias() {
        return "st";
    }

    @Override
    public void applyFilter(StockTransferFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(StockTransferFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM StockTransfer st ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH st.fromBranch fb ")
                .append("JOIN FETCH st.fromWarehouse fw ")
                .append("JOIN FETCH st.toBranch tb ")
                .append("JOIN FETCH st.toWarehouse tw ")
                .append("LEFT JOIN FETCH st.stock s ")
                .append("LEFT JOIN FETCH st.trip t ");
        } else {
            sql.append("JOIN st.fromBranch fb ")
                .append("JOIN st.fromWarehouse fw ")
                .append("JOIN st.toBranch tb ")
                .append("JOIN st.toWarehouse tw ")
                .append("LEFT JOIN st.stock s ")
                .append("LEFT JOIN st.trip t ");
        }

        // Base conditions
        sql.append("WHERE st.deleted = false ")
            .append("AND st.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getFromBranchId() != null, "AND st.fromBranchId = :fromBranchId ");
        appendCondition(sql, filter.getToBranchId() != null, "AND st.toBranchId = :toBranchId ");
        appendCondition(sql, filter.getExpeditorId() != null, "AND st.expeditorId = :expeditorId ");
        appendCondition(sql, filter.getFromWarehouseId() != null, "AND st.fromWarehouseId = :fromWarehouseId ");
        appendCondition(sql, filter.getToWarehouseId() != null, "AND st.toWarehouseId = :toWarehouseId ");
        appendCondition(sql, filter.getStatus() != null, "AND st.status = :status ");
        appendCondition(sql, filter.getExcludeStatus() != null, "AND st.status != :excludeStatus ");
        appendCondition(sql, filter.getFrom() != null, "AND st.transferDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND st.transferDate <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(st.transferNumber) ").append(filter.getSearchKey())
                .append(" OR LOWER(st.description) ").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, StockTransferFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "fromBranchId", filter.getFromBranchId());
        setParameterIfNotNull(query, "toBranchId", filter.getToBranchId());
        setParameterIfNotNull(query, "expeditorId", filter.getExpeditorId());
        setParameterIfNotNull(query, "fromWarehouseId", filter.getFromWarehouseId());
        setParameterIfNotNull(query, "toWarehouseId", filter.getToWarehouseId());

        if (filter.getStatus() != null) {
            query.setParameter("status", StockTransferStatus.valueOf(filter.getStatus()));
        }

        if (filter.getExcludeStatus() != null) {
            query.setParameter("excludeStatus", StockTransferStatus.valueOf(filter.getExcludeStatus()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
