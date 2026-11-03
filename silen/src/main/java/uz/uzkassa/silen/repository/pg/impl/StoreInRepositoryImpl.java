package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.StoreIn;
import uz.uzkassa.silen.dto.TopSelectItem;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.dto.filter.StoreInFilter;
import uz.uzkassa.silen.enumeration.StockInStatus;
import uz.uzkassa.silen.repository.pg.StoreInRepositoryCustom;
import uz.uzkassa.silen.security.SecurityUtils;

import java.util.List;
import java.util.Optional;

@Repository
public class StoreInRepositoryImpl implements StoreInRepositoryCustom {

    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<StoreIn> findAllByFilter(StoreInFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        final String organizationId = Optional.ofNullable(filter.getOrganizationId()).orElse(SecurityUtils.getCurrentOrganizationId());

        StringBuilder sql = new StringBuilder();
        sql.append(" select o from StoreIn o");
        sql.append(" where o.deleted is not true");
        sql.append(" and o.organizationId = :organizationId");

        if (filter.getStoreId() != null) {
            sql.append(" and o.storeId = :storeId");
        }
        if (filter.getStatus() != null) {
            sql.append(" and o.status =:status");
        }
        if (filter.getTransferType() != null) {
            sql.append(" and o.transferType = :transferType");
        }
        if (filter.getUserId() != null) {
            sql.append(" and o.createdBy = :createdBy");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate >= :from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate <= :to");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.number) like :searchKey");
            //sql.append(" or o.customer.tin like :searchKey");
            //sql.append(" or lower(o.customer.name) like :searchKey");
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

        TypedQuery<StoreIn> query = this.entityManager.createQuery(sql.toString(), StoreIn.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize())
            .setParameter("organizationId", organizationId);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class)
            .setParameter("organizationId", organizationId);

        if (filter.getStatus() != null) {
            query.setParameter("status", filter.getStatus());
            countQuery.setParameter("status", filter.getStatus());
        }
        if (filter.getTransferType() != null) {
            query.setParameter("transferType", filter.getTransferType());
            countQuery.setParameter("transferType", filter.getTransferType());
        }
        if (filter.getStoreId() != null) {
            query.setParameter("storeId", filter.getStoreId());
            countQuery.setParameter("storeId", filter.getStoreId());
        }
        if (filter.getShipmentId() != null) {
            query.setParameter("shipmentId", filter.getShipmentId());
            countQuery.setParameter("shipmentId", filter.getShipmentId());
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


    public List<TopSelectItem> topStoreIn(Filter filter) {
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        StringBuilder sql = new StringBuilder("select p.id as productId, p.visible_name as productName, coalesce(sum(op.qty)) as quantity");
        sql.append(" from store_in o");
        sql.append(" inner join store_in_items op on o.id = op.store_in_id");
        sql.append(" inner join product p on op.product_id = p.id");
        sql.append(" where o.deleted is not true");
        sql.append(" and o.organization_id=:organizationId");
        sql.append(" and o.status = :status");
        sql.append(" and o.last_modified_date >= :from");
        sql.append(" and o.last_modified_date <= :to");
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
        query.setParameter("status", StockInStatus.COMPLETED.name());
        query.setParameter("organizationId", filter.getOrganizationId());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());

        return query.getResultList();
    }
}
