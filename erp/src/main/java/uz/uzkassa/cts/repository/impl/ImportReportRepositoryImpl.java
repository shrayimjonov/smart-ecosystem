package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.exportimport.ImportReport;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.ExportReportStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.ImportReportRepositoryCustom;

public class ImportReportRepositoryImpl extends BaseRepositoryImpl implements ImportReportRepositoryCustom {

    @Override
    public ResultList<ImportReport> getResultList(BaseFilter filter) {
        ResultList<ImportReport> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select i from ImportReport i ");
        sql.append(" where i.deleted = false ");
        if (!isSuperAdmin()) {
            sql.append(" and i.creatorId =:creatorId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and i.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and i.createdDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and i.createdDate <=:toDate");
        }
        String countSql = sql.toString().replaceFirst("select i", "select count(i.id)");

        sql.append(" order by i.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<ImportReport> query = entityManager
                .createQuery(sql.toString(), ImportReport.class)
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
