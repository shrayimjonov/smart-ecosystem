package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import uz.uzkassa.horeca.domain.menu.Menu;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.MenuRepositoryCustom;
import uz.uzkassa.horeca.utils.HeaderUtils;

public class MenuRepositoryImpl extends BaseRepositoryImpl implements MenuRepositoryCustom {

    @Override
    public Page<Menu> getResultList(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select m ");
        sql.append(" from Menu m ");
        sql.append(" where m.deleted is not true");
        sql.append(" and m.branchId=:branchId");
        if (HeaderUtils.isFromMobile()) {
            if (filter.getParentId() == null) {
                sql.append(" and m.parentId is null");
            }
        }
        if (filter.getParentId() != null) {
            sql.append(" and m.parentId=:parentId");
        }

        if (filter.hasSearch()) {
            sql.append(" and ( ");
            sql.append(" lower(m.name)").append(filter.getLikeSearchKey());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select m", "select count(m.id)");

        sql.append(" order by m.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Menu> query = entityManager
                .createQuery(sql.toString(), Menu.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (filter.getParentId() != null) {
            query.setParameter("parentId", filter.getParentId());
            countQuery.setParameter("parentId", filter.getParentId());
        }

        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        return getResultPage(countQuery, query, filter);
    }
}
