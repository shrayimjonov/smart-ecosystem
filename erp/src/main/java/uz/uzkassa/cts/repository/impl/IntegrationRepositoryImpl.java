package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.Integration;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.IntegrationRepositoryCustom;

@SuppressWarnings("unused")
public class IntegrationRepositoryImpl extends BaseRepositoryImpl implements IntegrationRepositoryCustom {

    @Override
    public ResultList<Integration> getResultList(BaseFilter filter) {
        ResultList<Integration> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select i from Integration i ");
        sql.append(" where i.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and i.type = :type ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and i.status = :status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and i.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and i.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(i.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(i.description) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select i", "select count(i.id)");

        sql.append(" order by i.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Integration> query = entityManager.createQuery(sql.toString(), Integration.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("type", IntegrationType.valueOf(filter.getType()));
            countQuery.setParameter("type", IntegrationType.valueOf(filter.getType()));
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
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
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
