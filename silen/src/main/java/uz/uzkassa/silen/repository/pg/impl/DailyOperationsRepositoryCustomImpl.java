package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.DailyOperations;
import uz.uzkassa.silen.dto.AggregationSum;
import uz.uzkassa.silen.dto.DailyOperationsFilter;
import uz.uzkassa.silen.dto.DailyOperationsMaterialStats;
import uz.uzkassa.silen.dto.DailyOperationsStats;
import uz.uzkassa.silen.enumeration.DailyOperationTypes;
import uz.uzkassa.silen.enumeration.ShipmentStatus;
import uz.uzkassa.silen.enumeration.StockInStatus;
import uz.uzkassa.silen.enumeration.WarehouseOperation;
import uz.uzkassa.silen.repository.pg.DailyOperationsRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;

public class DailyOperationsRepositoryCustomImpl implements DailyOperationsRepositoryCustom {

    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<DailyOperations> findAllByFilter(DailyOperationsFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from DailyOperations o");
        sql.append(" where o.deleted is not true");
        sql.append(" and o.organizationId=:organizationId");

        if (filter.getProductId() != null) {
            sql.append(" and o.productId=:productId");
        }
        if (filter.getType() != null) {
            sql.append(" and o.type=:type");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.operationDate>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.operationDate<=:to");
        }
//        if (hasSearch) {
//            sql.append(" and (lower(o.) like :searchKey");
//            sql.append(" or lower(o.) like :searchKey )");
//        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)");
        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.operationDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<DailyOperations> query = this.entityManager.createQuery(sql.toString(), DailyOperations.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize())
                .setParameter("organizationId", filter.getOrganizationId());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);
        countQuery.setParameter("organizationId", filter.getOrganizationId());

        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.getType() != null) {
            query.setParameter("type", filter.getType());
            countQuery.setParameter("type", filter.getType());
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

    public DailyOperationsStats getStats(DailyOperationsFilter filter) {
        StringBuilder sql = new StringBuilder("select " +
                " greatest(SUM(amount) filter (where type = :production), 0) as production," +
                " greatest(SUM(amount) filter (where type = :shipment), 0)   as shipment," +
                " greatest(SUM(amount) filter (where type = :returned), 0)   as returned," +
                " greatest(SUM(amount) filter (where type = :dropout), 0)    as dropout" +
                " from daily_operations o");

        sql.append(" where o.organization_id=:organizationId");

        if (filter.getProductId() != null) {
            sql.append(" and o.product_id=:productId");
        }
        if (filter.getType() != null) {
            sql.append(" and o.type=:type");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.operation_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.operation_date<=:to");
        }

        Query query = this.entityManager.createNativeQuery(sql.toString(), "DailyOperationsStatsMapper");
        query.setParameter("organizationId", filter.getOrganizationId())
                .setParameter("production", DailyOperationTypes.Production.name())
                .setParameter("shipment", DailyOperationTypes.Shipment.name())
                .setParameter("returned", DailyOperationTypes.Returned.name())
                .setParameter("dropout", DailyOperationTypes.Dropout.name());

        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
        }

        if (filter.getType() != null) {
            query.setParameter("type", filter.getType().name());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
        }

        return (DailyOperationsStats) query.getSingleResult();
    }

    public List<DailyOperationsMaterialStats> materialReport(DailyOperationsFilter filter) {
        StringBuilder sql = new StringBuilder("with t as ( ");
        sql.append(" select o.product_id, o.serial_id,");
        sql.append(" greatest(sum(o.amount) filter (where type = :production),0)  as productionQty,");
        sql.append(" greatest(sum(o.amount) filter (where type = :shipped), 0)    as shippedQty,");
        sql.append(" greatest(sum(o.amount) filter (where type = :returned), 0)   as returnedQty,");
        sql.append(" greatest(sum(o.amount) filter (where type = :dropout), 0)    as dropoutQty");
        sql.append(" from daily_operations o");
        sql.append(" where o.organization_id = :organizationId");
        if (filter.getProductId() != null) {
            sql.append(" and o.product_id=:productId");
        }
        if (filter.getSerialId() != null) {
            sql.append(" and o.serial_id=:serialId");
        }
        if (filter.getType() != null) {
            sql.append(" and o.type=:type");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.operation_date >= :from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.operation_date <= :to");
        }
//                sql.append(" and o.deleted is not true");
        sql.append(" group by o.product_id, o.serial_id)");

        sql.append(" select p.id as productId, p.visible_name as productName, p.barcode, p.price,");
        sql.append(" s.id as serialId, s.serial_number as serialNumber, s.production_date as serialProductionDate,");
        sql.append(" t.productionQty, t.shippedQty, t.returnedQty, t.dropoutQty");
        sql.append(" from t");
        sql.append(" left join product p on t.product_id = p.id");
        sql.append(" left join serial_number s on t.serial_id = s.id");

        Query query = this.entityManager.createNativeQuery(sql.toString(), "DailyOperationsMaterialStatsMapper");
        query.setParameter("organizationId", filter.getOrganizationId());
        query.setParameter("production", DailyOperationTypes.Production.name());
        query.setParameter("shipped", DailyOperationTypes.Shipment.name());
        query.setParameter("returned", DailyOperationTypes.Returned.name());
        query.setParameter("dropout", DailyOperationTypes.Dropout.name());

        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
        }

        if (filter.getType() != null) {
            query.setParameter("type", filter.getType().name());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
        }

        return query.getResultList();
    }

    public List<AggregationSum> getProductionQuantity(LocalDateTime from, LocalDateTime to) {
        StringBuilder sql = new StringBuilder("select o.organization_id as organizationId, o.product_id as productId, o.serial_id as serialId, COALESCE(SUM(o.amount), 0) as quantity" +
                " from warehouse o" +
                " where o.operation=:operation and o.deleted is not true");
        sql.append(" and o.amount > 0");
        sql.append(" and o.operation_date>=:from");
        sql.append(" and o.operation_date<=:to");
        sql.append(" group by o.organization_id, o.product_id, o.serial_id");
        Query query = this.entityManager.createNativeQuery(sql.toString(), "AggregationQuantityMapper");
        query.setParameter("operation", WarehouseOperation.Production.name());
        query.setParameter("from", from);
        query.setParameter("to", to);

        return (List<AggregationSum>) query.getResultList();
    }

    public List<AggregationSum> getStoreInQuantity(LocalDateTime from, LocalDateTime to) {
        StringBuilder sql = new StringBuilder("select o.organization_id as organizationId, i.product_id as productId, i.serial_id as serialId, COALESCE(SUM(i.qty), 0) as quantity");
        sql.append(" from store_in o");
        sql.append(" inner join store_in_items i on o.id = i.shipment_id");
        sql.append(" where o.status=:status and o.deleted is not true");

        sql.append(" and o.last_modified_date>=:from");
        sql.append(" and o.last_modified_date<=:to");
        sql.append(" group by o.organization_id, i.product_id, i.serial_id");
        Query query = this.entityManager.createNativeQuery(sql.toString(), "AggregationQuantityMapper");
        query.setParameter("status", StockInStatus.COMPLETED.name());
        query.setParameter("from", from);
        query.setParameter("to", to);

        return (List<AggregationSum>) query.getResultList();
    }

    public List<AggregationSum> getDropoutQuantity(LocalDateTime from, LocalDateTime to) {
        StringBuilder sql = new StringBuilder("select o.organization_id as organizationId, o.product_id as productId, o.serial_id as serialId, COALESCE(SUM(o.amount), 0) as quantity");
        sql.append(" from dropout o");
        sql.append(" where o.deleted is not true");

        sql.append(" and o.last_modified_date>=:from");
        sql.append(" and o.last_modified_date<=:to");
        sql.append(" group by o.organization_id, o.product_id, o.serial_id");
        Query query = this.entityManager.createNativeQuery(sql.toString(), "AggregationQuantityMapper");
        query.setParameter("from", from);
        query.setParameter("to", to);

        return (List<AggregationSum>) query.getResultList();
    }

    public List<AggregationSum> getShippedQuantity(LocalDateTime from, LocalDateTime to) {
        StringBuilder sql = new StringBuilder("select o.organization_id as organizationId, i.product_id as productId, i.serial_id as serialId, COALESCE(SUM(i.qty), 0) as quantity");
        sql.append(" from shipment o");
        sql.append(" inner join shipment_item i on o.id = i.shipment_id");
        sql.append(" where o.status=:status and o.deleted is not true");

        sql.append(" and o.last_modified_date>=:from");
        sql.append(" and o.last_modified_date<=:to");
        sql.append(" group by o.organization_id, i.product_id, i.serial_id");
        Query query = this.entityManager.createNativeQuery(sql.toString(), "AggregationQuantityMapper");
        query.setParameter("status", ShipmentStatus.CLOSED.name());
        query.setParameter("from", from);
        query.setParameter("to", to);

        return (List<AggregationSum>) query.getResultList();
    }

}
