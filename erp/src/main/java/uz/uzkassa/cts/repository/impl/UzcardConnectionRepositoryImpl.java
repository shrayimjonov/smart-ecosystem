package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.UzcardConnection;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.UzcardConnectionStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.UzcardConnectionRepositoryCustom;

@SuppressWarnings("unused")
public class UzcardConnectionRepositoryImpl extends BaseRepositoryImpl implements UzcardConnectionRepositoryCustom {

    @Override
    public ResultList<UzcardConnection> getResultList(BaseFilter filter) {
        ResultList<UzcardConnection> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select uz from UzcardConnection uz ");
        sql.append(" where uz.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and uz.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and uz.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and uz.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(uz.serialNumber) ").append(filter.getLikeSearch());
            sql.append(" or lower(uz.customerBranchName) ").append(filter.getLikeSearch());
            sql.append(" or lower(uz.terminalType) ").append(filter.getLikeSearch());
            sql.append(" or lower(uz.model)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select uz", " select count(uz.id)");
        sql.append(" order by uz.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<UzcardConnection> query = entityManager
                .createQuery(sql.toString(), UzcardConnection.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", UzcardConnectionStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", UzcardConnectionStatus.valueOf(filter.getStatus()));
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
