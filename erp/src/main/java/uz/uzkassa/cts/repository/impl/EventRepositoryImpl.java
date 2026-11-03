package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.Event;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.EventStatus;
import uz.uzkassa.cts.filter.EventFilter;
import uz.uzkassa.cts.repository.custom.EventRepositoryCustom;

/**
 * Created date time: 26.12.2024 16:56
 */

@SuppressWarnings("unused")
public class EventRepositoryImpl extends BaseRepositoryImpl implements EventRepositoryCustom {

    @Override
    public ResultList<Event> getResultList(EventFilter filter) {
        ResultList<Event> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select e from Event e");
        sql.append(" where e.deleted = false");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and e.status = :status");
        }
        if (filter.getEventRequestId() != null) {
            sql.append(" and e.eventRequestId = :eventRequestId");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and e.creatorId = :creatorId");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(e.subject)").append(filter.getLikeSearch());
            sql.append(" or lower(e.description)").append(filter.getLikeSearch());
            sql.append(" or lower(e.guest)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select e", " select count(e.id)");
        sql.append(" order by e.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Event> query = entityManager
                .createQuery(sql.toString(), Event.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", EventStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", EventStatus.valueOf(filter.getStatus()));
        }
        if (filter.getEventRequestId() != null) {
            query.setParameter("eventRequestId", filter.getEventRequestId());
            countQuery.setParameter("eventRequestId", filter.getEventRequestId());
        }
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
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
