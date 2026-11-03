package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.DepartmentRepositoryCustom;

@SuppressWarnings("unused")
public class DepartmentRepositoryImpl extends BaseRepositoryImpl implements DepartmentRepositoryCustom {

    @Override
    public ResultList<Department> getResultList(BaseFilter filter) {
        ResultList<Department> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select d from Department d ");
        sql.append(" where d.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and d.status =:status");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and d.branch.companyId =:companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and d.branchId =:branchId");
        }
        if (filter.getParentId() != null) {
            sql.append(" and d.parentId =:parentId");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(d.nameUzLatin) ").append(filter.getLikeSearch());
            sql.append(" or lower(d.nameUzCyrillic) ").append(filter.getLikeSearch());
            sql.append(" or lower(d.nameRu) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select d", " select count(d.id)");
        sql.append(" order by d.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Department> query = entityManager
                .createQuery(sql.toString(), Department.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getParentId() != null) {
            query.setParameter("parentId", filter.getParentId());
            countQuery.setParameter("parentId", filter.getParentId());
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
