package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.PurchaseOrder;
import uz.uzkassa.silen.dto.filter.PurchaseOrderFilter;
import uz.uzkassa.silen.repository.pg.PurchaseOrderRepositoryCustom;

public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<PurchaseOrder> findAllByFilter(PurchaseOrderFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from PurchaseOrder o");
        sql.append(" where o.deleted is not true and o.fromOrganizationId=:organizationId");

        if (filter.getType() != null) {
            sql.append(" and o.type=:type");
        }
        if (filter.getStoreId() != null) {
            sql.append(" and o.storeId=:storeId");
        }
        if (filter.getStatus() != null) {
            sql.append(" and o.status=:status");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and o.customerId=:customerId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.number) like :searchKey");
            sql.append(" or o.customer.tin like :searchKey");
            sql.append(" or lower(o.customer.name) like :searchKey");
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

        TypedQuery<PurchaseOrder> query = this.entityManager.createQuery(sql.toString(), PurchaseOrder.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize())
            .setParameter("organizationId", filter.getOrganizationId());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class)
            .setParameter("organizationId", filter.getOrganizationId());

        if (filter.getType() != null) {
            query.setParameter("type", filter.getType());
            countQuery.setParameter("type", filter.getType());
        }
        if (filter.getStoreId() != null) {
            query.setParameter("storeId", filter.getStoreId());
            countQuery.setParameter("storeId", filter.getStoreId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", filter.getStatus());
            countQuery.setParameter("status", filter.getStatus());
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
}
