package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.Party;
import uz.uzkassa.silen.dto.TopSelectItem;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.enumeration.ShipmentStatus;
import uz.uzkassa.silen.repository.pg.PartyRepositoryCustom;

import java.util.List;

public class PartyRepositoryImpl implements PartyRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<Party> findAllByFilter(MongoFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from Party o");
        sql.append(" inner join o.orderProduct");
        sql.append(" where o.deleted is not true");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organizationId = :organizationId");
        }
        if (filter.getOrderProductId() != null) {
            sql.append(" and o.orderProductId=:orderProductId");
        }
        if (filter.getProductId() != null) {
            sql.append(" and o.orderProduct.productId=:productId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.orderProduct.product.number) like :searchKey");
            sql.append(" or lower(o.orderProduct.product.barcode) like :searchKey");
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

        TypedQuery<Party> query = this.entityManager.createQuery(sql.toString(), Party.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);
        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getOrderProductId() != null) {
            query.setParameter("orderProductId", filter.getOrderProductId());
            countQuery.setParameter("orderProductId", filter.getOrderProductId());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
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

    public List<TopSelectItem> topOrders(Filter filter) {
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        StringBuilder sql = new StringBuilder("select p.id as productId, p.visible_name as productName, coalesce(sum(o.quantity), 0) as quantity");
        sql.append(" from party o");
        sql.append(" inner join order_product op on o.order_product_id = op.id");
        sql.append(" inner join product p on op.product_id = p.id");
        sql.append(" where o.deleted is not true");
        sql.append(" and o.organization_id=:organizationId");
        sql.append(" and op.buffer_status = :status");
        sql.append(" and op.last_modified_date >= :from");
        sql.append(" and op.last_modified_date <= :to");
        sql.append(" group by p.id");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" ").append(filter.getOrderBy());
        } else {
            sql.append(" quantity");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        Query query = this.entityManager.createNativeQuery(sql.toString(), "TopSelectItemMapper")
                .setMaxResults(3);
        query.setParameter("status", ShipmentStatus.CLOSED.name());
        query.setParameter("organizationId", filter.getOrganizationId());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());


        return query.getResultList();
    }
}
