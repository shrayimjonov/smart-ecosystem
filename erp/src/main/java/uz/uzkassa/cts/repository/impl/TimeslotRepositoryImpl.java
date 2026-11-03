package uz.uzkassa.cts.repository.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.timeslot.Timeslot;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.TimeslotType;
import uz.uzkassa.cts.filter.TimeslotFilter;
import uz.uzkassa.cts.repository.custom.TimeslotRepositoryCustom;

@SuppressWarnings("unused")
public class TimeslotRepositoryImpl extends BaseRepositoryImpl implements TimeslotRepositoryCustom {

    @Override
    public ResultList<Timeslot> getResultList(TimeslotFilter filter) {
        ResultList<Timeslot> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select t from Timeslot t");
        sql.append(" where t.deleted = false");

        if (filter.isCompanyTimeslot()) {
            sql.append(" and t.entityId is null ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and t.status = :status");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and t.type = :type");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and t.creatorId = :creatorId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and t.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and t.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(t.name)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select t", " select count(t.id)");
        sql.append(" order by t.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Timeslot> query = entityManager.createQuery(sql.toString(), Timeslot.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            query.setParameter("type", TimeslotType.valueOf(filter.getType()));
            countQuery.setParameter("type", TimeslotType.valueOf(filter.getType()));
        }
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
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
