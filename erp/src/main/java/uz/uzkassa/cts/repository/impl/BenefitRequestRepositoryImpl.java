package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.BenefitRequest;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.Priority;
import uz.uzkassa.cts.filter.BenefitRequestFilter;
import uz.uzkassa.cts.repository.custom.BenefitRequestRepositoryCustom;

/**
 * Created date time: 23.12.2024 18:02
 */

@SuppressWarnings("unused")
public class BenefitRequestRepositoryImpl extends BaseRepositoryImpl implements BenefitRequestRepositoryCustom {

    @Override
    public ResultList<BenefitRequest> getResultList(BenefitRequestFilter filter) {
        ResultList<BenefitRequest> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select br from BenefitRequest br");
        sql.append(" where br.deleted = false");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and br.status = :status");
        }
        if (StringUtils.isNotEmpty(filter.getPriority())) {
            sql.append(" and br.priority = :priority");
        }
        if (filter.getRequestDate() != null) {
            sql.append(" and br.requestDate = :requestDate");
        }
        if (filter.getApproverId() != null) {
            sql.append(" and br.approverId = :approverId");
        }
        if (filter.getTypeId() != null) {
            sql.append(" and br.typeId = :typeId");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and br.creatorId = :creatorId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and br.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and br.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" or lower(br.description)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select br", " select count(br.id)");
        sql.append(" order by br.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<BenefitRequest> query = entityManager
                .createQuery(sql.toString(), BenefitRequest.class)
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
        if (filter.getTypeId() != null) {
            query.setParameter("typeId", filter.getTypeId());
            countQuery.setParameter("typeId", filter.getTypeId());
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
