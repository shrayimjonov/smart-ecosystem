package uz.uzkassa.silen.repository.pg.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.silen.domain.Warehouse;
import uz.uzkassa.silen.dto.dashboard.DashboardFilter;
import uz.uzkassa.silen.dto.filter.WarehouseFilter;
import uz.uzkassa.silen.dto.warehouse.WarehouseOperationStatsDTO;
import uz.uzkassa.silen.dto.warehouse.WarehouseStatsDTO;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.WarehouseOperation;
import uz.uzkassa.silen.repository.pg.WarehouseRepositoryCustom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarehouseRepositoryImpl implements WarehouseRepositoryCustom {
    @PersistenceContext(unitName = "slavePU")
    private EntityManager entityManager;

    @Override
    public Page<Warehouse> findAllByFilter(WarehouseFilter filter) {
//        final boolean hasSearch = filter.checkSearchIsEmpty();
        final boolean hasSort = StringUtils.isNotEmpty(filter.getOrderBy());
        List<WarehouseOperation> operationList = new ArrayList<>(Arrays.asList(WarehouseOperation.Utilized, WarehouseOperation.Returned));

        StringBuilder sql = new StringBuilder("select o from Warehouse o");
        sql.append(" where o.deleted is not true and unit not in ('PALLET','BOTTLE','UNIT')");
        if (filter.getOrganizationId() != null) {
            sql.append(" and o.organizationId=:organizationId");
        }
        if (filter.getOperation() != null) {
            operationList.add(filter.getOperation());
            sql.append(" and o.operation in ( :operation )");
        }
        if (filter.getProductId() != null) {
            sql.append(" and o.productId=:productId");
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
//        if (hasSearch) {
//            sql.append("and (");
//            sql.append(" or lower(o.product.name) like :searchKey");
//            sql.append(" or lower(o.organization.name) like :searchKey");
//            sql.append(" or lower(o.organization.tin) like :searchKey");
//            sql.append(" or lower(o.toOrganization.tin) like :searchKey");
//            sql.append(" or lower(o.toOrganization.tin) like :searchKey");
//            sql.append(")");
//        }
        String countSql = sql.toString().replaceFirst("select o", "select count(o.id)");

        sql.append(" order by");
        if (hasSort) {
            sql.append(" o.").append(filter.getOrderBy());
        } else {
            sql.append(" o.lastModifiedDate");
        }
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Warehouse> query = this.entityManager.createQuery(sql.toString(), Warehouse.class).setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
            countQuery.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getOperation() != null) {
            query.setParameter("operation", operationList);
            countQuery.setParameter("operation", operationList);
        }
        if (filter.getProductId() != null) {
            query.setParameter("productId", filter.getProductId());
            countQuery.setParameter("productId", filter.getProductId());
        }
        if (filter.getUnit() != null) {
            query.setParameter("unit", filter.getUnit());
            countQuery.setParameter("unit", filter.getUnit());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
            countQuery.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
            countQuery.setParameter("to", filter.getTo());
        }
//        if (hasSearch) {
//            query.setParameter("searchKey", filter.getSearchForQuery());
//            countQuery.setParameter("searchKey", filter.getSearchForQuery());
//        }

        return new PageImpl<>(query.getResultList(), filter.getPageable(), countQuery.getSingleResult());
    }

    @Override
    public List<WarehouseStatsDTO> getWarehouseStats(DashboardFilter filter, Boolean isFull) {
        StringBuilder sql = new StringBuilder("select ");
        if (isFull)
            sql.append(" w.organization_id as organizationId, o.name as organizationName, o.type as organizationType,");
        sql.append(" p.type as productType,");
        sql.append("greatest (SUM(amount) filter (where operation='Production'),0) as production,\n" +
            " greatest (SUM(amount) filter (where operation='ShipmentBuyer'),0) as shipment, \n");
//            + "greatest (SUM(amount) filter (where operation='Losses'),0) as losses, \n"
//            + "greatest (SUM(amount) filter (where operation='Income'),0) as income, \n");
        sql.append("coalesce(")
            .append("greatest (SUM(w.amount) filter (where operation='Production'),0)")
//            .append(" + greatest (SUM(w.amount) filter (where operation='Income'),0)")
            .append(" - greatest(SUM(w.amount) filter (where operation='ShipmentBuyer'),0)")
            .append(" ,0) as balance");
        sql.append(" from warehouse w");
        sql.append(" inner join product p on w.product_id = p.id");
        sql.append(" inner join organization o on w.organization_id = o.id");
        sql.append(" where w.deleted is not true");

        if (filter.getWarehouseOperations() != null) {
            sql.append(" and w.operation in (");
            sql.append(filter.getWarehouseOperationsString());
            sql.append(")");
        }
        if (filter.getProductTypes() != null) {
            sql.append(" and p.type in (");
            sql.append(filter.getProductTypesString());
            sql.append(")");
        }
        if (filter.getOrganizationTypes() != null) {
            sql.append(" and o.type in (");
            sql.append(filter.getOrganizationTypesString());
            sql.append(")");
        }
        if (filter.getOrganizationId() != null) {
            sql.append(" and w.organization_id=:organizationId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and w.operation_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and w.operation_date<=:to");
        }
        sql.append(" group by p.type");
        if (isFull) sql.append(" ,w.organization_id, o.type, o.name");

        Query query = this.entityManager.createNativeQuery(
            (filter.isConsumer()) ?
                sql.toString().replace("w.organization_id", "w.to_organization_id")
                : sql.toString()
            , (isFull) ? "WarehouseOrgStatsMapper" : "WarehouseStatsMapper");

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("from", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("to", filter.getTo());
        }
        return query.getResultList();
    }

    public WarehouseOperationStatsDTO warehouseStatsByOperationProduction(WarehouseFilter filter) {
        StringBuilder sql = new StringBuilder("select ");
        sql.append(" coalesce(sum(w.amount) filter (where w.operation = :production), 0) -");
        sql.append(" coalesce(sum(w.amount) filter (where (w.operation = :utilized)), 0) -");
        sql.append(" coalesce(sum(w.amount) filter (where w.operation = :returned and unit = :bottle), 0)");
        sql.append("  as amount,");
        sql.append(" count(w.id) filter (where w.operation = :production and w.unit = :block) -");
        sql.append(" count(w.id) filter (where w.operation= :utilized and w.unit = :block)");
        sql.append(" as block,");
        sql.append(" count(w.id) filter (where w.operation = :production and w.unit = :box) -");
        sql.append(" count(w.id) filter (where w.operation= :utilized and w.unit = :box)");
        sql.append(" as box,");
        sql.append(" count(w.id) filter (where w.operation = :production and w.unit = :pallet) -");
        sql.append(" count(w.id) filter (where w.operation = :utilized and w.unit = :pallet)");
        sql.append(" as pallet");
        sql.append(" from warehouse w");
        sql.append(" inner join product p on w.product_id=p.id");
        sql.append(" where w.deleted is not true");
        sql.append(" and w.operation in (:production, :utilized, :returned)");

        if (filter.getProductType() != null) {
            sql.append(" and p.type='").append(filter.getProductType().name()).append("'");
        }
        if (filter.getProductId() != null) {
            sql.append(" and w.product_id=:productId");
        }
        if (filter.getOrganizationId() != null) {
            sql.append(" and w.organization_id=:organizationId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and w.last_modified_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and w.last_modified_date<=:to");
        }

        Query query = this.entityManager.createNativeQuery(sql.toString(), "WarehouseOperationStatsMapper");
        query.setParameter("box", PackageType.BOX.name())
        .setParameter("block", PackageType.BLOCK.name())
            .setParameter("bottle", PackageType.BOTTLE.name())
            .setParameter("pallet", PackageType.PALLET.name())
            .setParameter("production", WarehouseOperation.Production.name())
            .setParameter("returned", WarehouseOperation.Returned.name())
            .setParameter("utilized", WarehouseOperation.Utilized.name());

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
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
        return (WarehouseOperationStatsDTO) query.getSingleResult();
    }

    public WarehouseOperationStatsDTO warehouseStatsByOperationShipmentBuyer(WarehouseFilter filter) {
        StringBuilder sql = new StringBuilder("select ");
        sql.append(" coalesce(sum(w.amount) filter (where w.operation = :shipment), 0) -");
        sql.append(" coalesce(sum(w.amount) filter (where w.operation = :returned), 0)");
        sql.append(" as amount,");
        sql.append(" count(w.id) filter (where w.operation = :shipment and w.unit = :block) -");
        sql.append(" count(w.id) filter ( where w.operation = :returned and unit = :block)");
        sql.append(" as block,");
        sql.append(" count(w.id) filter (where w.operation = :shipment and w.unit = :box) -");
        sql.append(" count(w.id) filter ( where w.operation = :returned and unit = :box)");
        sql.append(" as box,");
        sql.append(" count(w.id) filter (where w.operation = :shipment and w.unit = :pallet) -");
        sql.append(" count(w.id) filter (where w.operation = :returned and w.unit = :pallet)");
        sql.append(" as pallet");
        sql.append(" from warehouse w");
        sql.append(" inner join product p on w.product_id=p.id");
        sql.append(" where w.deleted=false");
        sql.append(" and w.operation in (:shipment, :returned)");

        if (filter.getProductType() != null) {
            sql.append(" and p.type='").append(filter.getProductType().name()).append("'");
        }
        if (filter.getProductId() != null) {
            sql.append(" and w.product_id=:productId");
        }
        if (filter.getOrganizationId() != null) {
            sql.append(" and w.organization_id=:organizationId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and w.last_modified_date>=:from");
        }
        if (filter.getTo() != null) {
            sql.append(" and w.last_modified_date<=:to");
        }

        Query query = this.entityManager.createNativeQuery(sql.toString(), "WarehouseOperationStatsMapper");
        query.setParameter("block", PackageType.BLOCK.name())
            .setParameter("box", PackageType.BOX.name())
            .setParameter("pallet", PackageType.PALLET.name())
            .setParameter("shipment", WarehouseOperation.ShipmentBuyer.name())
            .setParameter("returned", WarehouseOperation.Returned.name());

        if (filter.getOrganizationId() != null) {
            query.setParameter("organizationId", filter.getOrganizationId());
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

        return (WarehouseOperationStatsDTO) query.getSingleResult();
    }

}
