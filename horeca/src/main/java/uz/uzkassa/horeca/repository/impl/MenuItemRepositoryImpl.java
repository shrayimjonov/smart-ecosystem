package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.domain.unit.MultiUnit;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.MenuItemFilter;
import uz.uzkassa.horeca.repository.MenuItemRepositoryCustom;

public class MenuItemRepositoryImpl extends BaseRepositoryImpl implements MenuItemRepositoryCustom {

    @Override
    public ResultList<MenuItem> getResultList(MenuItemFilter filter) {
        ResultList<MenuItem> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select mi ");
        sql.append(" from MenuItem mi ");
        sql.append(" join mi.menu m");
        sql.append(" where m.deleted = false ");
        sql.append(" and mi.deleted = false ");
        sql.append(" and m.branchId=:branchId");
        if (filter.getMenuId() != null) {
            sql.append(" and (m.id=:menuId ");
            sql.append(" or mi.menu.parentId in (select id from Menu m where m.parentId=:menuId)");
            sql.append(" or mi.menu.parentId in (select id from Menu m where m.id=:menuId))");
        }

        if (filter.hasSearch()) {
            sql.append(" and ( ");
            sql.append(" lower(m.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(mi.name)").append(filter.getLikeSearchKey());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select mi", "select count(mi.id)");

        sql.append(" order by mi.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<MenuItem> query = entityManager
                .createQuery(sql.toString(), MenuItem.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (filter.getMenuId() != null) {
            query.setParameter("menuId", filter.getMenuId());
            countQuery.setParameter("menuId", filter.getMenuId());
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

    @Override
    public ResultList<MultiUnit> getResultListForTerminal(MenuItemFilter filter) {
        ResultList<MultiUnit> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select mu ");
        sql.append(" from MultiUnit mu ");
        sql.append(" join mu.menuItem mi");
        sql.append(" join mi.menu m");
        sql.append(" where m.deleted = false ");
        sql.append(" and mi.deleted = false ");
        sql.append(" and mu.deleted = false ");
        sql.append(" and m.branchId=:branchId");

        if (filter.getMenuId() != null) {
            sql.append(" and m.id=:menuId");
        }

        if (filter.hasSearch()) {
            sql.append(" and ( ");
            sql.append(" lower(m.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(mi.name)").append(filter.getLikeSearchKey());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select mu", "select count(mu.id)");

        sql.append(" order by mi.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<MultiUnit> query = entityManager
                .createQuery(sql.toString(), MultiUnit.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (filter.getMenuId() != null) {
            query.setParameter("menuId", filter.getMenuId());
            countQuery.setParameter("menuId", filter.getMenuId());
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
