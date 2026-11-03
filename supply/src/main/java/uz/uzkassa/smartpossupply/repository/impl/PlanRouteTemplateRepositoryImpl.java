package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.planroutetemplate.PlanRouteTemplate;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;
import uz.uzkassa.smartpossupply.repository.PlanRouteTemplateRepositoryCustom;

public class PlanRouteTemplateRepositoryImpl
    extends BaseFilterableRepositoryImpl<PlanRouteTemplate, PlanRouteFilter>
    implements PlanRouteTemplateRepositoryCustom {

    @Override
    protected Class<PlanRouteTemplate> getEntityClass() {
        return PlanRouteTemplate.class;
    }

    @Override
    protected String getEntityAlias() {
        return "prt";
    }

    @Override
    public void applyFilter(PlanRouteFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(PlanRouteFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM PlanRouteTemplate prt ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH prt.agent a ")
                .append("JOIN FETCH prt.branch b ")
                .append("LEFT JOIN FETCH prt.region r ");
        } else {
            sql.append("JOIN prt.agent a ")
                .append("JOIN prt.branch b ")
                .append("LEFT JOIN prt.region r ");
        }

        // Base conditions
        sql.append("WHERE prt.deleted = false ")
            .append("AND b.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getBranchId() != null, "AND prt.branchId = :branchId ");
        appendCondition(sql, filter.getRegionId() != null, "AND prt.regionId = :regionId ");
        appendCondition(sql, filter.getAgentId() != null, "AND a.id = :agentId ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND prt.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND prt.createdDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND prt.createdDate <= :toDate ");

        // Search conditions across multiple fields
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(prt.templateNumber)").append(filter.getSearchKey())
                .append(" OR LOWER(prt.name)").append(filter.getSearchKey())
                .append(" OR LOWER(a.firstName)").append(filter.getSearchKey())
                .append(" OR LOWER(a.lastName)").append(filter.getSearchKey())
                .append(" OR LOWER(a.login)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, PlanRouteFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        setParameterIfNotNull(query, "regionId", filter.getRegionId());
        setParameterIfNotNull(query, "agentId", filter.getAgentId());

        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }

        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
