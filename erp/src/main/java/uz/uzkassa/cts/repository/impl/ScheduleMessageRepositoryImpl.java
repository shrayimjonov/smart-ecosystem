package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.ScheduleMessage;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.ChannelType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.ScheduleMessageRepositoryCustom;

@SuppressWarnings("unused")
public class ScheduleMessageRepositoryImpl extends BaseRepositoryImpl implements ScheduleMessageRepositoryCustom {

    @Override
    public ResultList<ScheduleMessage> getResultList(BaseFilter filter) {
        ResultList<ScheduleMessage> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select sm from ScheduleMessage sm ");
        sql.append(" where sm.deleted = false ");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and sm.status =:status ");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and sm.channelType =:channelType ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and sm.scheduleDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and sm.scheduleDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(sm.subject) ").append(filter.getLikeSearch());
            sql.append(" or lower(sm.description) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select sm", " select count(sm.id)");
        sql.append(" order by sm.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<ScheduleMessage> query = entityManager
                .createQuery(sql.toString(), ScheduleMessage.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            query.setParameter("channelType", ChannelType.valueOf(filter.getType()));
            countQuery.setParameter("channelType", ChannelType.valueOf(filter.getType()));
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
