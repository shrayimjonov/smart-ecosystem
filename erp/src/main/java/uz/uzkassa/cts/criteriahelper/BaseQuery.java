package uz.uzkassa.cts.criteriahelper;

import jakarta.persistence.metamodel.SingularAttribute;

public interface BaseQuery<R, Q extends BaseQuery<R, Q>> {
    Q or();
    Q and();
    <V> Q equal(SingularAttribute<R, V> attribute, V value);
    <V> Q equal(String attribute, V value);
    <P, V> Q equal(SingularAttribute<R, P> attribute1, SingularAttribute<P, V> attribute2, V value);
    <P1, P2, V> Q equal(SingularAttribute<R, P1> attribute1, SingularAttribute<P1, P2> attribute2, SingularAttribute<P2, V> attribute3, V value);

    <V> Q like(SingularAttribute<R, V> attribute, V value);
    <V> Q like(String attribute, V value);
    <P, V> Q like(SingularAttribute<R, P> attribute1, SingularAttribute<P, V> attribute2, V value);
    <P1, P2, V> Q like(SingularAttribute<R, P1> attribute1, SingularAttribute<P1, P2> attribute2, SingularAttribute<P2, V> attribute3, V value);

    <V extends Comparable<V>> Q between(SingularAttribute<R, V> attribute, V value1, V value2);
    <V extends Comparable<V>> Q between(String attribute, V value1, V value2);
    <P, V extends Comparable<V>> Q between(SingularAttribute<R, P> attribute1, SingularAttribute<P, V> attribute2, V value1, V value2);
    <P1, P2, V extends Comparable<V>> Q between(SingularAttribute<R, P1> attribute1, SingularAttribute<P1, P2> attribute2, SingularAttribute<P2, V> attribute3, V value1, V value2);
}
