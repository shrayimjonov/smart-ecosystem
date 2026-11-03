package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.AcceptanceTransferAct;
import uz.uzkassa.silen.dto.filter.AcceptanceTransferActFilter;
import uz.uzkassa.silen.repository.pg.AcceptanceTransferActRepositoryCustom;

public class AcceptanceTransferActRepositoryCustomImpl implements AcceptanceTransferActRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    EntityManager entityManager;

    @Override
    public Page<AcceptanceTransferAct> findAllByFilter(AcceptanceTransferActFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from AcceptanceTransferAct o");

        sql.append(" where o.deleted is not true");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organizationId=:organizationId");
        }
        if (filter.getYear() != null) {
            sql.append(" and YEAR(o.createdDate)=:year");
        }
        if (filter.getCustomerTin() != null) {
            sql.append(" and o.customer.tin=:customerTin");
        }
        if (filter.getSupplierTin() != null) {
            sql.append(" and o.organization.tin=:supplierTin");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and o.customerId=:customerId");
        }
        if (filter.getStatus() != null) {
            sql.append(" and o.status=:status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.actNumber) like :searchKey");
            sql.append(" or o.actId like :searchKey)");
        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.lastModifiedDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<AcceptanceTransferAct> query = this.entityManager.createQuery(sql.toString(), AcceptanceTransferAct.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);


        if (filter.getYear() != null) {
            query.setParameter("year", filter.getYear());
            countQuery.setParameter("year", filter.getYear());
        }
        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getSupplierTin() != null) {
            query.setParameter("supplierTin", filter.getSupplierTin());
            countQuery.setParameter("supplierTin", filter.getSupplierTin());
        }
        if (filter.getCustomerTin() != null) {
            query.setParameter("customerTin", filter.getCustomerTin());
            countQuery.setParameter("customerTin", filter.getCustomerTin());
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
