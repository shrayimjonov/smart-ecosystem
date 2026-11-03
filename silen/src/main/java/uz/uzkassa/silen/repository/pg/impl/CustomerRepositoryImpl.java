package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Customer;
import uz.uzkassa.silen.dto.filter.CustomerFilter;
import uz.uzkassa.silen.repository.pg.CustomerRepositoryCustom;

@Repository
public class CustomerRepositoryImpl implements CustomerRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<Customer> findAllByFilter(CustomerFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from Customer o");
        sql.append(" where o.deleted is not true");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organizationId=:organizationId");
        }
        if (filter.getCommittent() != null) {
            sql.append(" and o.isCommittent=:isCommittent");
        }
        if (filter.getHasFinancialDiscount() != null) {
            sql.append(" and o.hasFinancialDiscount=:hasFinancialDiscount");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.name) like :searchKey");
            sql.append(" or o.tin like :searchKey");
            sql.append(")");
        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)")
            .replaceAll("fetch", "");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.lastModifiedDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Customer> query = this.entityManager.createQuery(sql.toString(), Customer.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getCommittent() != null) {
            query.setParameter("isCommittent", filter.getCommittent());
            countQuery.setParameter("isCommittent", filter.getCommittent());
        }
        if (filter.getHasFinancialDiscount() != null) {
            query.setParameter("hasFinancialDiscount", filter.getHasFinancialDiscount());
            countQuery.setParameter("hasFinancialDiscount", filter.getHasFinancialDiscount());
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
