package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.Lgota;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.LgotaRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * ate: 21.07.2023
 */
public class LgotaRepositoryImpl extends BaseFilterableRepositoryImpl<Lgota, BaseFilter> implements LgotaRepositoryCustom {

    @Override
    protected Class<Lgota> getEntityClass() {
        return Lgota.class;
    }

    @Override
    protected String getEntityAlias() {
        return "l";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Lgota l ");
        sql.append("WHERE 1=1 ");

        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(l.name)").append(filter.getSearchKey())
                .append(" OR LOWER(l.catalogCode)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
