package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.domain.Department;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.DepartmentType;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.DepartmentRepositoryCustom;

public class DepartmentRepositoryImpl extends BaseRepositoryImpl implements DepartmentRepositoryCustom {

    @Override
    public ResultList<Department> getResultList(BaseFilter filter) {
        ResultList<Department> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select d from Department d");
        sql.append(" where d.deleted = false ");
        sql.append(" and d.branchId=:branchId");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and d.status=:status");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and d.type=:type");
        }
        if (filter.hasSearch()) {
            sql.append(" and ( ");
            sql.append(" lower(d.name)").append(filter.getLikeSearchKey());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select d", "select count(d.id)");

        sql.append(" order by d.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Department> query = entityManager
                .createQuery(sql.toString(), Department.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            query.setParameter("type", DepartmentType.valueOf(filter.getType()));
            countQuery.setParameter("type", DepartmentType.valueOf(filter.getType()));
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
