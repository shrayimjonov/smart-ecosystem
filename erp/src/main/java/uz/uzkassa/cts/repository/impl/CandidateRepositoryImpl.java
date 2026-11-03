package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.Candidate;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CandidateStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CandidateRepositoryCustom;

@SuppressWarnings("unused")
public class CandidateRepositoryImpl extends BaseRepositoryImpl implements CandidateRepositoryCustom {

    @Override
    public ResultList<Candidate> getResultList(BaseFilter filter) {
        ResultList<Candidate> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select c from Candidate c ");
        sql.append(" where c.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and c.status =:status ");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and c.department.branch.companyId =:companyId ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and c.department.branchId =:branchId ");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and c.departmentId =:departmentId ");
        }
        if (filter.getPositionId() != null) {
            sql.append(" and c.positionId =:positionId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and c.createdDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and c.createdDate<=:toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.firstName) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.lastName) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.patronymic) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.phone) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select c", " select count(c.id)");
        sql.append(" order by c.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Candidate> query = entityManager
                .createQuery(sql.toString(), Candidate.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CandidateStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CandidateStatus.valueOf(filter.getStatus()));
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getPositionId() != null) {
            query.setParameter("positionId", filter.getPositionId());
            countQuery.setParameter("positionId", filter.getPositionId());
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
