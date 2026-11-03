package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.product.WarehouseExpeditorMapDTO;
import uz.uzkassa.smartpossupply.dto.warehouseexpeditor.WarehouseExpeditorStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.WarehouseExpeditorRepositoryCustom;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.11.2024
 */
public class WarehouseExpeditorRepositoryImpl extends BaseRepositoryImpl implements WarehouseExpeditorRepositoryCustom {

    @Override
    public WarehouseExpeditorStatisticsDTO getStatistics(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append(" count(distinct e.id) as totalExpeditorCount, ");
        sql.append(" count(distinct pre.plan_route_id) as totalPlanRouteCount, ");
        sql.append(" count(distinct o.id) as totalOrderCount, ");
        sql.append(" count(distinct o.id) filter ( where o.status = 'COMPLETED' ) as totalCompletedOrderCount ");
        sql.append(getBaseSql(filter));
        Query query = entityManager.createNativeQuery(sql.toString(), "WarehouseExpeditorStatisticsMapper");

        query.setParameter("companyId", filter.getCompanyId());

        query.setParameter("stockTransferStatuses", List.of(StockTransferStatus.DRAFT.getCode(), StockTransferStatus.REJECTED.getCode()));

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        return (WarehouseExpeditorStatisticsDTO) query.getSingleResult();
    }

    @Override
    public ResultList<WarehouseExpeditorMapDTO> getList(BaseFilter filter) {
        ResultList<WarehouseExpeditorMapDTO> resultList = new ResultList<>();
        filter.setCompanyId(getCompanyId(filter));
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append(" e.id as expeditorId, ");
        sql.append(" e.first_name as expeditorFirstName, ");
        sql.append(" e.last_name as expeditorLastName, ");
        sql.append(" count(distinct o.id) as totalOrderCount, ");
        sql.append(" count(distinct o.id) filter ( where o.status = '").append(OrderStatus.COMPLETED.getCode()).append("')");
        sql.append(" as totalCompletedOrderCount, ");
        sql.append(" sum(st.total_qty) as totalQty, ");
        sql.append(" sum(s.total_qty) as totalSoldQty ");
        sql.append(getBaseSql(filter));
        sql.append(" group by e.id ");
        Query query = entityManager.createNativeQuery(sql.toString(), "WarehouseExpeditorMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        Query countQuery = entityManager.createNativeQuery("select count(distinct e.id) ".concat(getBaseSql(filter).toString()), Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        query.setParameter("stockTransferStatuses", List.of(StockTransferStatus.DRAFT.getCode(), StockTransferStatus.REJECTED.getCode()));
        countQuery.setParameter("stockTransferStatuses", List.of(StockTransferStatus.DRAFT.getCode(), StockTransferStatus.REJECTED.getCode()));

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
            countQuery.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setCount(count);
            resultList.setList(query.getResultList());
        }
        return resultList;
    }

    @Override
    public ResultList<WarehouseExpeditorMapDTO> getCarList(BaseFilter filter) {
        ResultList<WarehouseExpeditorMapDTO> resultList = new ResultList<>();
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        filter.setWarehouseId(getWarehouseId(filter, false));
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append(" car.id as carId, ");
        sql.append(" car.name as carName, ");
        sql.append(" car.truck_reg_no as carRegNo, ");
        sql.append(" car.truck_model as carModel, ");
        sql.append(" sum(st.total_qty) as totalQty, ");
        sql.append(" sum(s.total_qty) as totalSoldQty ");
        sql.append(getCarListBaseSql(filter));
        sql.append(" group by car.id ");
        Query query = entityManager.createNativeQuery(sql.toString(), "WarehouseExpeditorCarMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        Query countQuery = entityManager.createNativeQuery("select count(distinct car.id) ".concat(getCarListBaseSql(filter).toString()), Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        query.setParameter("stockTransferStatuses", List.of(StockTransferStatus.DRAFT.getCode(), StockTransferStatus.REJECTED.getCode()));
        countQuery.setParameter("stockTransferStatuses", List.of(StockTransferStatus.DRAFT.getCode(), StockTransferStatus.REJECTED.getCode()));

        if (filter.getUserId() != null) {
            query.setParameter("expeditorId", filter.getUserId());
            countQuery.setParameter("expeditorId", filter.getUserId());
        }

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getWarehouseId() != null) {
            query.setParameter("warehouseId", filter.getWarehouseId());
            countQuery.setParameter("warehouseId", filter.getWarehouseId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setCount(count);
            resultList.setList(query.getResultList());
        }
        return resultList;
    }

    public StringBuilder getBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from users e ");
        sql.append(" inner join warehouse_expeditor we on e.id = we.expeditor_id ");
        sql.append(" inner join plan_route_expeditor pre on e.id = pre.expeditor_id ");
        sql.append(" inner join stock_transfer st on we.stock_transfer_id = st.id ");
        sql.append(" left join stock s on s.creator_id = e.id ");
        sql.append(" left join orders o on o.expeditor_id = e.id ");
        sql.append(" where o.deleted = false ");
        sql.append(" and e.deleted = false ");
        sql.append(" and e.status ='").append(UserStatus.ACTIVE.getCode()).append("' ");
        sql.append(" and st.status not in (:stockTransferStatuses)");
        sql.append(" and s.status ='").append(StockStatus.COMPLETED.getCode()).append("' ");
        sql.append(" and e.role ='").append(Role.ROLE_EXPEDITOR.getCode()).append("' ");
        sql.append(" and s.transfer_type = '").append(TransferType.OUT.name()).append("' ");
        sql.append(" and e.company_id=:companyId ");
        if (filter.getBranchId() != null) {
            sql.append(" and e.branch_id=:branchId ");
            sql.append(" and o.branch_id=:branchId");
            sql.append(" and s.branch_id=:branchId ");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and e.warehouse_id=:warehouseId ");
            sql.append(" and o.warehouse_id=:warehouseId");
            sql.append(" and s.warehouse_id=:warehouseId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and st.transfer_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and st.transfer_date<=:toDate ");
        }
        return sql;
    }

    public StringBuilder getCarListBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from warehouse_expeditor we ");
        sql.append(" inner join users e on we.expeditor_id = e.id ");
        sql.append(" inner join warehouse car on we.car_id = car.id ");
        sql.append(" inner join stock_transfer st on we.stock_transfer_id = st.id ");
        sql.append(" left join stock s on s.creator_id = we.expeditor_id ");
        sql.append(" where e.deleted = false ");
        sql.append(" and st.status not in (:stockTransferStatuses)");
        sql.append(" and s.status ='").append(StockStatus.COMPLETED.getCode()).append("' ");
        sql.append(" and s.transfer_type = '").append(TransferType.OUT.name()).append("' ");
        sql.append(" and e.company_id=:companyId ");
        if (filter.getUserId() != null) {
            sql.append(" and e.id=:expeditorId ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and e.branch_id=:branchId ");
            sql.append(" and o.branch_id=:branchId");
            sql.append(" and s.branch_id=:branchId ");
        }
        if (filter.getWarehouseId() != null) {
            sql.append(" and e.warehouse_id=:warehouseId ");
            sql.append(" and o.warehouse_id=:warehouseId");
            sql.append(" and s.warehouse_id=:warehouseId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and st.transfer_date>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and st.transfer_date<=:toDate ");
        }
        return sql;
    }
}
