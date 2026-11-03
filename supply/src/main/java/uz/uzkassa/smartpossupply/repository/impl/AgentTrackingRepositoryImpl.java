package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.planroute.AgentTracking;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.repository.AgentTrackingRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 11.12.2024
 */
public class AgentTrackingRepositoryImpl extends BaseFilterableRepositoryImpl<AgentTracking, PlanRouteFilter> implements AgentTrackingRepositoryCustom {

    @Override
    protected Class<AgentTracking> getEntityClass() {
        return AgentTracking.class;
    }

    @Override
    protected String getEntityAlias() {
        return "at";
    }

    @Override
    public void applyFilter(PlanRouteFilter filter) {

    }

    @Override
    public String getBaseQuery(PlanRouteFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM AgentTracking at WHERE 1=1 ");

        appendCondition(sql, filter.getAgentId() != null, "AND at.agentId = :agentId ");
        appendCondition(sql, filter.getFrom() != null, "AND at.createdDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND at.createdDate <= :toDate ");
        appendCondition(sql, filter.getEntityId() != null, "AND at.entityId = :entityId ");
        appendCondition(sql, StringUtils.hasText(filter.getEntityType()), "AND at.entityType = :entityType ");

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, PlanRouteFilter filter) {
        setParameterIfNotNull(query, "agentId", filter.getAgentId());
        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());
        setParameterIfNotNull(query, "entityId", filter.getEntityId());

        if (StringUtils.hasText(filter.getEntityType())) {
            query.setParameter("entityType", EntityTypeEnum.valueOf(filter.getEntityType()));
        }
    }
}