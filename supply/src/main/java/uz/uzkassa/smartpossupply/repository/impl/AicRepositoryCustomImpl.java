package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.aic.Aic;
import uz.uzkassa.smartpossupply.enums.AicStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.AicRepositoryCustom;

public class AicRepositoryCustomImpl
    extends BaseFilterableRepositoryImpl<Aic, BaseFilter>
    implements AicRepositoryCustom {

    @Override
    protected Class<Aic> getEntityClass() {
        return Aic.class;
    }

    @Override
    protected String getEntityAlias() {
        return "a";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Aic a ");

        if (includeFetch) {
            sql.append(" JOIN FETCH a.creator creator ")
                .append(" JOIN FETCH a.product p ");
        } else {
            sql.append(" JOIN a.creator creator ")
                .append(" JOIN a.product p ");
        }

        // Base conditions
        sql.append("WHERE a.deleted = false ")
            .append("AND a.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getStatus() != null, "AND a.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND a.updatedDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND a.updatedDate <= :toDate ");

        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(a.reportId)").append(filter.getSearchKey())
                .append(" OR LOWER(p.name)").append(filter.getSearchKey())
                .append(" OR LOWER(p.barcode)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        if (filter.getStatus() != null) {
            query.setParameter("status", AicStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
