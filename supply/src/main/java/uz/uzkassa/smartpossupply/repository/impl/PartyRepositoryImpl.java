package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.party.Party;
import uz.uzkassa.smartpossupply.enums.PartyStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.PartyRepositoryCustom;

public class PartyRepositoryImpl
    extends BaseFilterableRepositoryImpl<Party, BaseFilter>
    implements PartyRepositoryCustom {

    @Override
    protected Class<Party> getEntityClass() {
        return Party.class;
    }

    @Override
    protected String getEntityAlias() {
        return "a";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Party a ");

        // Base conditions
        sql.append("WHERE a.companyId = :companyId ")
            .append("AND a.deleted = false ");

        // Dynamic conditions
        appendCondition(sql, filter.getStatus() != null, "AND a.status = :status ");
        appendCondition(sql, filter.getBranchId() != null, "AND a.branchId = :branchId ");
        appendCondition(sql, filter.getFrom() != null, "AND a.createdDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND a.createdDate <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(a.partyNumber)").append(filter.getSearchKey())
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
            query.setParameter("status", PartyStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
