package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.hrms.Groups;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.GroupRepositoryCustom;

@SuppressWarnings("unused")
public class GroupRepositoryImpl extends BaseRepositoryImpl implements GroupRepositoryCustom {

    @Override
    public ResultList<Groups> getResultList(BaseFilter filter) {
        ResultList<Groups> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select g from Groups g ");
        sql.append(" join g.department d ");
        sql.append(" where g.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and g.status =:status");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and d.branch.companyId =:companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and d.branchId =:branchId");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(g.name) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select g", " select count(g.id)");
        sql.append(" order by g.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Groups> query = entityManager
                .createQuery(sql.toString(), Groups.class)
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
