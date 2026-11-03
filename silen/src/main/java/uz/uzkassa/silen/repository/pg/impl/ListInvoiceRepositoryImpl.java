package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.ListInvoice;
import uz.uzkassa.silen.dto.filter.InvoiceFilter;
import uz.uzkassa.silen.dto.invoice.DashboardInfoDTO;
import uz.uzkassa.silen.enumeration.InvoiceType;
import uz.uzkassa.silen.enumeration.Status;
import uz.uzkassa.silen.repository.pg.ListInvoiceRepositoryCustom;

@Repository
public class ListInvoiceRepositoryImpl implements ListInvoiceRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    public Page<ListInvoice> findAllByFilter(InvoiceFilter filter, boolean forExcel) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select i from ListInvoice i where i.deleted is not true");

        if (filter.getStatus() != null) {
            sql.append(" and i.status=:status");
        } else {
            sql.append(" and i.status!=:status");
        }
        if (StringUtils.isNotEmpty(filter.getOrganizationTin())) {
            if (InvoiceType.INCOMING.equals(filter.getType())) {
                sql.append(" and i.buyerTin=:organizationTin");
            } else if (InvoiceType.OUTGOING.equals(filter.getType())) {
                sql.append(" and i.sellerTin=:organizationTin");
            } else {
                sql.append(" and (i.sellerTin=:organizationTin or i.buyerTin=:organizationTin)");
            }
        }
        if (filter.getSellerTin() != null) {
            sql.append(" and i.sellerTin=:sellerTin");
        }
        if (filter.getBuyerTin() != null) {
            sql.append(" and i.buyerTin=:buyerTin");
        }
        if (filter.getFrom() != null) {
            sql.append(" and i.lastModifiedDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and i.lastModifiedDate<=:toDate");
        }
        if (hasSearch) {
            sql.append(" and (lower(i.facturaId) like :searchKey");
            sql.append(" or lower(i.number) like :searchKey");
            sql.append(" or lower(i.sellerTin) like :searchKey");
            sql.append(" or lower(i.sellerName) like :searchKey");
            sql.append(" or lower(i.buyerTin) like :searchKey");
            sql.append(" or lower(i.buyerName) like :searchKey");
            sql.append(")");
        }

        String countSql = sql.toString().replaceFirst("select i", "select count(i.id)");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" i.").append(filter.getOrderBy());
        } else {
            sql.append(" i.lastModifiedDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<ListInvoice> query = this.entityManager.createQuery(sql.toString(), ListInvoice.class)
            .setParameter("status", filter.getStatus() != null ? filter.getStatus() : Status.DRAFT);
        Pageable pageRequest;
        if (forExcel) {
            query.setFirstResult(0).setMaxResults(64000);
            pageRequest = PageRequest.of(0, 64000);
        } else {
            query.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
            pageRequest = filter.getPageable();
        }

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class)
            .setParameter("status", filter.getStatus() != null ? filter.getStatus() : Status.DRAFT);

        if (StringUtils.isNotEmpty(filter.getOrganizationTin())) {
            query.setParameter("organizationTin", filter.getOrganizationTin());
            countQuery.setParameter("organizationTin", filter.getOrganizationTin());
        }
        if (StringUtils.isNotEmpty(filter.getBuyerTin())) {
            query.setParameter("buyerTin", filter.getBuyerTin());
            countQuery.setParameter("buyerTin", filter.getBuyerTin());
        }
        if (StringUtils.isNotEmpty(filter.getSellerTin())) {
            query.setParameter("sellerTin", filter.getSellerTin());
            countQuery.setParameter("sellerTin", filter.getSellerTin());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        return new PageImpl<>(query.getResultList(), pageRequest, countQuery.getSingleResult());
    }

    @Override
    public DashboardInfoDTO getDashboardInfo(InvoiceFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        StringBuilder sql = new StringBuilder("select count(i.id) filter (where i.status='PENDING') as pendingCount,")
            .append(" count(i.id) filter (where i.status='ACCEPTED') as acceptedCount,")
            .append(" count(i.id) filter (where i.status='REJECTED') as rejectedCount,")
            .append(" count(i.id) filter (where i.status='CANCELLED') as cancelledCount,")
            .append(" count(i.id) filter (where i.status='AGENT_ACCEPTED') as agentAcceptedCount,")
            .append(" count(i.id) filter (where i.status='AGENT_REJECTED') as agentRejectedCount")
            .append(" from list_invoice i where i.deleted is not true");
        if (filter.getStatus() != null) {
            sql.append(" and i.status=:status");
        } else {
            sql.append(" and i.status!=:status");
        }
        if (StringUtils.isNotEmpty(filter.getOrganizationTin())) {
            if (InvoiceType.INCOMING.equals(filter.getType())) {
                sql.append(" and i.buyer_tin=:organizationTin");
            } else if (InvoiceType.OUTGOING.equals(filter.getType())) {
                sql.append(" and i.seller_tin=:organizationTin");
            } else {
                sql.append(" and (i.seller_tin=:organizationTin or i.buyer_tin=:organizationTin)");
            }
        }
        if (filter.getFrom() != null) {
            sql.append(" and i.last_modified_date>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and i.last_modified_date<=:toDate");
        }
        if (hasSearch) {
            sql.append(" and (lower(i.factura_id) like :searchKey");
            sql.append(" or lower(i.number) like :searchKey");
            sql.append(" or lower(i.seller_tin) like :searchKey");
            sql.append(" or lower(i.seller_name) like :searchKey");
            sql.append(" or lower(i.buyer_tin) like :searchKey");
            sql.append(" or lower(i.buyer_name) like :searchKey");
            sql.append(")");
        }
        try {
            Query query = entityManager.createNativeQuery(sql.toString(), "DashboardInfoMapper")
                .setParameter("status", filter.getStatus() != null ? filter.getStatus().name() : Status.DRAFT.name())
                .setMaxResults(1);
            if (StringUtils.isNotEmpty(filter.getOrganizationTin())) {
                query.setParameter("organizationTin", filter.getOrganizationTin());
            }
            if (filter.getFrom() != null) {
                query.setParameter("fromDate", filter.getFrom());
            }
            if (filter.getTo() != null) {
                query.setParameter("toDate", filter.getTo());
            }
            if (hasSearch) {
                query.setParameter("searchKey", filter.getSearchForQuery());
            }

            return (DashboardInfoDTO) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return new DashboardInfoDTO();
        }
    }
}
