package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.CustomerGroup;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.CustomerGroupRepositoryCustom;


@Repository
public class CustomerGroupRepositoryImpl extends BaseFilterableRepositoryImpl<CustomerGroup, BaseFilter> implements CustomerGroupRepositoryCustom {

    @Override
    protected Class<CustomerGroup> getEntityClass() {
        return CustomerGroup.class;
    }

    @Override
    protected String getEntityAlias() {
        return "cg";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM CustomerGroup cg ");
        if (includeFetch) {
            sql.append("JOIN FETCH cg.company c ")
                .append("JOIN FETCH cg.creator creator ");
        } else {
            sql.append("JOIN cg.company c ")
                .append("JOIN cg.creator creator ");
        }
        // Base condition
        sql.append("WHERE cg.companyId =:companyId ");
        // Dynamic conditions
        appendCondition(sql, filter.getFrom() != null, "AND cg.createdDate >=:fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND cg.createdDate >=:toDate ");
        appendCondition(sql, filter.getCreatorId() != null, "AND creator.id=:creatorId ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND cg.status=:status ");

        // Search conditions across multiple fields
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(cg.name)").append(filter.getSearchKey())
                .append("OR LOWER(cg.description)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        query.setParameter("companyId", getCompanyId(filter));

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());
        setParameterIfNotNull(query, "creatorId", filter.getCreatorId());

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
