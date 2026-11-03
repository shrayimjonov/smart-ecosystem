package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.Lead;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CallStatus;
import uz.uzkassa.cts.enums.LeadPaymentStatus;
import uz.uzkassa.cts.enums.LeadState;
import uz.uzkassa.cts.enums.LeadStatus;
import uz.uzkassa.cts.filter.LeadFilter;
import uz.uzkassa.cts.repository.custom.LeadRepositoryCustom;

@SuppressWarnings("unused")
public class LeadRepositoryImpl extends BaseRepositoryImpl implements LeadRepositoryCustom {

    @Override
    public ResultList<Lead> getResultList(LeadFilter filter) {
        ResultList<Lead> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select l from Lead l ");
        sql.append(" where l.deleted = false ");

        if (filter.getFrom() != null) {
            sql.append(" and l.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and l.createdDate <= :toDate");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and l.status =:status ");
        }
        if (StringUtils.isNotEmpty(filter.getCallStatus())) {
            sql.append(" and l.callStatus = :callStatus");
        }
        if (StringUtils.isNotEmpty(filter.getState())) {
            sql.append(" and l.state = :state");
        }
        if (StringUtils.isNotEmpty(filter.getPaymentStatus())) {
            sql.append(" and l.paymentStatus = :paymentStatus");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(l.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(l.companyName) ").append(filter.getLikeSearch());
            sql.append(" or lower(l.phone) ").append(filter.getLikeSearch());
            sql.append(" or lower(l.source) ").append(filter.getLikeSearch());
            sql.append(" or lower(l.tin) ").append(filter.getLikeSearch());
            sql.append(" or lower(l.utmTerm) ").append(filter.getLikeSearch());
            sql.append(" or lower(l.utmMedium) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select l", " select count(l.id)");
        sql.append(" order by l.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Lead> query = entityManager
                .createQuery(sql.toString(), Lead.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", LeadStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", LeadStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getCallStatus())) {
            query.setParameter("callStatus", CallStatus.valueOf(filter.getCallStatus()));
            countQuery.setParameter("callStatus", CallStatus.valueOf(filter.getCallStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getState())) {
            query.setParameter("state", LeadState.valueOf(filter.getState()));
            countQuery.setParameter("state", LeadState.valueOf(filter.getState()));
        }
        if (StringUtils.isNotEmpty(filter.getPaymentStatus())) {
            query.setParameter("paymentStatus", LeadPaymentStatus.valueOf(filter.getPaymentStatus()));
            countQuery.setParameter("paymentStatus", LeadPaymentStatus.valueOf(filter.getPaymentStatus()));
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
