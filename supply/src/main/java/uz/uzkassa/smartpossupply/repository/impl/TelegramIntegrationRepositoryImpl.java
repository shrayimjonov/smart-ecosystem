package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.telegram.TelegramIntegration;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.TelegramIntegrationRepositoryCustom;

public class TelegramIntegrationRepositoryImpl extends BaseFilterableRepositoryImpl<TelegramIntegration, BaseFilter>  implements TelegramIntegrationRepositoryCustom {

    @Override
    protected Class<TelegramIntegration> getEntityClass() {
        return TelegramIntegration.class;
    }

    @Override
    protected String getEntityAlias() {
        return "ti";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        // No custom filter logic needed for this repository
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM TelegramIntegration ti ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH ti.company c ");
        } else {
            sql.append("JOIN ti.company c ");
        }

        // Base condition
        sql.append("WHERE 1=1 ");

        // Dynamic conditions
        appendCondition(sql, filter.getCompanyId() != null, "AND c.id = :companyId ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(c.name)").append(filter.getSearchKey())
                .append(" OR LOWER(c.tin)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        // Optional parameters
        setParameterIfNotNull(query, "companyId", filter.getCompanyId());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
