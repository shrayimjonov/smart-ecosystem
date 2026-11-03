package uz.uzkassa.smartpossupply.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;

import java.util.List;

public interface FilterableRepository<T, F extends SimpleFilter> {

    /**
     * Applies security and context filters
     */
    void applyFilter(F filter);

    /**
     * Builds the base SQL query
     */
    String getBaseQuery(F filter, boolean includeFetch);

    /**
     * Sets query parameters based on a filter
     */
    void setParameters(TypedQuery<?> query, F filter);

    /**
     * Gets the total count of entities matching the filter
     */
    default Long getCount(F filter, EntityManager entityManager) {
        String countSql = "SELECT COUNT(*) " + getBaseQuery(filter, false);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);
        setParameters(countQuery, filter);
        return countQuery.getSingleResult();
    }

    /**
     * Fetches entities with pagination
     */
    default List<T> fetchEntities(F filter, EntityManager entityManager, Class<T> entityClass, String entityAlias) {
        String sql = String.format("SELECT %s ", entityAlias) + getBaseQuery(filter, true) +
            String.format(" ORDER BY %s.%s %s", entityAlias, filter.getOrderBy(), filter.getSortOrder().getName());

        TypedQuery<T> query = entityManager
            .createQuery(sql, entityClass)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        setParameters(query, filter);
        return query.getResultList();
    }
}
