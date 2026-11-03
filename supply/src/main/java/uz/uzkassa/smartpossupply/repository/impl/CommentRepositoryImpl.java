package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.Comment;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.enums.CommentType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.CommentRepositoryCustom;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 07.11.2023
 */
public class CommentRepositoryImpl
    extends BaseFilterableRepositoryImpl<Comment, BaseFilter>
    implements CommentRepositoryCustom {

    @Override
    protected Class<Comment> getEntityClass() {
        return Comment.class;
    }

    @Override
    protected String getEntityAlias() {
        return "c";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Comment c ");

        // Base conditions
        sql.append("WHERE c.commentType = :commentType ")
            .append("AND c.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getFrom() != null, "AND c.commentDate >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND c.commentDate <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(c.name)").append(filter.getSearchKey())
                .append(" OR LOWER(c.phone)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        // Required parameters
        query.setParameter("companyId", filter.getCompanyId());
        query.setParameter("commentType", CommentType.valueOf(filter.getType()));

        // Optional parameters
        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public ResultList<Comment> getComments(BaseFilter filter) {
        ResultList<Comment> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select c from Comment c");
        sql.append(" where c.orderId=:orderId");
        sql.append(" and  c.commentType=:commentType ");
        if (filter.getFrom() != null) {
            sql.append(" and c.commentDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and c.commentDate<=:toDate");
        }
        String countSql = sql.toString().replaceFirst("select c", "select count(c.id)");

        sql.append(" order by c.commentDate asc");
        TypedQuery<Comment> query = entityManager.createQuery(sql.toString(), Comment.class).setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);
        query.setParameter("orderId", filter.getId());
        countQuery.setParameter("orderId", filter.getId());

        query.setParameter("commentType", CommentType.valueOf(filter.getType()));
        countQuery.setParameter("commentType", CommentType.valueOf(filter.getType()));

        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setCount(count);
            resultList.setList(query.getResultList());
        }
        return resultList;
    }
}
