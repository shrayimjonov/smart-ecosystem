package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.checklist.Checklist;
import uz.uzkassa.smartpossupply.enums.ChecklistStatus;
import uz.uzkassa.smartpossupply.filters.ChecklistFilter;
import uz.uzkassa.smartpossupply.repository.ChecklistRepositoryCustom;

@Repository
public class ChecklistRepositoryImpl extends BaseFilterableRepositoryImpl<Checklist, ChecklistFilter> implements ChecklistRepositoryCustom {

    @Override
    protected Class<Checklist> getEntityClass() {
        return Checklist.class;
    }

    @Override
    protected String getEntityAlias() {
        return "ch";
    }

    @Override
    public void applyFilter(ChecklistFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(ChecklistFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Checklist ch ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH ch.supervisor s ")
                .append("JOIN FETCH ch.agent a ");
        } else {
            sql.append("JOIN ch.supervisor s ")
                .append("JOIN ch.agent a ");
        }

        // Base condition
        sql.append("WHERE ch.deleted = false ");

        // Dynamic conditions
        appendCondition(sql, filter.getCompanyId() != null, "AND ch.companyId = :companyId ");
        appendCondition(sql, filter.getAgentId() != null, "AND a.id = :agentId ");
        appendCondition(sql, filter.getSupervisorId() != null, "AND s.id = :supervisorId ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND ch.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND ch.checklistDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND ch.checklistDate <= :toDate ");

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, ChecklistFilter filter) {
        // Optional parameters
        setParameterIfNotNull(query, "companyId", filter.getCompanyId());
        setParameterIfNotNull(query, "agentId", filter.getAgentId());
        setParameterIfNotNull(query, "supervisorId", filter.getSupervisorId());

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", ChecklistStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());
    }
}
