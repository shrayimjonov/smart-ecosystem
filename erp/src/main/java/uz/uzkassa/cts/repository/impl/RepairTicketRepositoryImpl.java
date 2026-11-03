package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.RepairTicket;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.RepairTicketStatus;
import uz.uzkassa.cts.filter.RepairTicketFilter;
import uz.uzkassa.cts.repository.custom.RepairTicketRepositoryCustom;

public class RepairTicketRepositoryImpl extends BaseRepositoryImpl implements RepairTicketRepositoryCustom {

    @Override
    public ResultList<RepairTicket> getResultList(RepairTicketFilter filter) {
        ResultList<RepairTicket> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select rt from RepairTicket rt ");
        sql.append(" join rt.type t ");
        sql.append(" join rt.device d ");
        if (filter.getMasterId() != null) {
            sql.append(" join rt.master m ");
        }
        sql.append(" where rt.deleted = false ");

        if (filter.getCompanyId() != null) {
            sql.append(" and rt.companyId = :companyId ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and rt.fromBranchId =:branchId ");
        }
        if (filter.getTypeId() != null) {
            sql.append(" and t.id =:typeId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and rt.status =:status ");
        }
        if (filter.getFromStartOfDay() != null) {
            sql.append(" and rt.createdDate >=:from ");
        }
        if (filter.getToEndOfDay() != null) {
            sql.append(" and rt.createdDate <=:to ");
        }
        if (filter.getVisitType() != null) {
            sql.append(" and rt.visitType =:visitType ");
        }
        if (filter.getMasterId() != null) {
            sql.append(" and rt.masterId =:masterId ");
        }
        if (filter.getManagerId() != null) {
            sql.append(" and rt.creatorId =:managerId ");
        }
        if (filter.getManagerId() != null) {
            sql.append(" and rt.creatorId =:managerId ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and lower(d.serialNumber) ").append(filter.getLikeSearch());
        }

        String countSql = sql.toString().replaceFirst("select rt", " select count(rt.id)");
        sql.append(" order by rt.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<RepairTicket> query = entityManager
                .createQuery(sql.toString(), RepairTicket.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getTypeId() != null) {
            query.setParameter("typeId", filter.getTypeId());
            countQuery.setParameter("typeId", filter.getTypeId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", RepairTicketStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", RepairTicketStatus.valueOf(filter.getStatus()));
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        if (filter.getFromStartOfDay() != null) {
            query.setParameter("from", filter.getFromStartOfDay());
            countQuery.setParameter("from", filter.getFromStartOfDay());
        }
        if (filter.getToEndOfDay() != null) {
            query.setParameter("to", filter.getToEndOfDay());
            countQuery.setParameter("to", filter.getToEndOfDay());
        }
        if (filter.getVisitType() != null) {
            query.setParameter("visitType", filter.getVisitType());
            countQuery.setParameter("visitType", filter.getVisitType());
        }
        if (filter.getMasterId() != null) {
            query.setParameter("masterId", filter.getMasterId());
            countQuery.setParameter("masterId", filter.getMasterId());
        }
        if (filter.getManagerId() != null) {
            query.setParameter("managerId", filter.getManagerId());
            countQuery.setParameter("managerId", filter.getManagerId());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
