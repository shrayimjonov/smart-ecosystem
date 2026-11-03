package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.OrderProduct;
import uz.uzkassa.silen.dto.filter.OrderFilter;
import uz.uzkassa.silen.repository.pg.OrderProductRepositoryCustom;

public class OrderProductRepositoryImpl implements OrderProductRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<OrderProduct> findAllByFilter(OrderFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from OrderProduct o");
        sql.append(" inner join o.order");
        sql.append(" inner join o.product");
        sql.append(" where o.order.deleted is not true ");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.order.organizationId=:organizationId");
        }
        if (filter.getCisType() != null) {
            sql.append(" and o.cisType=:cisType");
        }
        if (filter.getStatus() != null) {
            sql.append(" and o.bufferStatus=:bufferStatus");
        }
        if (filter.getStatusIn() != null) {
            sql.append(" and o.bufferStatus in (:statusList)");
        }
        if (filter.getOrderProductId() != null) {
            sql.append(" and o.id=:id");
        }
        if (filter.getProductId() != null) {
            sql.append(" and o.productId=:productId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and ( lower(o.order.number) like :searchKey )");
        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.createdDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<OrderProduct> query = this.entityManager.createQuery(sql.toString(), OrderProduct.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getCisType() != null) {
            query.setParameter("cisType", filter.getCisType());
            countQuery.setParameter("cisType", filter.getCisType());
        }
        if (filter.getStatus() != null) {
            query.setParameter("bufferStatus", filter.getStatus());
            countQuery.setParameter("bufferStatus", filter.getStatus());
        }
        if (filter.getStatusIn() != null) {
            query.setParameter("statusList", filter.getStatusIn());
            countQuery.setParameter("statusList", filter.getStatusIn());
        }
        if (filter.getOrderProductId() != null) {
            query.setParameter("id", filter.getOrderProductId());
            countQuery.setParameter("id", filter.getOrderProductId());
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
}
