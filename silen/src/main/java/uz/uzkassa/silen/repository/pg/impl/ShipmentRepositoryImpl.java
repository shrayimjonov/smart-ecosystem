package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.Shipment;
import uz.uzkassa.silen.dto.ShipmentHistoryDTO;
import uz.uzkassa.silen.dto.ShipmentStatsDTO;
import uz.uzkassa.silen.dto.dashboard.DashboardStatsDTO;
import uz.uzkassa.silen.dto.filter.ShipmentFilter;
import uz.uzkassa.silen.dto.filter.TopFilter;
import uz.uzkassa.silen.dto.warehouse.WarehouseSelectItem;
import uz.uzkassa.silen.enumeration.ShipmentStatus;
import uz.uzkassa.silen.repository.pg.ShipmentRepositoryCustom;

import java.math.BigDecimal;
import java.util.List;

public class ShipmentRepositoryImpl implements ShipmentRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<Shipment> findAllByFilter(ShipmentFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from Shipment o");
        sql.append(" where o.deleted is not true ");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organizationId=:organizationId");
        }
        if (filter.getStatus() != null) {
            sql.append(" and o.status=:status");
        }
        if (filter.getStoreId() != null) {
            sql.append(" and o.storeId=:storeId");
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

        TypedQuery<Shipment> query = this.entityManager.createQuery(sql.toString(), Shipment.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", filter.getStatus());
            countQuery.setParameter("status", filter.getStatus());
        }
        if (filter.getStoreId() != null) {
            query.setParameter("storeId", filter.getStoreId());
            countQuery.setParameter("storeId", filter.getStoreId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
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

    @Override
    public List<ShipmentStatsDTO> findAllByFilterForStats(ShipmentFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();

        StringBuilder sql = new StringBuilder("select o.status, count(o.status) as qty from Shipment o");
        if (hasSearch) {
            sql.append(" left join organization on o.organization_id=organization.id");
        }
        sql.append(" where o.deleted is not true and o.organization_id=:organizationId");

        if (filter.getStatus() != null) {
            sql.append(" and o.status=:status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.createdDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.createdDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.number) like :searchKey");
            sql.append(" or lower(organization.name) like :searchKey");
            sql.append(" or lower(organization.tin) like :searchKey");
            sql.append(")");
        }

        sql.append(" group by o.status");
        sql.append(" order by o.status");

        Query query = this.entityManager.createNativeQuery(sql.toString(), "ShipmentStatsMapper")
                .setParameter("organizationId", filter.getOrganizationId());


        if (filter.getStatus() != null) {
            query.setParameter("status", filter.getStatus().toString());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
        }

        return query.getResultList();
    }

    @Override
    public Page<Shipment> findAllByFilterWithoutOrgId(ShipmentFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from Shipment o ");
        sql.append(" join fetch o.toOrganization orgTo");
        sql.append(" join fetch o.organization org");
        sql.append(" where o.deleted is not true");
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
            sql.append(" and (lower(o.number) like :searchKey");
            sql.append(" or lower(org.name) like :searchKey");
            sql.append(" or lower(org.tin) like :searchKey");
            sql.append(")");
        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)")
                .replaceFirst("join fetch o.toOrganization orgTo", "")
                .replaceFirst("join fetch o.organization org", "");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.createdDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Shipment> query = this.entityManager.createQuery(sql.toString(), Shipment.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

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

    @Override
    public Page<WarehouseSelectItem> getShippedProductsStats(ShipmentFilter filter) {

        StringBuilder sql = new StringBuilder("select distinct p.id, p.name,'ShipmentBuyer' as operation, sum(si.qty) as amount");
        sql.append(" from shipment o");
        sql.append(" inner join shipment_item si on o.id = si.shipment_id");
        sql.append(" inner join product p on p.id = si.product_id");
        sql.append(" where o.deleted is not true and o.status = 'CLOSED'");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organization_id=:organizationId");
        }
        if (filter.getProductId() != null) {
            sql.append(" and p.id=:productId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.shipment_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.shipment_date<=:to");
        }

        sql.append(" group by p.id");
        sql.append(" order by p.name");

        String countSql = sql.toString()
                .replace("distinct p.id, p.name,'ShipmentBuyer' as operation, sum(si.qty) as amount", "count(distinct p.id)")
                .replace(" group by p.id,p.name", "")
                .replace(" order by p.name asc", "");

        Query query = this.entityManager.createNativeQuery(sql.toString(), "WarehouseSelectItemMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        Query countQuery = this.entityManager.createNativeQuery(countSql);

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
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
        return new PageImpl<WarehouseSelectItem>(query.getResultList(), filter.getPageable(), (Long) countQuery.getSingleResult());
    }

    @Override
    public Page<ShipmentHistoryDTO> getShipHistory(ShipmentFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o.id,o.shipment_date as shipmentDate,o.number as shipmentNumber,");
        sql.append("o.int_number as intNumber,li.id as invoiceId,li.number as invoiceNumber,li.total,li.id as listInvoiceId, li.factura_id as facturaId");
        sql.append(" from shipment o");
        sql.append(" inner join list_invoice li on o.id = li.shipment_id and li.deleted=false");
        sql.append(" where o.deleted is not true");
        sql.append(" and o.status = :shipmentStatus");
        sql.append(" and o.organization_id=:organizationId");
        sql.append(" and o.customer_id=:customerId");

        if (filter.getFrom() != null) {
            sql.append(" and o.last_modified_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.last_modified_date<=:to");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.number) like :searchKey");
            sql.append(" or lower(li.number) like :searchKey");
            sql.append(")");
        }
        String countSql = sql.toString()
                .replaceAll(" o.id,o.shipment_date as shipmentDate,o.number as shipmentNumber,o.int_number as intNumber,li.id as invoiceId,li.number as invoiceNumber,li.total,li.id as listInvoiceId, li.factura_id as facturaId", " count(o.id)");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.last_modified_date");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        Query query = this.entityManager.createNativeQuery(sql.toString(), "ShipmentHistoryMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize())
                .setParameter("organizationId", filter.getOrganizationId())
                .setParameter("shipmentStatus", ShipmentStatus.CLOSED.name())
                .setParameter("customerId", filter.getCustomerId());
        Query countQuery = entityManager.createNativeQuery(countSql)
                .setParameter("organizationId", filter.getOrganizationId())
                .setParameter("shipmentStatus", ShipmentStatus.CLOSED.name())
                .setParameter("customerId", filter.getCustomerId());

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

        return new PageImpl<ShipmentHistoryDTO>(query.getResultList(), filter.getPageable(), (Long) countQuery.getSingleResult());
    }

    @Override
    public BigDecimal getShipHistorySummary(ShipmentFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        StringBuilder sql = new StringBuilder("select coalesce(sum(o.total),0)");
        sql.append(" from list_invoice o");
        sql.append(" inner join shipment s on o.shipment_id = s.id");
        sql.append(" and s.deleted is not true");
        sql.append(" where o.deleted is not true");
        sql.append(" and s.status =:shipmentStatus");
        sql.append(" and s.organization_id =:organizationId");
        sql.append(" and s.customer_id =:customerId");
        if (filter.getFrom() != null) {
            sql.append(" and s.last_modified_date>= :from");
        }
        if (filter.getTo() != null) {
            sql.append(" and s.last_modified_date<= :to");
        }
        if (hasSearch) {
            sql.append(" and (lower(s.number) like :searchKey");
            sql.append(" or lower(o.number) like :searchKey");
            sql.append(")");
        }

        Query query = this.entityManager.createNativeQuery(sql.toString())
                .setParameter("organizationId", filter.getOrganizationId())
                .setParameter("shipmentStatus", ShipmentStatus.CLOSED.name())
                .setParameter("customerId", filter.getCustomerId());

        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
        }

        return (BigDecimal) query.getSingleResult();

    }

    public List<DashboardStatsDTO> getShippedByProductAndUnit(TopFilter filter) {
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        StringBuilder sql = new StringBuilder("with t as (select o.organization_id as organizationId,");
        sql.append(" i.product_id as productId,");
        sql.append(" jsonb_array_elements(i.types) ->> 'unit'  as unit,");
        sql.append(" cast(jsonb_array_elements(i.types) ->> 'count' as int) as counts");
        sql.append(" from shipment_item i");
        sql.append(" inner join shipment o on o.id = i.shipment_id and status = :status");
        sql.append(" and last_modified_date >= :from");
        sql.append(" and last_modified_date <= :to and deleted is not true");
        sql.append(" group by organizationId, productId, unit, counts)");
        sql.append(" select organizationId, productId, unit, sum(counts) as quantity");
        sql.append(" from t where t.counts > 0");
        sql.append(" group by organizationId, productId, unit");

        Query query = this.entityManager.createNativeQuery(sql.toString(), "TopShippedSelectItemMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        query.setParameter("status", ShipmentStatus.CLOSED.name());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());
        return query.getResultList();
    }
}
