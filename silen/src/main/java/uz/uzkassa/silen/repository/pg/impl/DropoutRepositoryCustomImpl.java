package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.config.CacheConstants;
import uz.uzkassa.silen.domain.Dropout;
import uz.uzkassa.silen.dto.DropoutStatsDTO;
import uz.uzkassa.silen.dto.TopSelectItem;
import uz.uzkassa.silen.dto.filter.DropoutFilter;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.enumeration.CodeType;
import uz.uzkassa.silen.repository.pg.DropoutRepositoryCustom;

import java.util.List;

public class DropoutRepositoryCustomImpl implements DropoutRepositoryCustom, CacheConstants {
    @PersistenceContext(unitName = "slavePU")
    EntityManager entityManager;

    @Override
    public Page<Dropout> findAllByFilter(DropoutFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        StringBuilder sql = new StringBuilder("select o from Dropout o");
        sql.append(" where o.deleted is not true");
        sql.append(" and o.organizationId=:organizationId");
        if (filter.getDropoutReason() != null) {
            sql.append(" and o.dropoutReason=:dropoutReason");
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
            sql.append(" and (lower(o.printCode) like :searchKey");
            sql.append(" or lower(o.createdBy) like :searchKey )");
        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)");
        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.lastModifiedDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Dropout> query = this.entityManager.createQuery(sql.toString(), Dropout.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize())
                .setParameter("organizationId", filter.getOrganizationId());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);
        countQuery.setParameter("organizationId", filter.getOrganizationId());

        if (filter.getDropoutReason() != null) {
            query.setParameter("dropoutReason", filter.getDropoutReason());
            countQuery.setParameter("dropoutReason", filter.getDropoutReason());
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

    @Override
    public DropoutStatsDTO findAllByFilterStats(DropoutFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();

        StringBuilder sql = new StringBuilder("select count(d.code) filter ( where d.code_type=:aggregation) as boxCount,");
        sql.append(" count(d.code) filter ( where d.code_type=:block) as blockCount,");
        sql.append(" count(d.code) filter ( where d.code_type=:mark) as bottleCount");
        sql.append(" from dropout d");
        sql.append(" where d.deleted is not true");
        sql.append(" and d.organization_id=:organizationId");

        if (filter.getDropoutReason() != null) {
            sql.append(" and d.dropout_reason=:dropoutReason");
        }
        if (filter.getProductId() != null) {
            sql.append(" and d.product_id=:productId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and d.last_modified_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and d.last_modified_date<=:to");
        }
        if (hasSearch) {
            sql.append(" and (d.code like :searchKey");
            sql.append(" or d.created_by like :searchKey )");
        }

        Query query = this.entityManager.createNativeQuery(sql.toString(), "DropoutStatsMapper")
                .setParameter("organizationId", filter.getOrganizationId())
                .setParameter("aggregation", CodeType.AGGREGATION.name())
                .setParameter("block", CodeType.BLOCK.name())
                .setParameter("mark", CodeType.MARK.name());

        if (filter.getDropoutReason() != null) {
            query.setParameter("dropoutReason", filter.getDropoutReason().name());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
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
        return (DropoutStatsDTO) query.getSingleResult();
    }

    @Override
    public List<TopSelectItem> topDropout(Filter filter) {
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        StringBuilder sql = new StringBuilder("select p.id as productId, p.visible_name as productName,  coalesce(sum(o.amount), 0) as quantity");
        sql.append(" from dropout o");
        sql.append(" inner join product p on o.product_id = p.id");
        sql.append(" where o.organization_id=:organizationId");
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
        query.setParameter("organizationId", filter.getOrganizationId());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());


        return query.getResultList();
    }
}
