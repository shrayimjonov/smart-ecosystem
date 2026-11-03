package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.CaseFilter;
import uz.uzkassa.cts.repository.custom.CaseRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;

@SuppressWarnings("unused")
public class CaseRepositoryImpl extends BaseRepositoryImpl implements CaseRepositoryCustom {

    @Override
    public ResultList<Cases> getResultList(CaseFilter filter) {
        ResultList<Cases> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select c from Cases c");
        sql.append(" join c.creator u");
        sql.append(" join c.customer cus");
        sql.append(" where c.deleted = false");

        if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(), Role.ACCOUNTANT.getRoleCode())) {
            if (filter.getBranchId() != null) {
                sql.append(" and u.branchId = :branchId");
            }
        } else {
            sql.append(" and u.branchId = :branchId");
        }
        if (filter.getId() != null) {
            sql.append(" and c.id =:id");
        }
        if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
            sql.append(" and c.status in (:statuses)");
        }
        if (CollectionUtils.isNotEmpty(filter.getCaseTypes())) {
            sql.append(" and c.caseType in (:caseTypes)");
        }
        if (CollectionUtils.isNotEmpty(filter.getCreatorIds())) {
            sql.append(" and u.id in (:creatorIds)");
        }
        if (CollectionUtils.isNotEmpty(filter.getAdminCaseTypes())) {
            sql.append(" and c.caseType in (:adminCaseTypes)");
        }
        if (filter.getFrom() != null) {
            sql.append(" and c.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and c.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cus.name)").append(filter.getLikeSearch());
            sql.append(" or lower(cus.companyName)").append(filter.getLikeSearch());
            sql.append(" or lower(cus.tin)").append(filter.getLikeSearch());
            sql.append(" or lower(c.jsonProperties)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select c", "select count(c.id)");

        sql.append(" order by c.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Cases> query = entityManager.createQuery(sql.toString(), Cases.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(), Role.ACCOUNTANT.getRoleCode())) {
            if (filter.getBranchId() != null) {
                query.setParameter("branchId", filter.getBranchId());
                countQuery.setParameter("branchId", filter.getBranchId());
            }
        } else {
            query.setParameter("branchId", SecurityUtils.getBranchId());
            countQuery.setParameter("branchId", SecurityUtils.getBranchId());
        }
        if (filter.getId() != null) {
            query.setParameter("id", filter.getId());
            countQuery.setParameter("id", filter.getId());
        }
        if (CollectionUtils.isNotEmpty(filter.getStatuses())) {
            query.setParameter("statuses", filter.getStatuses());
            countQuery.setParameter("statuses", filter.getStatuses());
        }
        if (CollectionUtils.isNotEmpty(filter.getCaseTypes())) {
            query.setParameter("caseTypes", filter.getCaseTypes());
            countQuery.setParameter("caseTypes", filter.getCaseTypes());
        }
        if (CollectionUtils.isNotEmpty(filter.getCreatorIds())) {
            query.setParameter("creatorIds", filter.getCreatorIds());
            countQuery.setParameter("creatorIds", filter.getCreatorIds());
        }
        if (CollectionUtils.isNotEmpty(filter.getAdminCaseTypes())) {
            query.setParameter("adminCaseTypes", filter.getAdminCaseTypes());
            countQuery.setParameter("adminCaseTypes", filter.getAdminCaseTypes());
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
