package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.Order;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.repository.pg.OrderRepositoryCustom;

public class OrderRepositoryImpl implements OrderRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<Order> findAllByFilter(Filter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from Order o");
        sql.append(" where o.deleted is not true and o.organizationId=:organizationId");
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.number) like :searchKey");
            sql.append(")");
        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.lastModifiedDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Order> query = this.entityManager.createQuery(sql.toString(), Order.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize())
            .setParameter("organizationId", filter.getOrganizationId());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class)
            .setParameter("organizationId", filter.getOrganizationId());

        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
            countQuery.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
            countQuery.setParameter("to", filter.getTo());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }
}
