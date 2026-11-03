package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.domain.warehouse.Vehicle;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.VehicleRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.01.2025
 */
public class VehicleRepositoryImpl extends BaseFilterableRepositoryImpl<Vehicle, BaseFilter>  implements VehicleRepositoryCustom {

    @Override
    protected Class<Vehicle> getEntityClass() {
        return Vehicle.class;
    }

    @Override
    protected String getEntityAlias() {
        return "v";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Vehicle v ");

        // Base conditions
        sql.append("WHERE v.deleted = false ");
        sql.append(" AND v.companyId =:companyId ");

        // Dynamic conditions
        appendCondition(sql, StringUtils.isNotEmpty(filter.getStatus()), "AND v.status = :status ");

        // Search conditions with multiple fields
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(v.name) ").append(filter.getSearchKey())
                .append(" OR LOWER(v.regNo) ").append(filter.getSearchKey())
                .append(" OR LOWER(v.model) ").append(filter.getSearchKey())
                .append(" OR LOWER(v.ownerFullName) ").append(filter.getSearchKey())
                .append(" OR LOWER(v.ownerPinfl) ").append(filter.getSearchKey())
                .append(" OR LOWER(v.transportCompanyTin) ").append(filter.getSearchKey())
                .append(" OR LOWER(v.transportCompanyName) ").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
