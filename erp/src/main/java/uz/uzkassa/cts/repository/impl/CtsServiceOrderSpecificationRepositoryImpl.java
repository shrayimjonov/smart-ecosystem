package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.CtsServiceOrderSpecification;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CtsServiceOrderSpecificationRepositoryCustom;

public class CtsServiceOrderSpecificationRepositoryImpl extends BaseRepositoryImpl implements CtsServiceOrderSpecificationRepositoryCustom {

    @Override
    public ResultList<CtsServiceOrderSpecification> getResultList(BaseFilter filter) {
        ResultList<CtsServiceOrderSpecification> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select sos from CtsServiceOrderSpecification sos ");
        sql.append(" join sos.contract con ");
        sql.append(" join con.customer c ");
        sql.append(" where sos.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and sos.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and sos.createdDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and sos.createdDate <=:toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(con.companyName) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.tin) ").append(filter.getLikeSearch());
            sql.append(" or lower(con.name) ").append(filter.getLikeSearch());
            if (StringUtils.isNumeric(filter.getSearch())) {
                sql.append(" or sos.id = ").append(filter.getSearch());
            }
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select sos", " select count(sos.id)");
        sql.append(" order by sos.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CtsServiceOrderSpecification> query = entityManager
                .createQuery(sql.toString(), CtsServiceOrderSpecification.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.get(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.get(filter.getStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
