package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.horeca.domain.Compensation;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.CompensationRepositoryCustom;

public class CompensationRepositoryImpl extends BaseRepositoryImpl implements CompensationRepositoryCustom {

    @Override
    public ResultList<Compensation> getResultList(BaseFilter filter) {
        ResultList<Compensation> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select c from Compensation c ");
        sql.append(" where c.deleted = false ");
        sql.append(" and c.branchId=:branchId ");

        if (filter.getProductId() != null) {
            sql.append(" and c.productId=:productId ");
        }
        if (filter.getCategoryId() != null) {
            sql.append(" and c.categoryId=:categoryId ");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(c.description) ").append(filter.getLikeSearchKey());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select c", "select count(c.id)");

        sql.append(" order by c.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Compensation> query = entityManager
                .createQuery(sql.toString(), Compensation.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.getCategoryId() != null) {
            query.setParameter("categoryId", filter.getCategoryId());
            countQuery.setParameter("categoryId", filter.getCategoryId());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        Long listCount = countQuery.getSingleResult();
        if (listCount > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(listCount);
        }
        return resultList;
    }
}
