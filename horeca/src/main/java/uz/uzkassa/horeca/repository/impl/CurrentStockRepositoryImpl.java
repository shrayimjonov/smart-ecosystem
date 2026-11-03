package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.stock.CurrentStock;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.MenuItemFilter;
import uz.uzkassa.horeca.repository.CurrentStockRepositoryCustom;

import java.util.Collections;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 10.11.2024
 */
public class CurrentStockRepositoryImpl extends BaseRepositoryImpl implements CurrentStockRepositoryCustom {

    @Override
    public ResultList<CurrentStock> getResultList(MenuItemFilter filter) {
        filter.setDepartmentId(filter.getDepartmentId());
        ResultList<CurrentStock> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select cs ");
        sql.append(" from CurrentStock cs ");
        sql.append(" join cs.menuItem mi");
        sql.append(" join mi.product p");
        sql.append(" join p.branch b");
        sql.append(" join mi.menu m");
        sql.append(" where cs.deleted=false and mi.deleted = false ");
        sql.append(" and b.companyId=:companyId");

        if (filter.getBranchId() != null) {
            sql.append(" and b.id=:branchId");
        }
        if (filter.getMenuId() != null) {
            sql.append(" and m.id=:menuId");
        } else if (!CollectionUtils.isEmpty(filter.getMenuIdList())) {
            sql.append(" and m.id in (:menuIdList)");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and p.departmentId=:departmentId");
        } else if (!CollectionUtils.isEmpty(filter.getDepartmentIdList())) {
            sql.append(" and p.departmentId in (:departmentIdList)");
        }
        if (filter.getUnitId() != null) {
            sql.append(" and cs.unitId=:unitId");
        }
        if (filter.hasSearch()) {
            sql.append(" and ( ");
            sql.append(" lower(mi.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(m.name)").append(filter.getLikeSearchKey());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select cs", "select count(cs.id)");

        sql.append(" order by cs.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CurrentStock> query = entityManager
                .createQuery(sql.toString(), CurrentStock.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }

        if (filter.getMenuId() != null) {
            query.setParameter("menuId", filter.getMenuId());
            countQuery.setParameter("menuId", filter.getMenuId());
        } else if (!CollectionUtils.isEmpty(filter.getMenuIdList())) {
            query.setParameter("menuIdList", Collections.emptyList());
            countQuery.setParameter("menuIdList", Collections.emptyList());
        }

        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        } else if (!CollectionUtils.isEmpty(filter.getDepartmentIdList())) {
            query.setParameter("departmentIdList", filter.getDepartmentIdList());
            countQuery.setParameter("departmentIdList", filter.getDepartmentIdList());
        }

        if (filter.getUnitId() != null) {
            query.setParameter("unitId", filter.getUnitId());
            countQuery.setParameter("unitId", filter.getUnitId());
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
