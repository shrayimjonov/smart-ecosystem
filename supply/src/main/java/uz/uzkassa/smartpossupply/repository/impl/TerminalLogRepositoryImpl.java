package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.TerminalLog;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.TerminalLogRepositoryCustom;


public class TerminalLogRepositoryImpl extends BaseFilterableRepositoryImpl<TerminalLog, BaseFilter>  implements TerminalLogRepositoryCustom {

    @Override
    protected Class<TerminalLog> getEntityClass() {
        return TerminalLog.class;
    }

    @Override
    protected String getEntityAlias() {
        return "tl";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        // No custom filter logic needed for this repository
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM TerminalLog tl ");

        sql.append("WHERE 1=1 ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(tl.serialNumber)").append(filter.getSearchKey())
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