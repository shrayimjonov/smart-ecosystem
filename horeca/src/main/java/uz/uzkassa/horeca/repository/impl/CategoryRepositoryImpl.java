package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.horeca.domain.Category;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.CategoryType;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.CategoryRepositoryCustom;


@SuppressWarnings("unused")
public class CategoryRepositoryImpl extends BaseRepositoryImpl implements CategoryRepositoryCustom {

    @Override
    public ResultList<Category> getResultList(BaseFilter filter) {
        ResultList<Category> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select c from Category c");
        sql.append(" where c.deleted = false and c.branchId =:branchId ");
        if (filter.getParentId() != null) {
            sql.append(" and c.parentId=:parentId ");
        }
        if (!StringUtils.isEmpty(filter.getType())) {
            sql.append(" and c.categoryType=:type ");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.catalogCode)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.catalogName)").append(filter.getLikeSearchKey());
            sql.append(")");
        }

        String countSql = sql.toString().replaceFirst("select c", "select count(c.id)");
        sql.append(" order by c.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Category> query = entityManager
                .createQuery(sql.toString(), Category.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", getBranchId(filter, true));
        countQuery.setParameter("branchId", getBranchId(filter, true));

        if (filter.getParentId() != null) {
            query.setParameter("parentId", filter.getParentId());
            countQuery.setParameter("parentId", filter.getParentId());
        }
        if (!StringUtils.isEmpty(filter.getType())) {
            query.setParameter("type", CategoryType.valueOf(filter.getType()));
            countQuery.setParameter("type", CategoryType.valueOf(filter.getType()));
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
