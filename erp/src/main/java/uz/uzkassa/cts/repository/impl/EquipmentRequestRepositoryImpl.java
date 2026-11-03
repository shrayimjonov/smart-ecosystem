package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.EquipmentRequest;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.Priority;
import uz.uzkassa.cts.filter.EquipmentRequestFilter;
import uz.uzkassa.cts.repository.custom.EquipmentRequestRepositoryCustom;

/**
 * Created date time: 25.12.2024 14:41
 */

@SuppressWarnings("unused")
public class EquipmentRequestRepositoryImpl extends BaseRepositoryImpl implements EquipmentRequestRepositoryCustom {

    @Override
    public ResultList<EquipmentRequest> getResultList(EquipmentRequestFilter filter) {
        ResultList<EquipmentRequest> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select er from EquipmentRequest er");
        sql.append(" where er.deleted = false");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and er.status = :status");
        }
        if (StringUtils.isNotEmpty(filter.getPriority())) {
            sql.append(" and er.priority = :priority");
        }
        if (filter.getRequestDate() != null) {
            sql.append(" and er.requestDate = :requestDate");
        }
        if (filter.getApproverId() != null) {
            sql.append(" and er.approverId = :approverId");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and er.creatorId = :creatorId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and er.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and er.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" or lower(er.description)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select er", " select count(er.id)");
        sql.append(" order by er.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<EquipmentRequest> query = entityManager
                .createQuery(sql.toString(), EquipmentRequest.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", ApproverCommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", ApproverCommonStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getPriority())) {
            query.setParameter("priority", Priority.valueOf(filter.getPriority()));
            countQuery.setParameter("priority", Priority.valueOf(filter.getPriority()));
        }
        if (filter.getRequestDate() != null) {
            query.setParameter("requestDate", filter.getRequestDate());
            countQuery.setParameter("requestDate", filter.getRequestDate());
        }
        if (filter.getApproverId() != null) {
            query.setParameter("approverId", filter.getApproverId());
            countQuery.setParameter("approverId", filter.getApproverId());
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
