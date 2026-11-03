package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.hrms.LeaveAllowance;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.LeaveAllowanceFilter;
import uz.uzkassa.cts.repository.custom.LeaveAllowanceRepositoryCustom;

@SuppressWarnings("unused")
public class LeaveAllowanceRepositoryImpl extends BaseRepositoryImpl implements LeaveAllowanceRepositoryCustom {

    @Override
    public ResultList<LeaveAllowance> getResultList(LeaveAllowanceFilter filter) {
        ResultList<LeaveAllowance> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select la from LeaveAllowance la ");
        sql.append(" join la.employee e ");
        sql.append(" where la.deleted = false");
        sql.append(" and la.year = :year");
        if (filter.getEmployeeId() != null) {
            sql.append(" and e.id = :employeeId");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and e.departmentId = :departmentId");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append("  lower(e.firstName) ").append(filter.getLikeSearch());
            sql.append(" or lower(e.lastName) ").append(filter.getLikeSearch());
            sql.append(" or lower(e.patronymic) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select la", " select count(la.id)");
        sql.append(" order by e.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<LeaveAllowance> query = entityManager
                .createQuery(sql.toString(), LeaveAllowance.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("year", filter.getYear());
        countQuery.setParameter("year", filter.getYear());

        if (filter.getEmployeeId() != null) {
            query.setParameter("employeeId", filter.getEmployeeId());
            countQuery.setParameter("employeeId", filter.getEmployeeId());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
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
