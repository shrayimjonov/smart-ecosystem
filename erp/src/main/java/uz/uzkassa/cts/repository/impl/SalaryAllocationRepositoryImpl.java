package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.hrms.SalaryAllocation;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.SalaryAllocationFilter;
import uz.uzkassa.cts.repository.custom.SalaryAllocationRepositoryCustom;

@SuppressWarnings("unused")
public class SalaryAllocationRepositoryImpl extends BaseRepositoryImpl implements SalaryAllocationRepositoryCustom {

    @Override
    public ResultList<SalaryAllocation> getResultList(SalaryAllocationFilter filter) {
        ResultList<SalaryAllocation> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select sa from SalaryAllocation sa");
        sql.append(" where sa.deleted = false");

        if (filter.getDepartmentId() != null) {
            sql.append(" and sa.departmentId = :departmentId");
        }
        if (filter.getMinSalaryTotalAmount() != null) {
            sql.append(" and sa.minSalaryTotalAmount >= :minSalaryTotalAmount");
        }
        if (filter.getMaxSalaryTotalAmount() != null) {
            sql.append(" and sa.maxSalaryTotalAmount <= :maxSalaryTotalAmount");
        }
        if (filter.getMinEmployeeTotalCount() != null) {
            sql.append(" and sa.minEmployeeTotalCount >= :minEmployeeTotalCount");
        }
        if (filter.getMaxEmployeeTotalCount() != null) {
            sql.append(" and sa.maxEmployeeTotalCount <= :maxEmployeeTotalCount");
        }

        String countSql = sql.toString().replaceFirst("select sa", "select count(sa) ");
        sql.append(" order by sa.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder());

        TypedQuery<SalaryAllocation> query = entityManager
                .createQuery(sql.toString(), SalaryAllocation.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getMinSalaryTotalAmount() != null) {
            query.setParameter("minSalaryTotalAmount", filter.getMinSalaryTotalAmount());
            countQuery.setParameter("minSalaryTotalAmount", filter.getMinSalaryTotalAmount());
        }
        if (filter.getMaxSalaryTotalAmount() != null) {
            query.setParameter("maxSalaryTotalAmount", filter.getMaxSalaryTotalAmount());
            countQuery.setParameter("maxSalaryTotalAmount", filter.getMaxSalaryTotalAmount());
        }
        if (filter.getMinEmployeeTotalCount() != null) {
            query.setParameter("minEmployeeTotalCount", filter.getMinEmployeeTotalCount());
            countQuery.setParameter("minEmployeeTotalCount", filter.getMinEmployeeTotalCount());
        }
        if (filter.getMaxEmployeeTotalCount() != null) {
            query.setParameter("maxEmployeeTotalCount", filter.getMaxEmployeeTotalCount());
            countQuery.setParameter("maxEmployeeTotalCount", filter.getMaxEmployeeTotalCount());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
