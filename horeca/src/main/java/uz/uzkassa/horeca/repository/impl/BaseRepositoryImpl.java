package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.web.rest.errors.BadRequestException;

import java.util.Collections;

public abstract class BaseRepositoryImpl {

    @PersistenceContext
    protected EntityManager entityManager;

    protected static Long getBranchId(BaseFilter filter, boolean throwException) {
        Long branchId = filter.getBranchId();
        if (branchId == null && throwException) {
            throw new BadRequestException("Branch is required");
        }
        return branchId;
    }

    protected <T> Page<T> getResultPage (TypedQuery<Long> countQuery, TypedQuery<T> query, BaseFilter filter) {
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            return new PageImpl<>(query.getResultList(), filter.getSortedPageable(), count);
        }
        return new PageImpl<>(Collections.emptyList(), filter.getSortedPageable(), count);
    }
}
