package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.BatchAggregation;
import uz.uzkassa.silen.dto.filter.BatchFilter;
import uz.uzkassa.silen.dto.marking.BatchAggregationListDTO;
import uz.uzkassa.silen.repository.pg.BatchAggregationRepositoryCustom;

public class BatchAggregationRepositoryImpl implements BatchAggregationRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<BatchAggregation> findAllByFilter(BatchFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        final String organizationId = filter.getOrganizationId();

        StringBuilder sql = new StringBuilder("select o from BatchAggregation o");
        sql.append(" where o.deleted is not true");
        if (organizationId != null) {
            sql.append(" and o.organizationId=:organizationId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.createdDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.createdDate<=:to");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.number) like :searchKey");
            sql.append(" or lower(o.description) like :searchKey");
            sql.append(" or o.gcp like :searchKey");
            sql.append(")");
        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.createdDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<BatchAggregation> query = this.entityManager.createQuery(sql.toString(), BatchAggregation.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (organizationId != null) {
            query.setParameter("organizationId", organizationId);
            countQuery.setParameter("organizationId", organizationId);
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
    public Page<BatchAggregationListDTO> findAllByFilterBatch(BatchFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        final String organizationId = filter.getOrganizationId();

        StringBuilder sql = new StringBuilder("with aggregations as (select batch_id, count(id) from aggregation");
//        sql.append(" where deleted is not true");
//        if (filter.getOrganizationId() != null) {
//            sql.append(" and organization_id=:organizationId");
//        }
//        if (filter.getFrom() != null) {
//            sql.append(" and created_date>=:from");
//        }
//        if (filter.getTo() != null) {
//            sql.append(" and created_date<=:to");
//        }
//        if (hasSearch) {
//            sql.append(" and (lower(description) like :searchKey )");
//        }
//        sql.append(" and status = 'DRAFT' group by batch_id)");
        sql.append(" where status = 'DRAFT' group by batch_id)");
        sql.append(" select o.id as id,");
        sql.append(" o.number as number,");
        sql.append(" o.gcp as gcp,");
        sql.append(" o.unit as unit,");
        sql.append(" o.quantity as quantity,");
        sql.append(" coalesce(a.count, 0) as unUsed,");
        sql.append(" o.description as description,");
        sql.append(" o.created_date as createdDate");
        sql.append(" from batch_aggregation o left join aggregations a on o.id=a.batch_id");
        sql.append(" where o.deleted is not true");
        if (organizationId != null) {
            sql.append(" and o.organization_id=:organizationId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.created_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.created_date<=:to");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.description) like :searchKey )");
        }

        String countSql = "select count(t.id) from (" + sql + ") t";

        sql.append(" order by");
        if (hasSort) {
            sql.append(" ").append(filter.getOrderBy());
        } else {
            sql.append(" o.created_date");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        Query query = this.entityManager.createNativeQuery(sql.toString(), "BatchAggregationMapper")
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());
        Query countQuery = entityManager.createNativeQuery(countSql);

        if (organizationId != null) {
            query.setParameter("organizationId", organizationId);
            countQuery.setParameter("organizationId", organizationId);
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

        return new PageImpl<>(query.getResultList(), filter.getPageable(), (Long) countQuery.getSingleResult());
    }

    @Override
    public Long getStats(BatchFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        StringBuilder sql = new StringBuilder("select count(id) from aggregation");
        sql.append(" where deleted is not true");
        if (filter.getOrganizationId() != null) {
            sql.append(" and organization_id=:organizationId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and created_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and created_date<=:to");
        }
        sql.append(" and status = 'DRAFT'");
        if (hasSearch) {
            sql.append(" and ( lower(gcp) like :searchKey )");
        }
        Query countQuery = entityManager.createNativeQuery(sql.toString());

        if (filter.getOrganizationId() != null) {
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getFrom() != null) {
            countQuery.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            countQuery.setParameter("to", filter.getTo());
        }
        if (hasSearch) {
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }
        return (Long) countQuery.getSingleResult();
    }
}
