package uz.uzkassa.cts.criteriahelper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SelectQueryImpl<R, PR> extends BaseQueryImpl<R, SelectQueryImpl<R, PR>> implements SelectQuery<R, SelectQueryImpl<R, PR>> {
    private final EntityManager em;
    private final CriteriaBuilder cb;
    private final CriteriaQuery query;
    private final Root<R> root;
    private final Class<PR> projection;
    private final List<jakarta.persistence.criteria.Order> orderList;
    private boolean isCount = false;

    public SelectQueryImpl(EntityManager em, Class<R> type, Class<PR> projection) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
        this.query = cb.createQuery(projection);
        this.root = query.from(type);
        this.orderList = new ArrayList<>();
        this.projection = projection;
    }

    @Override
    protected SelectQueryImpl<R, PR> self() {
        return this;
    }

    private <T> TypedQuery<T> buildQuery(Selection<T> selection) {
        CriteriaQuery<T> resultQuery;
        if (!isCount)
            resultQuery = query.multiselect(mapToProjection(projection, root));
        else
            resultQuery = query.select(selection);
        resultQuery.orderBy(orderList);
        resultQuery.where(cb.and(buildPredicates(query, andPredicates, cb, root)), cb.or(buildPredicates(query, orPredicates, cb, root)));
        isCount = false;
        return em.createQuery(resultQuery);
    }


    public <T> List<Selection<?>> mapToProjection(Class<T> clazz, Root<?> root) {
        List<Selection<?>> paths = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            AliasMap annotation = field.getAnnotation(AliasMap.class);
            if (annotation == null) {
                paths.add(root.get(field.getName()));
            } else if (!StringUtils.isEmpty(annotation.name())) {
                if (!StringUtils.isEmpty(annotation.joinRootName())) {
                    doJoin(field, paths, root, annotation);
                } else
                    paths.add(root.get(annotation.name()).alias(field.getName()));
            }
        }
        return paths;
    }

    private void doJoin(Field field, List<Selection<?>> paths, Root<?> root, AliasMap annotation) {
        String[] joinNames = annotation.joinRootName().split(":");
        Join<?, ?> join = doJoin(root, joinNames[0]);
        for (int i = 1; i < joinNames.length; i++) {
            join = doJoin(join, joinNames[1]);
        }
        paths.add(join.get(annotation.name()).alias(field.getName()));
    }

    public <X, Y> Join<X, Y> doJoin(From<X, Y> criteriaRoot, String name) {
        return (Join<X, Y>) criteriaRoot.getJoins().stream()
            .filter(j -> j.getAttribute().getName().equalsIgnoreCase(name))
            .findFirst()
            .orElseGet(() -> criteriaRoot.join(name, JoinType.LEFT));
    }


    @Override
    public List<R> findAll() {
        return buildQuery(root)
            .getResultList();
    }

    @Override
    public Page<R> findAll(Pageable pageable) {
        List<R> result = buildQuery(root).setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .getResultList();
        long total = count();
        return new PageImpl<>(result, pageable, total);
    }


    @Override
    public <P> List<P> findAll(SingularAttribute<R, P> attribute) {
        return buildQuery(root.get(attribute))
            .getResultList();
    }

    @Override
    public <P1, P2> List<P2> findAll(SingularAttribute<R, P1> attribute1,
                                     SingularAttribute<P1, P2> attribute2) {
        return buildQuery(root.get(attribute1).get(attribute2))
            .getResultList();
    }

    @Override
    public R getOne() {
        return buildQuery(root)
            .setMaxResults(1)
            .getSingleResult();
    }

    @Override
    public <P> P getOne(SingularAttribute<R, P> attribute) {
        return buildQuery(root.get(attribute))
            .setMaxResults(1)
            .getSingleResult();
    }

    @Override
    public <P1, P2> P2 getOne(SingularAttribute<R, P1> attribute1,
                              SingularAttribute<P1, P2> attribute2) {
        return buildQuery(root.get(attribute1).get(attribute2))
            .setMaxResults(1)
            .getSingleResult();
    }

    @Override
    public Optional<R> findOne() {
        return Optional.ofNullable(getOne());
    }

    @Override
    public <P> Optional<P> findOne(SingularAttribute<R, P> attribute) {
        return Optional.ofNullable(getOne(attribute));
    }

    @Override
    public <P1, P2> Optional<P2> findOne(SingularAttribute<R, P1> attribute1,
                                         SingularAttribute<P1, P2> attribute2) {
        return Optional.ofNullable(getOne(attribute1, attribute2));
    }

    @Override
    public long count() {
        isCount = true;
        return buildQuery(cb.count(root))
            .getSingleResult();
    }

    @Override
    public <P> long count(SingularAttribute<R, P> attribute) {
        isCount = true;
        return buildQuery(cb.count(root.get(attribute)))
            .getSingleResult();
    }

    @Override
    public <P1, P2> long count(SingularAttribute<R, P1> attribute1,
                               SingularAttribute<P1, P2> attribute2) {
        isCount = true;
        return buildQuery(cb.count(root.get(attribute1).get(attribute2)))
            .getSingleResult();
    }

    @Override
    public <P> SelectQueryImpl<R, PR> order(SingularAttribute<R, P> attribute) {
        return order(attribute, Order.ASC);
    }

    @Override
    public <P> SelectQueryImpl<R, PR> order(SingularAttribute<R, P> attribute,
                                            Order sort) {
        jakarta.persistence.criteria.Order order = sort == Order.ASC ? cb.asc(root.get(attribute)) : cb.desc(root.get(attribute));
        orderList.add(order);
        return this;
    }

    @Override
    public <P1, P2> SelectQueryImpl<R, PR> order(SingularAttribute<R, P1> attribute1,
                                                 SingularAttribute<P1, P2> attribute2) {
        return order(attribute1, attribute2, Order.ASC);
    }

    @Override
    public <P1, P2> SelectQueryImpl<R, PR> order(SingularAttribute<R, P1> attribute1,
                                                 SingularAttribute<P1, P2> attribute2,
                                                 Order sort) {
        Path<P2> path = root.get(attribute1).get(attribute2);
        jakarta.persistence.criteria.Order order = sort == Order.ASC ? cb.asc(path) : cb.desc(path);
        orderList.add(order);
        return this;
    }

    @Override
    public <P1, P2, P3> SelectQueryImpl<R, PR> order(SingularAttribute<R, P1> attribute1,
                                                     SingularAttribute<P1, P2> attribute2,
                                                     SingularAttribute<P2, P3> attribute3) {
        return order(attribute1, attribute2, attribute3, Order.ASC);
    }

    @Override
    public <P1, P2, P3> SelectQueryImpl<R, PR> order(SingularAttribute<R, P1> attribute1,
                                                     SingularAttribute<P1, P2> attribute2,
                                                     SingularAttribute<P2, P3> attribute3, Order sort) {
        Path<P3> path = root.get(attribute1).get(attribute2).get(attribute3);
        jakarta.persistence.criteria.Order order = sort == Order.ASC ? cb.asc(path) : cb.desc(path);
        orderList.add(order);
        return this;
    }

    @Override
    public <P> SelectQueryImpl<R, PR> fetch(SingularAttribute<R, P> attribute) {
        root.fetch(attribute);
        return this;
    }

    @Override
    public <P1, P2> SelectQueryImpl<R, PR> fetch(SingularAttribute<R, P1> attribute1,
                                                 SingularAttribute<P1, P2> attribute2) {
        root.fetch(attribute1).fetch(attribute2);
        return this;
    }

    @Override
    public <P1, P2, P3> SelectQueryImpl<R, PR> fetch(SingularAttribute<R, P1> attribute1,
                                                     SingularAttribute<P1, P2> attribute2,
                                                     SingularAttribute<P2, P3> attribute3) {
        root.fetch(attribute1).fetch(attribute2).fetch(attribute3);
        return this;
    }

    @Override
    public <P> SelectQuery<R, SelectQueryImpl<R, PR>> join(SingularAttribute<R, P> attribute,
                                                           JoinType joinType) {
        root.join(attribute, joinType);
        return this;
    }

    @Override
    public <P1, P2> SelectQuery<R, SelectQueryImpl<R, PR>> join(SingularAttribute<R, P1> attribute1,
                                                                SingularAttribute<P1, P2> attribute2,
                                                                JoinType joinType) {
        root.join(attribute1, JoinType.INNER).join(attribute2, joinType);
        return this;
    }

    @Override
    public <P1, P2, P3> SelectQuery<R, SelectQueryImpl<R, PR>> join(SingularAttribute<R, P1> attribute1,
                                                                    SingularAttribute<P1, P2> attribute2,
                                                                    SingularAttribute<P2, P3> attribute3,
                                                                    JoinType joinType) {
        root.join(attribute1).join(attribute2).join(attribute3, joinType);
        return this;
    }
}
