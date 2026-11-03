package uz.uzkassa.cts.criteriahelper;

import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Collection;

public abstract class BaseQueryImpl<R, Q extends BaseQueryImpl<R, Q>> implements BaseQuery<R, Q> {

    protected abstract Q self();

    protected final Collection<QueryPredicate<R>> andPredicates;
    protected final Collection<QueryPredicate<R>> orPredicates;
    protected boolean condition = true;

    public BaseQueryImpl() {
        this.orPredicates = new ArrayList<>();
        this.andPredicates = new ArrayList<>();
    }

    protected <T> Predicate[] buildPredicates(CommonAbstractCriteria criteria, Collection<QueryPredicate<T>> predicates, CriteriaBuilder cb, Root<T> root) {
        return predicates.stream().map(t -> t.apply(criteria, cb, root)).toArray(Predicate[]::new);
    }

    @Override
    public Q or() {
        condition = false;
        return self();
    }

    @Override
    public Q and() {
        condition = true;
        return self();
    }

    @Override
    public <V> Q equal(SingularAttribute<R, V> attribute, V value) {
        if (condition) andPredicates.add((criteria, cb, root) -> cb.equal(root.get(attribute), value));
        else orPredicates.add((criteria, cb, root) -> cb.equal(root.get(attribute), value));
        return self();
    }

    @Override
    public <V> Q equal(String attribute, V value) {
        if (condition) andPredicates.add((criteria, cb, root) -> cb.equal(root.get(attribute), value));
        else orPredicates.add((criteria, cb, root) -> cb.equal(root.get(attribute), value));
        return self();
    }

    @Override
    public <P, V> Q equal(SingularAttribute<R, P> attribute1, SingularAttribute<P, V> attribute2, V value) {
        if (condition) andPredicates.add((criteria, cb, root) -> cb.equal(root.get(attribute1).get(attribute2), value));
        else orPredicates.add((criteria, cb, root) -> cb.equal(root.get(attribute1).get(attribute2), value));
        return self();
    }

    @Override
    public <P1, P2, V> Q equal(SingularAttribute<R, P1> attribute1, SingularAttribute<P1, P2> attribute2, SingularAttribute<P2, V> attribute3, V value) {
        QueryPredicate predicate = (criteria, cb, root) -> cb.equal(root.get(attribute1).get(attribute2).get(attribute3), value);
        if (condition) {
            andPredicates.add(predicate);
        } else {
            orPredicates.add(predicate);
        }
        return self();
    }

    @Override
    public <V> Q like(SingularAttribute<R, V> attribute, V value) {
        QueryPredicate predicate = (criteria, cb, root) -> cb.like(cb.lower((Expression<String>) root.get(attribute)), "%" + value + "%");
        if (condition) {
            andPredicates.add(predicate);
        } else {
            orPredicates.add(predicate);
        }
        return self();
    }

    @Override
    public <V> Q like(String attribute, V value) {
        QueryPredicate predicate = (criteria, cb, root) -> cb.like(root.get(attribute),  value.toString());
        if (condition) {
            andPredicates.add(predicate);
        } else {
            orPredicates.add(predicate);
        }
        return self();
    }

    @Override
    public <P, V> Q like(SingularAttribute<R, P> attribute1, SingularAttribute<P, V> attribute2, V value) {
        QueryPredicate predicate = (criteria, cb, root) -> cb.like(cb.lower((Expression<String>) root.get(attribute1).get(attribute2)), value.toString());
        if (condition) {
            andPredicates.add(predicate);
        } else {
            orPredicates.add(predicate);
        }
        return self();
    }

    @Override
    public <P1, P2, V> Q like(SingularAttribute<R, P1> attribute1, SingularAttribute<P1, P2> attribute2, SingularAttribute<P2, V> attribute3, V value) {
        QueryPredicate predicate = (criteria, cb, root) -> cb.like(cb.lower((Expression<String>) root.get(attribute1).get(attribute2).get(attribute3)), value.toString() );
        if (condition) {
            andPredicates.add(predicate);
        } else {
            orPredicates.add(predicate);
        }
        return self();
    }

    @Override
    public <V extends Comparable<V>> Q between(SingularAttribute<R, V> attribute, V value1, V value2) {
        QueryPredicate predicate = (criteria, cb, root) -> cb.between(root.get(attribute), value1, value2);
        if (condition)
            andPredicates.add(predicate);
        else
            orPredicates.add(predicate);
        return self();
    }

    @Override
    public <V extends Comparable<V>> Q between(String attribute, V value1, V value2) {
        QueryPredicate predicate = (criteria, cb, root) -> cb.between(root.get(attribute), value1, value2);
        if (condition) {
            andPredicates.add(predicate);
        } else {
            orPredicates.add(predicate);
        }
        return self();
    }

    @Override
    public <P, V extends Comparable<V>> Q between(SingularAttribute<R, P> attribute1, SingularAttribute<P, V> attribute2, V value1, V value2) {
        if (condition)
            andPredicates.add((criteria, cb, root) -> cb.between(root.get(attribute1).get(attribute2), value1, value2));
        else
            orPredicates.add((criteria, cb, root) -> cb.between(root.get(attribute1).get(attribute2), value1, value2));
        return self();
    }

    @Override
    public <P1, P2, V extends Comparable<V>> Q between(SingularAttribute<R, P1> attribute1, SingularAttribute<P1, P2> attribute2, SingularAttribute<P2, V> attribute3, V value1, V value2) {
        if (condition)
            andPredicates.add((criteria, cb, root) -> cb.between(root.get(attribute1).get(attribute2).get(attribute3), value1, value2));
        else
            orPredicates.add((criteria, cb, root) -> cb.between(root.get(attribute1).get(attribute2).get(attribute3), value1, value2));
        return self();
    }

}
