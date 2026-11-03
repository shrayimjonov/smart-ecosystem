package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.Dependent;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.Gender;
import uz.uzkassa.cts.filter.DependentFilter;
import uz.uzkassa.cts.repository.custom.DependentRepositoryCustom;

@SuppressWarnings("unused")
public class DependentRepositoryImpl extends BaseRepositoryImpl implements DependentRepositoryCustom {

    @Override
    public ResultList<Dependent> getResultList(DependentFilter filter) {
        ResultList<Dependent> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select d from Dependent d ");
        sql.append(" join d.employee e");
        sql.append(" where d.deleted = false ");
        if (filter.getBranchId() != null) {
            sql.append(" and e.branchId =:branchId ");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and e.companyId =:companyId ");
        }
        if (filter.getPositionId() != null) {
            sql.append(" and e.positionId =:positionId ");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and e.departmentId =:departmentId ");
        }
        if (filter.getEmployeeId() != null) {
            sql.append(" and d.employeeId =:employeeId ");
        }
        if (filter.getRelationshipId() != null) {
            sql.append(" and d.relationshipId =:relationshipId ");
        }
        if (StringUtils.isNotEmpty(filter.getGender())) {
            sql.append(" and d.gender =:gender ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(d.firstName) ").append(filter.getLikeSearch());
            sql.append(" or lower(d.lastName) ").append(filter.getLikeSearch());
            sql.append(" or lower(d.patronymic) ").append(filter.getLikeSearch());
            sql.append(" or lower(e.firstName) ").append(filter.getLikeSearch());
            sql.append(" or lower(e.lastName) ").append(filter.getLikeSearch());
            sql.append(" or lower(e.patronymic) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select d", " select count(d.id)");
        sql.append(" order by d.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Dependent> query = entityManager
                .createQuery(sql.toString(), Dependent.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getPositionId() != null) {
            query.setParameter("positionId", filter.getPositionId());
            countQuery.setParameter("positionId", filter.getPositionId());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getEmployeeId() != null) {
            query.setParameter("employeeId", filter.getEmployeeId());
            countQuery.setParameter("employeeId", filter.getEmployeeId());
        }
        if (filter.getRelationshipId() != null) {
            query.setParameter("relationshipId", filter.getRelationshipId());
            countQuery.setParameter("relationshipId", filter.getRelationshipId());
        }
        if (StringUtils.isNotEmpty(filter.getGender())) {
            query.setParameter("gender", Gender.valueOf(filter.getGender()));
            countQuery.setParameter("gender", Gender.valueOf(filter.getGender()));
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
