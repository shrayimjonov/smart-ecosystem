package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.repository.StockReturnRepositoryCustom;

@SuppressWarnings("unused")
public class StockReturnRepositoryImpl
    extends BaseFilterableRepositoryImpl<Stock, StockFilter>
    implements StockReturnRepositoryCustom {

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
        StringBuilder sql = new StringBuilder(" FROM Stock s ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH s.supplier supp ")
                .append("JOIN FETCH s.customer cus ")
                .append("JOIN FETCH s.company c ")
                .append("JOIN FETCH s.branch b ")
                .append("JOIN FETCH s.warehouse w ")
                .append("LEFT JOIN FETCH s.factura f ");
        } else {
            sql.append("JOIN s.supplier supp ")
                .append("JOIN s.customer cus ")
                .append("JOIN s.company c ")
                .append("JOIN s.branch b ")
                .append("JOIN s.warehouse w ")
                .append("LEFT JOIN s.factura f ");
        }

        // Base condition - always RETURN type for this repository
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
        appendCondition(sql, filter.getCashierId() != null, "AND s.cashierId = :cashierId ");
        appendCondition(sql, filter.getStatus() != null, "AND s.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND s.transferDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND s.transferDate <= :toDate ");

        // Boolean condition for hasFactura
        if (filter.getHasFactura() != null) {
            if (filter.getHasFactura()) {
                sql.append("AND s.facturaId IS NOT NULL ");
            } else {
                sql.append("AND s.facturaId IS NULL ");
            }
        }

        // Search conditions across multiple fields
        if (filter.isNotEmpty()) {
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
        // Required parameter - always RETURN for this repository
        query.setParameter("transferType", TransferType.RETURN);

        // Optional parameters
        setParameterIfNotNull(query, "companyId", filter.getCompanyId());
        setParameterIfNotNull(query, "creatorId", filter.getCreatorId());
        setParameterIfNotNull(query, "supplierId", filter.getSupplierId());
        setParameterIfNotNull(query, "customerId", filter.getCustomerId());
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "warehouseId", filter.getWarehouseId());
        setParameterIfNotNull(query, "cashierId", filter.getCashierId());
        setParameterIfNotNull(query, "status", filter.getStatus() != null ? StockStatus.valueOf(filter.getStatus()) : null);
        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
