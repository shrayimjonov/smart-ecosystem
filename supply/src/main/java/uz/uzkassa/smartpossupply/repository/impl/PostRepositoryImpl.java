package uz.uzkassa.smartpossupply.repository.impl;


import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.post.Post;
import uz.uzkassa.smartpossupply.enums.PostStatus;
import uz.uzkassa.smartpossupply.enums.PostType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.PostRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 02.11.2023
 */
public class PostRepositoryImpl
    extends BaseFilterableRepositoryImpl<Post, BaseFilter>
    implements PostRepositoryCustom {

    @Override
    protected Class<Post> getEntityClass() {
        return Post.class;
    }

    @Override
    protected String getEntityAlias() {
        return "p";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Post p ");

        // Base conditions
        sql.append("WHERE p.deleted = false ")
            .append("AND p.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND p.status = :status ");
        appendCondition(sql, StringUtils.hasText(filter.getType()), "AND p.type = :type ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(p.name)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", PostStatus.valueOf(filter.getStatus()));
        }

        if (StringUtils.hasText(filter.getType())) {
            query.setParameter("type", PostType.valueOf(filter.getType()));
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
