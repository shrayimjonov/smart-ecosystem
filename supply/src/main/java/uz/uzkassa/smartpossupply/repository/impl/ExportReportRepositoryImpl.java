package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.exportimport.ExportReport;
import uz.uzkassa.smartpossupply.enums.ReportStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.ExportReportRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

/**
 * Powered by: Shuxratjon Rayimjonov
 */
public class ExportReportRepositoryImpl extends BaseFilterableRepositoryImpl<ExportReport, BaseFilter> implements ExportReportRepositoryCustom {

    @Override
    protected Class<ExportReport> getEntityClass() {
        return ExportReport.class;
    }

    @Override
    protected String getEntityAlias() {
        return "e";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM ExportReport e ");

        if (includeFetch) {
            sql.append("LEFT JOIN FETCH e.file f ");
        } else {
            sql.append("LEFT JOIN e.file f ");
        }

        sql.append("WHERE e.deleted = false ");
        sql.append("AND e.companyId = :companyId ");

        appendCondition(sql, SecurityUtils.getCurrentUserId() != null, "AND e.creatorId =:creatorId ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND e.status =:status ");
        appendCondition(sql, filter.getFrom() != null, "AND e.createdDate >=:fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND e.createdDate <=:toDate ");

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        query.setParameter("companyId", filter.getCompanyId());
        setParameterIfNotNull(query, "creatorId", SecurityUtils.getCurrentUserId());
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", ReportStatus.valueOf(filter.getStatus()));
        }
        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());
    }
}
