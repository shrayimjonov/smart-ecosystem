package uz.uzkassa.cts.criteriahelper;

import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@FunctionalInterface
public interface QueryPredicate <R> {
    Predicate apply(CommonAbstractCriteria criteria, CriteriaBuilder cb, Root<R> root);
}
