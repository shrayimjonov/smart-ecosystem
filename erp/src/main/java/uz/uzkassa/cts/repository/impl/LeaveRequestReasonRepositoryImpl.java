package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.LeaveRequestReason;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.LeaveRequestReasonType;
import uz.uzkassa.cts.filter.LeaveRequestReasonFilter;
import uz.uzkassa.cts.repository.custom.LeaveRequestReasonRepositoryCustom;

@SuppressWarnings("unused")
public class LeaveRequestReasonRepositoryImpl extends BaseRepositoryImpl implements LeaveRequestReasonRepositoryCustom {

    @Override
    public ResultList<LeaveRequestReason> getResultList(LeaveRequestReasonFilter filter) {
        ResultList<LeaveRequestReason> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select lrr from LeaveRequestReason lrr ");
        sql.append(" where lrr.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getAbbreviation())) {
            sql.append(" and lrr.abbreviation = :abbreviation ");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and lrr.type = :type ");
        }
        if (StringUtils.isNotEmpty(filter.getColor())) {
            sql.append(" and lrr.color = :color ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(lrr.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" or lower(lrr.nameUzCyrillic) ").append(filter.getLikeSearch());
            sql.append(" or lower(lrr.nameRu) ").append(filter.getLikeSearch());
            sql.append(" or lower(lrr.abbreviation) ").append(filter.getLikeSearch());
            sql.append(" or lower(lrr.description) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select lrr", " select count(lrr.id)");
        sql.append(" order by lrr.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<LeaveRequestReason> query = entityManager
                .createQuery(sql.toString(), LeaveRequestReason.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getAbbreviation())) {
            query.setParameter("abbreviation", filter.getAbbreviation());
            countQuery.setParameter("abbreviation", filter.getAbbreviation());
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            query.setParameter("type", LeaveRequestReasonType.valueOf(filter.getType()));
            countQuery.setParameter("type", LeaveRequestReasonType.valueOf(filter.getType()));
        }
        if (StringUtils.isNotEmpty(filter.getColor())) {
            query.setParameter("color", filter.getColor());
            countQuery.setParameter("color", filter.getColor());
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
