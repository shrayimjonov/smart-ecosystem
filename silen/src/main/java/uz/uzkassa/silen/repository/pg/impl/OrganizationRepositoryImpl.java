package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.dto.dashboard.OrganizationsDTO;
import uz.uzkassa.silen.dto.filter.OrganizationFilter;
import uz.uzkassa.silen.repository.pg.OrganizationRepositoryCustom;

import java.util.List;

public class OrganizationRepositoryImpl implements OrganizationRepositoryCustom {
    @PersistenceContext(unitName = "masterPU")
    private EntityManager masterEntityManager;
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<Organization> findAllByFilter(OrganizationFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        final boolean hasType = filter.hasOrganizationType();
        StringBuilder sql = new StringBuilder("select o from Organization o");
        if (hasType) {
            sql.append(" join o.types types");
        }
        sql.append(" where o.deleted is not true");
        sql.append(" and o.active=:active");

        if (hasType) {
            sql.append(" and types in (:types)");
        }
        if (filter.getManagerId() != null) {
            sql.append(" and o.managerId=:managerId");
        }
        if (filter.getAggregationMode() != null) {
            sql.append(" and o.aggregationMode=:aggregationMode");
        }
        if (filter.isWithOutManager()) {
            sql.append(" and o.managerId is null");
        }
        if (filter.getDemoAllowed() != null) {
            sql.append(" and o.demoAllowed=:demoAllowed");
        }
        if (filter.getXfileBinded() != null) {
            sql.append(" and o.xfileBinded=:xfileBinded");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate<=:to");
        }
        if (filter.getWithoutChild() != null) {
            sql.append(" and o.hasChild is not true");
        }
        if (filter.getWithoutParent() != null) {
            sql.append(" and o.parentId is null");
        }
        if (filter.getParentId() != null) {
            sql.append(" and o.parentId=:parentId");
        }
        if (filter.getBillingDocumentType() != null) {
            sql.append(" and o.billingDocumentType=:billingDocumentType");
        }

        if (hasSearch) {
            sql.append(" and (lower(o.name) like :searchKey");
            sql.append(" or lower(o.tin) like :searchKey");
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

        TypedQuery<Organization> query = this.entityManager.createQuery(sql.toString(), Organization.class)
                .setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("active", filter.isActive());
        countQuery.setParameter("active", filter.isActive());

        if (hasType) {
            query.setParameter("types", filter.getOrganizationTypes());
            countQuery.setParameter("types", filter.getOrganizationTypes());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
            countQuery.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
            countQuery.setParameter("to", filter.getTo());
        }
        if (filter.getAggregationMode() != null) {
            query.setParameter("aggregationMode", filter.getAggregationMode());
            countQuery.setParameter("aggregationMode", filter.getAggregationMode());
        }
        if (filter.getManagerId() != null) {
            query.setParameter("managerId", filter.getManagerId());
            countQuery.setParameter("managerId", filter.getManagerId());
        }
        if (filter.getDemoAllowed() != null) {
            query.setParameter("demoAllowed", filter.getDemoAllowed());
            countQuery.setParameter("demoAllowed", filter.getDemoAllowed());
        }
        if (filter.getXfileBinded() != null) {
            query.setParameter("xfileBinded", filter.getXfileBinded());
            countQuery.setParameter("xfileBinded", filter.getXfileBinded());
        }
        if (filter.getParentId() != null) {
            query.setParameter("parentId", filter.getParentId());
            countQuery.setParameter("parentId", filter.getParentId());
        }
        if (filter.getBillingDocumentType() != null) {
            query.setParameter("billingDocumentType", filter.getBillingDocumentType());
            countQuery.setParameter("billingDocumentType", filter.getBillingDocumentType());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }

    @Override
    public List<OrganizationsDTO> findOrganizations() {
        Query query = entityManager.createNativeQuery("select type as type, count(id) as total from organization " +
            "where deleted is not true group by type", "OrganizationsMapper");
        return query.getResultList();
    }

    @Override
    public void createSequence(String gcp) {
        String sql = "create sequence if not exists org_seq_" + gcp + " start 1 increment 1 maxvalue 9999999;";
        Query query = masterEntityManager.createNativeQuery(sql);
        query.executeUpdate();
    }
}
