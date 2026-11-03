package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.exportimport.ExportReport;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.ExportReportStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.ExportReportRepositoryCustom;

@SuppressWarnings("unused")
public class ExportReportRepositoryImpl extends BaseRepositoryImpl implements ExportReportRepositoryCustom {

    @Override
    public ResultList<ExportReport> getResultList(BaseFilter filter) {
        ResultList<ExportReport> resultList = new ResultList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("select er from ExportReport er ");
        sql.append(" where er.deleted = false ");
        if (!isSuperAdmin()) {
            sql.append(" and er.creatorId =:creatorId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and er.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and er.createdDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and er.createdDate <=:toDate");
        }

        String countSql = sql.toString().replaceFirst("select er", " select count(er.id)");
        sql.append(" order by er.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<ExportReport> query = entityManager
                .createQuery(sql.toString(), ExportReport.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (!isSuperAdmin()) {
            query.setParameter("creatorId", getCurrentUserId());
            countQuery.setParameter("creatorId", getCurrentUserId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", ExportReportStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", ExportReportStatus.valueOf(filter.getStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
