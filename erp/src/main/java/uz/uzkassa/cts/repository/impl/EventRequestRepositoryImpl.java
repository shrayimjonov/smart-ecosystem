package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.EventRequest;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.filter.EventRequestFilter;
import uz.uzkassa.cts.repository.custom.EventRequestRepositoryCustom;

/**
 * Created date time: 26.12.2024 11:45
 */

@SuppressWarnings("unused")
public class EventRequestRepositoryImpl extends BaseRepositoryImpl implements EventRequestRepositoryCustom {

    @Override
    public ResultList<EventRequest> getResultList(EventRequestFilter filter) {
        ResultList<EventRequest> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select er from EventRequest er");
        sql.append(" where er.deleted = false");

        if (filter.getCompanyId() != null) {
            sql.append(" and er.companyId = :companyId");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and er.status = :status");
        }
        if (filter.getApproverId() != null) {
            sql.append(" and er.approverId = :approverId");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and er.creatorId = :creatorId");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" or lower(er.room)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select er", " select count(er.id)");
        sql.append(" order by er.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<EventRequest> query = entityManager
                .createQuery(sql.toString(), EventRequest.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", ApproverCommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", ApproverCommonStatus.valueOf(filter.getStatus()));
        }
        if (filter.getApproverId() != null) {
            query.setParameter("approverId", filter.getApproverId());
            countQuery.setParameter("approverId", filter.getApproverId());
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
