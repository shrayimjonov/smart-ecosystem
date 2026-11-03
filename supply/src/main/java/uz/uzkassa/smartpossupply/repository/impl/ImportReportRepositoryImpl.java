package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.exportimport.ImportReport;
import uz.uzkassa.smartpossupply.enums.ReportStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.ImportReportRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.03.2023
 */
public class ImportReportRepositoryImpl extends BaseFilterableRepositoryImpl<ImportReport, BaseFilter> implements ImportReportRepositoryCustom {

    @Override
    protected Class<ImportReport> getEntityClass() {
        return ImportReport.class;
    }

    @Override
    protected String getEntityAlias() {
        return "i";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM ImportReport i ");

        if (includeFetch) {
            sql.append("LEFT JOIN FETCH i.file f ");
        } else {
            sql.append("LEFT JOIN i.file f ");
        }

        sql.append("WHERE i.deleted = false ");
        sql.append("AND i.companyId = :companyId ");

        appendCondition(sql, SecurityUtils.getCurrentUserId() != null, "AND i.creatorId =:creatorId ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND i.status =:status ");
        appendCondition(sql, filter.getFrom() != null, "AND i.createdDate >=:fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND i.createdDate <=:toDate ");

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
