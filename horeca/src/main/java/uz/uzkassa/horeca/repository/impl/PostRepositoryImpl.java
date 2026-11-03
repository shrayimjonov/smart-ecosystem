package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.horeca.domain.post.Post;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.PostRepositoryCustom;

public class PostRepositoryImpl extends BaseRepositoryImpl implements PostRepositoryCustom {

    @Override
    public ResultList<Post> getResultList(BaseFilter filter) {
        ResultList<Post> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select p from Post p");
        sql.append(" where p.deleted = false");
        sql.append(" and p.branchId=:branchId ");

        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(p.title)").append(filter.getLikeSearchKey());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select p", "select count(p.id)");
        sql.append(" order by p.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Post> query = entityManager
                .createQuery(sql.toString(), Post.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

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
