package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.WarehouseRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;


@SuppressWarnings("unused")
public class WarehouseRepositoryImpl extends BaseFilterableRepositoryImpl<Warehouse, BaseFilter> implements WarehouseRepositoryCustom {

    @Override
    protected Class<Warehouse> getEntityClass() {
        return Warehouse.class;
    }

    @Override
    protected String getEntityAlias() {
        return "w";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        if (!filter.isForTransfer()) {
            filter.setBranchId(getBranchId(filter, false));
            filter.setWarehouseId(SecurityUtils.isNetworkAgent() ? null : getWarehouseId(filter, false));
        }
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Warehouse w ");
        if (includeFetch) {
            sql.append(" JOIN FETCH w.branch b ")
                .append(" LEFT JOIN FETCH w.owner o ")
                .append(" LEFT JOIN FETCH w.parent p ");
        } else {
            sql.append(" JOIN w.branch b ")
                .append(" LEFT JOIN w.owner o ")
                .append(" LEFT JOIN w.parent p ");
        }
        sql.append(" WHERE w.deleted = false ");

        appendCondition(sql, filter.getCompanyId() != null, "AND b.companyId = :companyId ");
        appendCondition(sql, filter.getBranchId() != null, "AND b.id = :branchId ");

        if (!SecurityUtils.isNetworkAgent()) {
            if (filter.getParentId() != null) {
                sql.append(" AND p.id =:parentId ");
            } else {
                sql.append(" AND p.id IS NULL ");
            }
        }
        appendCondition(sql, filter.getWarehouseId() != null, " AND w.id = :warehouseId ");
        appendCondition(sql, filter.getRegionId() != null, "AND b.regionId = :regionId ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND w.status = :status ");

        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(w.name) ").append(filter.getSearchKey())
                .append(" OR LOWER(w.truckRegNo) ").append(filter.getSearchKey())
                .append(" OR LOWER(w.truckModel) ").append(filter.getSearchKey())
                .append(") ");
        }
        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        setParameterIfNotNull(query, "companyId", filter.getCompanyId());
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "warehouseId", filter.getWarehouseId());
        setParameterIfNotNull(query, "parentId", filter.getParentId());
        setParameterIfNotNull(query, "regionId", filter.getRegionId());

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public boolean checkQtyInWarehouse(Long warehouseId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select coalesce(sum(wp.qty),0) from warehouse_product wp");
        sql.append(" inner join warehouse w on wp.warehouse_id = w.id");
        sql.append(" where w.deleted = false");
        sql.append(" and w.id =").append(warehouseId);
        long count = ((BigDecimal) entityManager.createNativeQuery(sql.toString()).getSingleResult()).longValue();
        return count > 0;
    }
}
