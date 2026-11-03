package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.WhiteList;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.WhiteListFilter;
import uz.uzkassa.cts.repository.custom.WhiteListRepositoryCustom;

@SuppressWarnings("unused")
public class WhiteListRepositoryImpl extends BaseRepositoryImpl implements WhiteListRepositoryCustom {

    @Override
    public ResultList<WhiteList> getResultList(WhiteListFilter filter) {
        ResultList<WhiteList> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select wl from WhiteList wl ");
        sql.append(" where wl.deleted = false ");

        if (filter.getCustomerId() != null) {
            sql.append(" and wl.customerId = :customerId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and wl.fromDate <= :fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and wl.toDate >= :toDate ");
        }
        if (filter.getAction() != null) {
            sql.append(" and wl.action =:action ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and wl.status = :status ");
        }

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.companyName) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.tin) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.oldTin) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.newTin) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select wl", " select count(wl.id)");
        sql.append(" order by wl.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<WhiteList> query = entityManager
                .createQuery(sql.toString(), WhiteList.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.getAction() != null) {
            query.setParameter("action", filter.getAction());
            countQuery.setParameter("action", filter.getAction());
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
