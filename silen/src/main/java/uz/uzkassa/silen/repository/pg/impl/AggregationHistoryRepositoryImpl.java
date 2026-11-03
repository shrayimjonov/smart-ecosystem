package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.AggregationHistory;
import uz.uzkassa.silen.dto.MaterialReportDTO;
import uz.uzkassa.silen.dto.MaterialReportFilter;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.marking.AggregationStatsDTO;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.repository.pg.AggregationHistoryRepositoryCustom;

@Slf4j
public class AggregationHistoryRepositoryImpl implements AggregationHistoryRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<AggregationHistory> findAllByFilter(AggregationFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());

        StringBuilder sql = new StringBuilder("select o from AggregationHistory o");
        sql.append(" where o.deleted is not true");
        sql.append(" and o.organizationId=:organizationId");

        if (filter.getProductId() != null) {
            sql.append(" and o.productId=:productId");
        }
        if (filter.getSerialId() != null) {
            sql.append(" and o.serialId=:serialId");
        }
        if (filter.getAggregationStatus() != null) {
            sql.append(" and o.status=:status");
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
        if (filter.getLogin() != null) {
            sql.append(" and lower(o.login) like :login");
        }
        if (hasSearch) {
            sql.append(" and lower(o.code) like :searchKey");
        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.lastModifiedDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<AggregationHistory> query = this.entityManager.createQuery(sql.toString(), AggregationHistory.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize())
            .setParameter("organizationId", filter.getOrganizationId());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class)
            .setParameter("organizationId", filter.getOrganizationId());

        if (filter.getAggregationStatus() != null) {
            query.setParameter("status", filter.getAggregationStatus());
            countQuery.setParameter("status", filter.getAggregationStatus());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.getSerialId() != null) {
            query.setParameter("serialId", filter.getSerialId());
            countQuery.setParameter("serialId", filter.getSerialId());
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
            query.setParameter("login", "%" + filter.getLogin().toLowerCase() + "%");
            countQuery.setParameter("login", "%" + filter.getLogin().toLowerCase() + "%");
        }
        if (hasSearch) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }

    public AggregationStatsDTO getStats(AggregationFilter filter) {
        final boolean hasSearch = filter.checkSearchIsEmpty();
        StringBuilder sql = new StringBuilder("select ")
            .append(" count(o.code) filter (where o.unit = ").append(PackageType.BOX.getCode()).append(") as boxQuantity,")
            .append(" count(o.code) filter (where o.unit = ").append(PackageType.PALLET.getCode()).append(") as palletQuantity,")
            .append(" count(o.code) filter (where o.unit = ").append(PackageType.BLOCK.getCode()).append(") as blockQuantity,")
            .append(" coalesce(sum(o.quantity) ,0) as bottleQuantity");
        sql.append(" from aggregation_history o");
        sql.append(" where o.deleted is not true");
        sql.append(" and o.organization_id=:organizationId");

        if (filter.getAggregationStatus() != null) {
            sql.append(" and o.status=:status");
        }
        if (filter.getProductId() != null) {
            sql.append(" and o.product_id=:productId");
        }
        if (filter.getSerialId() != null) {
            sql.append(" and o.serial_id=:serialId");
        }
        if (filter.getLogin() != null) {
            sql.append(" and lower(o.login) like :login");
        }
        if (filter.getUnit() != null) {
            sql.append(" and o.unit=:unit");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.created_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.created_date<=:to");
        }
        if (hasSearch) {
            sql.append(" and lower(o.code) like :searchKey");
        }
        Query query = entityManager.createNativeQuery(sql.toString(), "AggregationStatsMapper")
        .setParameter("organizationId", filter.getOrganizationId());
        if (filter.getAggregationStatus() != null) {
            query.setParameter("status", filter.getAggregationStatus().name());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
        }
        if (filter.getSerialId() != null) {
            query.setParameter("serialId", filter.getSerialId());
        }
        if (filter.getUnit() != null) {
            query.setParameter("unit", filter.getUnit().getCode());
        }
        if (filter.getLogin() != null) {
            query.setParameter("login", "%" + filter.getLogin().toLowerCase() + "%");
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
        try {
            return (AggregationStatsDTO) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return new AggregationStatsDTO();
        }
    }

    @Override
    public Page<MaterialReportDTO> materialReport(MaterialReportFilter filter) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("with r as (select w.product_id, w.organization_id,");
        sqlBuilder.append(" greatest(SUM(w.amount) filter (where w.operation = 'Production' and w.unit in ('BOX', 'BOTTLE')),0) as productionBottle,");
        sqlBuilder.append(" greatest(SUM(w.amount) filter (where w.operation = 'Utilized' and w.unit in ('BOX', 'BOTTLE') and  w.from_shipment is false), 0) as utilizedBottle,");
        sqlBuilder.append(" greatest(SUM(w.amount) filter (where w.operation = 'Returned' and w.unit in ('BOX', 'BOTTLE')),0) as returnedBottle,");
        sqlBuilder.append(" greatest(SUM(w.amount) filter (where w.operation = 'ShipmentBuyer' and w.unit in ('BOX', 'BOTTLE')), 0)  as shippedBottle,");
        sqlBuilder.append(" greatest(SUM(w.amount) filter (where w.operation = 'Utilized' and w.unit in ('BOX', 'BOTTLE') and w.from_shipment is true), 0) as utilizedFromShipBottle");
        if (filter.getPackageTypes().contains(PackageType.BOX)) {
            sqlBuilder.append(" ,COUNT(w.id) filter (where w.operation = 'Production' and w.unit = 'BOX') as productionBox,");
            sqlBuilder.append(" COUNT(w.id) filter (where w.operation = 'Utilized' and w.unit = 'BOX' and w.from_shipment is false) as utilizedBox,");
            sqlBuilder.append(" COUNT(w.id) filter (where w.operation = 'Returned' and w.unit = 'BOX') as returnedBox,");
            sqlBuilder.append(" COUNT(w.id) filter (where w.operation = 'ShipmentBuyer' and w.unit = 'BOX') as shippedBox,");
            sqlBuilder.append(" COUNT(w.id) filter (where w.operation = 'Utilized' and w.unit = 'BOX' and w.from_shipment is true) as utilizedFromShipBox");
        }
        if (filter.getPackageTypes().contains(PackageType.PALLET)) {
            sqlBuilder.append(" ,COUNT(w.id) filter (where w.operation = 'Production' and w.unit = 'PALLET') as productionPallet,");
            sqlBuilder.append(" COUNT(w.id) filter (where w.operation = 'Utilized' and w.unit = 'PALLET') as utilizedPallet,");
            sqlBuilder.append(" COUNT(w.id) filter (where w.operation = 'Returned' and w.unit = 'PALLET') as returnedPallet,");
            sqlBuilder.append(" COUNT(w.id) filter (where w.operation = 'ShipmentBuyer' and w.unit = 'PALLET') as shippedPallet,");
            sqlBuilder.append(" COUNT(w.id) filter (where w.operation = 'Utilized' and w.unit = 'PALLET' and w.from_shipment is true) as utilizedFromShipPallet");
        }
        sqlBuilder.append(" from warehouse w");

        StringBuilder countSql = new StringBuilder("select count(w.product_id) as qty from warehouse w");
        if (filter.getOrganizationId() != null) {
            sqlBuilder.append(" where w.organization_id =:organizationId");
            countSql.append(" where w.organization_id =:organizationId");
        }
        if (filter.getFrom() != null) {
            sqlBuilder.append(" and w.operation_date>=:from");
            countSql.append(" and w.operation_date>=:from");
        }
        if (filter.getTo() != null) {
            sqlBuilder.append(" and w.operation_date<=:to");
            countSql.append(" and w.operation_date<=:to");
        }
        if (filter.getProductId() != null) {
            sqlBuilder.append(" and w.product_id=:productId");
            countSql.append(" and w.product_id=:productId");
        }
        sqlBuilder.append(" group by w.product_id,w.organization_id )");
        countSql.append(" group by w.product_id,w.organization_id");

        sqlBuilder.append(" select r.product_id as productId, p.visible_name as productName, p.price,");
        sqlBuilder.append(" r.productionBottle - r.utilizedBottle as productionBottle,");
        sqlBuilder.append(" r.shippedBottle - r.returnedBottle - r.utilizedFromShipBottle as shippedBottle");
        if (filter.getPackageTypes().contains(PackageType.BOX)) {
            sqlBuilder.append(" ,r.productionBox - r.utilizedBox as productionBox");
            sqlBuilder.append(" ,r.shippedBox - r.returnedBox - r.utilizedFromShipBox as shippedBox");
        } else {
            sqlBuilder.append(" ,0 as productionBox");
            sqlBuilder.append(" ,0 as shippedBox");
        }
        if (filter.getPackageTypes().contains(PackageType.PALLET)) {
            sqlBuilder.append(" ,r.productionPallet - r.utilizedPallet as productionPallet");
            sqlBuilder.append(" ,r.shippedPallet - r.returnedPallet - r.utilizedFromShipPallet as shippedPallet");
        } else {
            sqlBuilder.append(" ,0 as productionPallet");
            sqlBuilder.append(" ,0 as shippedPallet");
        }
        sqlBuilder.append(" from r inner join product p on p.id = r.product_id");


        Query query = this.entityManager.createNativeQuery(sqlBuilder.toString(), "MaterialReportMapper")
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());
        Query countQuery = this.entityManager.createNativeQuery(" select count(w.qty) from (" + countSql + ") as w");

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
            countQuery.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
            countQuery.setParameter("to", filter.getTo());
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        return new PageImpl<MaterialReportDTO>(query.getResultList(), filter.getPageable(), (Long) countQuery.getSingleResult());
    }
}
