package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.equipment.Equipment;
import uz.uzkassa.smartpossupply.enums.EquipmentType;
import uz.uzkassa.smartpossupply.filters.EquipmentFilter;
import uz.uzkassa.smartpossupply.repository.EquipmentRepositoryCustom;


public class EquipmentRepositoryImpl extends BaseFilterableRepositoryImpl<Equipment, EquipmentFilter> implements EquipmentRepositoryCustom {

    @Override
    protected Class<Equipment> getEntityClass() {
        return Equipment.class;
    }

    @Override
    protected String getEntityAlias() {
        return "e";
    }

    @Override
    public void applyFilter(EquipmentFilter filter) {
    }

    @Override
    public String getBaseQuery(EquipmentFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Equipment e ");

        if (includeFetch) {
            sql.append("JOIN FETCH e.customerBranch cb ");
        } else {
            sql.append("JOIN e.customerBranch cb ");
        }

        sql.append("WHERE e.deleted = false ");

        appendCondition(sql, StringUtils.hasText(filter.getType()), "AND e.type = :type ");
        appendCondition(sql, filter.getCustomerBranchId() != null, "AND e.customerBranchId = :customerBranchId ");
        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(e.name)").append(filter.getSearchKey())
                .append(" OR LOWER(e.brand)").append(filter.getSearchKey())
                .append(") ");
        }
        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, EquipmentFilter filter) {
        if (StringUtils.hasText(filter.getType())) {
            query.setParameter("type", EquipmentType.valueOf(filter.getType()));
        }
        setParameterIfNotNull(query, "customerBranchId", filter.getCustomerBranchId());
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}