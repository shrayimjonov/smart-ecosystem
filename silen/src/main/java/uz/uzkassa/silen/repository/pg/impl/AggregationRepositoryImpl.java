package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.Aggregation;
import uz.uzkassa.silen.dto.TopSelectItem;
import uz.uzkassa.silen.dto.dashboard.DashboardStatsDTO;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.dto.filter.TopFilter;
import uz.uzkassa.silen.dto.marking.AggregationStatsDTO;
import uz.uzkassa.silen.dto.partyaggregation.AggregationStateMessage;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.repository.pg.AggregationRepositoryCustom;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AggregationRepositoryImpl implements AggregationRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<Aggregation> findAllByFilter(AggregationFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from Aggregation o ");
        sql.append(" where o.organizationId=:organizationId");

        if (filter.getAggregationStatus() != null) {
            sql.append(" and o.status=:status");
        } else {
            if (CollectionUtils.isNotEmpty(filter.getAggregationStatusList())) {
                String statusList = filter.getAggregationStatusList().stream().map(status -> "'" + status + "'").collect(Collectors.joining(","));
                sql.append(" and o.status in (").append(statusList).append(")");
            }
        }
//        if (filter.getOrganizationId() != null) {
//            sql.append(" and o.organizationId=:organizationId");
//        }
        if (filter.getPartyAggregationId() != null) {
            sql.append(" and o.partyAggregationId=:partyAggregationId");
        }
        if (filter.getPartitionId() != null) {
            sql.append(" and o.partitionId=:partitionId");
        }
        if (filter.getType() != null) {
            sql.append(" and o.aggregationType=:aggregationType");
        }
        if (filter.getBatchId() != null) {
            sql.append(" and o.batchId=:batchId");
        }
        if (filter.getSerialId() != null) {
            sql.append(" and o.serialId=:serialId");
        }
        if (filter.getUnit() != null) {
            sql.append(" and o.unit=:unit");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.lastModifiedDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.lastModifiedDate<=:to");
        }
        if (filter.getProductId() != null) {
            sql.append(" and o.productId=:productId");
        }
        if (filter.getLogin() != null) {
            sql.append(" and lower(o.login) like :login");
        }
        if (hasSearch) {
            sql.append(" and (lower(o.code) like :searchKey)");
        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.lastModifiedDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Aggregation> query = this.entityManager.createQuery(sql.toString(), Aggregation.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

//        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
//        }
        if (filter.getPartyAggregationId() != null) {
            query.setParameter("partyAggregationId", filter.getPartyAggregationId());
            countQuery.setParameter("partyAggregationId", filter.getPartyAggregationId());
        }
        if (filter.getType() != null) {
            query.setParameter("aggregationType", filter.getType());
            countQuery.setParameter("aggregationType", filter.getType());
        }
        if (filter.getBatchId() != null) {
            query.setParameter("batchId", filter.getBatchId());
            countQuery.setParameter("batchId", filter.getBatchId());
        }
        if (filter.getSerialId() != null) {
            query.setParameter("serialId", filter.getSerialId());
            countQuery.setParameter("serialId", filter.getSerialId());
        }
        if (filter.getAggregationStatus() != null) {
            query.setParameter("status", filter.getAggregationStatus());
            countQuery.setParameter("status", filter.getAggregationStatus());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.getUnit() != null) {
            query.setParameter("unit", filter.getUnit().getCode());
            countQuery.setParameter("unit", filter.getUnit().getCode());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
            countQuery.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
            countQuery.setParameter("to", filter.getTo());
        }
        if (filter.getLogin() != null) {
            query.setParameter("login", "%" + filter.getLogin().trim().toLowerCase() + "%");
            countQuery.setParameter("login", "%" + filter.getLogin().trim().toLowerCase() + "%");
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }
        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }

    @Override
    public AggregationStatsDTO getStats(AggregationFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        StringBuilder sql = new StringBuilder("select ")
                .append(" count(a.id) filter (where a.unit = ").append(PackageType.BOX.getCode()).append(") as boxQuantity,")
                .append(" count(a.id) filter (where a.unit = ").append(PackageType.PALLET.getCode()).append(") as palletQuantity,")
                .append(" 0 as blockQuantity,")
                .append(" coalesce(sum(a.quantity), 0) as bottleQuantity")
                .append(" from aggregation a");
        if (filter.getProductId() != null) {
            sql.append(" inner join product p on p.id=:productId");
        }
        sql.append(" where a.deleted is not true ");

        if (filter.getAggregationStatus() != null) {
            sql.append(" and a.status=:status");
        } else {
            if (CollectionUtils.isNotEmpty(filter.getAggregationStatusList())) {
                String statusList = filter.getAggregationStatusList().stream().map(status -> "'" + status + "'").collect(Collectors.joining(","));
                sql.append(" and a.status in (").append(statusList).append(")");
            }
        }
        if (filter.getOrganizationId() != null) {
            sql.append(" and a.organization_id=:organizationId");
        }
        if (filter.getPartyAggregationId() != null) {
            sql.append(" and a.party_aggregation_id=:partyAggregationId");
        }
        if (filter.getType() != null) {
            sql.append(" and a.aggregation_type=:aggregationType");
        }
        if (filter.getBatchId() != null) {
            sql.append(" and a.batch_id=:batchId");
        }
        if (filter.getSerialId() != null) {
            sql.append(" and a.serial_id=:serialId");
        }
        if (filter.getUnit() != null) {
            sql.append(" and a.unit=:unit");
        }
        if (filter.getProductId() != null) {
            sql.append(" and a.product_id=:productId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and a.last_modified_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and a.last_modified_date<=:to");
        }
        if (filter.getLogin() != null) {
            sql.append(" and lower(a.login) like :login");
        }
        if (hasSearch) {
            sql.append(" and (lower(a.code) like :searchKey");
            sql.append(")");
        }
        Query query = entityManager.createNativeQuery(sql.toString(), "AggregationStatsMapper");
        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getPartyAggregationId() != null) {
            query.setParameter("partyAggregationId", filter.getPartyAggregationId());
        }
        if (filter.getType() != null) {
            query.setParameter("aggregationType", filter.getType().name());
        }
        if (filter.getBatchId() != null) {
            query.setParameter("batchId", filter.getBatchId());
        }
        if (filter.getSerialId() != null) {
            query.setParameter("serialId", filter.getSerialId());
        }
        if (filter.getAggregationStatus() != null) {
            query.setParameter("status", filter.getAggregationStatus().name());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
        }
        if (filter.getUnit() != null) {
            query.setParameter("unit", filter.getUnit().getCode());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
        }
        if (filter.getLogin() != null) {
            query.setParameter("login", "%" + filter.getLogin().trim().toLowerCase() + "%");
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
        }
        return (AggregationStatsDTO) query.getSingleResult();
    }

    @Override
    public List<String> getOrganizationIdsByFilter(AggregationFilter filter) {
        StringBuilder sql = new StringBuilder("select organization_id from aggregation")
                .append(" where status=:status")
                .append(" and last_modified_date >=:from")
                .append(" and last_modified_date <=:to")
                .append(" group by organization_id ");
        Query query = entityManager.createNativeQuery(sql.toString(), List.class);
        query.setParameter("status", filter.getAggregationStatus().name());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());
        return query.getResultList();
    }

    @Override
    public List<TopSelectItem> topErrors(Filter filter) {
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        StringBuilder sql = new StringBuilder("select p.id as productId, p.visible_name as productName, coalesce(sum(o.quantity), 0) as quantity");
        sql.append(" from aggregation o");
        sql.append(" inner join product p on o.product_id = p.id");
        sql.append(" where o.deleted is not true");
        sql.append(" and o.organization_id=:organizationId");
        sql.append(" and o.status = :status");
        sql.append(" and o.production_date >= :from");
        sql.append(" and o.production_date <= :to");
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
        query.setParameter("status", AggregationStatus.ERROR.name());
        query.setParameter("organizationId", filter.getOrganizationId());
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());


        return query.getResultList();

    }

    @Override
    public List<DashboardStatsDTO> dashboardStats(TopFilter filter) {
        StringBuilder sql = new StringBuilder("select o.organization_id as organizationId,")
                .append(" o.product_id as productId,")
                .append(" o.unit,")
                .append(" count(o.id) as quantity")
                .append(" from aggregation o ");
        sql.append(" where o.deleted is not true and (o.status = 'CREATED' or o.status = 'SHIPPED') ");
        if (filter.getUnit() != null) {
            sql.append(" and o.unit =:unit");
        }
        sql.append(" and o.production_date >= :from");
        sql.append(" and o.production_date <= :to");
        sql.append(" group by o.product_id, o.organization_id, o.unit");

        Query query = entityManager.createNativeQuery(sql.toString(), "TopShippedSelectItemMapper");
        query.setParameter("from", filter.getFrom());
        query.setParameter("to", filter.getTo());
        if (filter.getUnit() != null) {
            query.setParameter("unit", filter.getTo());
        }
        return query.getResultList();
    }

    @Override
    public List<AggregationStateMessage> getPartyAggregations(String partyAggregationId) {
        StringBuilder sql = new StringBuilder("select ")
                .append(" printCode, status")
                .append(" from Aggregation a");
        sql.append(" where a.deleted is not true ");
        sql.append(" and a.partyAggregationId=:partyAggregationId");

        Query query = entityManager.createQuery(sql.toString(), AggregationStateMessage.class);
        query.setParameter("partyAggregationId", partyAggregationId);
        return query.getResultList();
    }

}
