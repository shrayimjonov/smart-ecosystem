package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.CaseStatusLog;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CaseStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CaseStatusLogRepositoryCustom;


@SuppressWarnings("unused")
public class CaseStatusLogRepositoryImpl extends BaseRepositoryImpl implements CaseStatusLogRepositoryCustom {


    @Override
    public ResultList<CaseStatusLog> getResultList(BaseFilter filter) {
        ResultList<CaseStatusLog> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select csl from CaseStatusLog csl ");
        sql.append(" where csl.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and csl.status = :status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and csl.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and csl.createdDate <= :toDate");
        }

        String countSql = sql.toString().replaceFirst("select csl", " select count(csl.id)");
        sql.append(" order by csl.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CaseStatusLog> query = entityManager
                .createQuery(sql.toString(), CaseStatusLog.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CaseStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CaseStatus.valueOf(filter.getStatus()));
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
