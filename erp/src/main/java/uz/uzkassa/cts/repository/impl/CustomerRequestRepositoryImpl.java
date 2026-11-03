package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.customer.CustomerRequest;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CallStatus;
import uz.uzkassa.cts.enums.CustomerRequestStatus;
import uz.uzkassa.cts.enums.CustomerRequestType;
import uz.uzkassa.cts.filter.CustomerRequestFilter;
import uz.uzkassa.cts.repository.custom.CustomerRequestRepositoryCustom;

import java.time.LocalTime;

@SuppressWarnings("unused")
public class CustomerRequestRepositoryImpl extends BaseRepositoryImpl implements CustomerRequestRepositoryCustom {

    @Override
    public ResultList<CustomerRequest> getResultList(CustomerRequestFilter filter) {
        ResultList<CustomerRequest> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cr from CustomerRequest cr ");
        sql.append(" inner join cr.customer c ");
        sql.append(" where cr.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cr.status =:status ");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and cr.customerId =:customerId ");
        }
        if (CollectionUtils.isNotEmpty(filter.getEmployeeIds())) {
            sql.append(" and cr.lastModifierId in (:employeeIds) ");
        }
        if (filter.getType() != null) {
            sql.append(" and cr.requestType =:requestType ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and cr.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cr.createdDate <= :toDate");
        }
        if (filter.getLastFrom() != null) {
            sql.append(" and cr.lastModifiedDate >= :lastFromDate");
        }
        if (filter.getLastTo() != null) {
            sql.append(" and cr.lastModifiedDate <= :lastToDate");
        }
        if (StringUtils.isNotEmpty(filter.getCallStatus())) {
            sql.append(" and cr.callStatus = :callStatus");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.tin) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.phone) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select cr", " select count(cr.id)");
        sql.append(" order by cr.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CustomerRequest> query = entityManager
                .createQuery(sql.toString(), CustomerRequest.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CustomerRequestStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CustomerRequestStatus.valueOf(filter.getStatus()));
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getType() != null) {
            query.setParameter("requestType", CustomerRequestType.valueOf(filter.getType()));
            countQuery.setParameter("requestType", CustomerRequestType.valueOf(filter.getType()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.getLastFrom() != null) {
            query.setParameter("lastFromDate", filter.getLastFrom().atStartOfDay());
            countQuery.setParameter("lastFromDate", filter.getLastFrom().atStartOfDay());
        }
        if (filter.getLastTo() != null) {
            query.setParameter("lastToDate", filter.getLastTo().atTime(LocalTime.MAX));
            countQuery.setParameter("lastToDate", filter.getLastTo().atTime(LocalTime.MAX));
        }
        if (CollectionUtils.isNotEmpty(filter.getEmployeeIds())) {
            query.setParameter("employeeIds", filter.getEmployeeIds());
            countQuery.setParameter("employeeIds", filter.getEmployeeIds());
        }
        if (StringUtils.isNotEmpty(filter.getCallStatus())) {
            query.setParameter("callStatus", CallStatus.valueOf(filter.getCallStatus()));
            countQuery.setParameter("callStatus", CallStatus.valueOf(filter.getCallStatus()));
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
