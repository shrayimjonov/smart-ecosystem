package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;
import uz.uzkassa.smartpossupply.repository.FilterableRepository;

import java.util.List;

public abstract class BaseFilterableRepositoryImpl<T, F extends SimpleFilter>  extends BaseRepositoryImpl implements FilterableRepository<T, F> {

    protected abstract Class<T> getEntityClass();

    protected abstract String getEntityAlias();

    /**
     * Generic method to get a result list with count and pagination
     */
    public ResultList<T> getResultList(F filter) {
        applyFilter(filter);

        // Early return if no results
        Long count = getCount(filter, entityManager);
        if (count == 0) {
            return new ResultList<>();
        }

        // Fetch results
        List<T> entities = fetchEntities(filter, entityManager, getEntityClass(), getEntityAlias());

        ResultList<T> resultList = new ResultList<>();
        resultList.setCount(count);
        resultList.setList(entities);

        return resultList;
    }

    /**
     * Generic lookup method
     */
    public List<T> lookUp(F filter) {
        applyFilter(filter);
        return fetchEntities(filter, entityManager, getEntityClass(), getEntityAlias());
    }

    /**
     * Helper method to append conditions conditionally
     */
    protected void appendCondition(StringBuilder sql, boolean condition, String clause) {
        if (condition) {
            sql.append(clause);
        }
    }

    /**
     * Helper method to set parameter only if the value is not null
     */
    protected void setParameterIfNotNull(TypedQuery<?> query, String paramName, Object value) {
        if (value != null) {
            query.setParameter(paramName, value);
        }
    }
}