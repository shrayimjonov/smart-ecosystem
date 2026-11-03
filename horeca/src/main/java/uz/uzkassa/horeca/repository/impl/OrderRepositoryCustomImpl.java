package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.order.kanban.*;
import uz.uzkassa.horeca.dto.unit.MultiUnitListDTO;
import uz.uzkassa.horeca.enums.OrderStatus;
import uz.uzkassa.horeca.filters.OrderFilter;
import uz.uzkassa.horeca.repository.OrderRepositoryCustom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepositoryCustomImpl extends BaseRepositoryImpl implements OrderRepositoryCustom {

    @Override
    public ResultList<Order> getResultList(OrderFilter filter) {
        ResultList<Order> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select o ");
        sql.append(getBaseSql(filter));
        String countSql = sql.toString().replace("select o ", "select count(o.id)");
        sql.append(" order by o.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Order> query = entityManager
                .createQuery(sql.toString(), Order.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (filter.getPaymentType() != null) {
            query.setParameter("paymentType", filter.getPaymentType());
            countQuery.setParameter("paymentType", filter.getPaymentType());
        }
        if (filter.getItemsCount() != null) {
            query.setParameter("itemsCount", filter.getItemsCount());
            countQuery.setParameter("itemsCount", filter.getItemsCount());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getClientId() != null) {
            query.setParameter("clientId", filter.getClientId());
            countQuery.setParameter("clientId", filter.getClientId());
        }
        if (filter.getTableId() != null) {
            query.setParameter("tableId", filter.getTableId());
            countQuery.setParameter("tableId", filter.getTableId());
        }
        if (filter.getWaiterId() != null) {
            query.setParameter("waiterId", filter.getWaiterId());
            countQuery.setParameter("waiterId", filter.getWaiterId());
        }
        if (filter.getChefId() != null) {
            query.setParameter("chefId", filter.getChefId());
            countQuery.setParameter("chefId", filter.getChefId());
        }
        if (filter.getShiftId() != null) {
            query.setParameter("shiftId", filter.getShiftId());
            countQuery.setParameter("shiftId", filter.getShiftId());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", OrderStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", OrderStatus.valueOf(filter.getStatus()));
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            List<OrderStatus> statusList = filter.getStatuses().stream().map(OrderStatus::valueOf).toList();
            query.setParameter("statuses", statusList);
            countQuery.setParameter("statuses", statusList);
        }
        if (filter.getFromTotalPrice() != null) {
            query.setParameter("fromTotalPrice", filter.getFromTotalPrice());
            countQuery.setParameter("fromTotalPrice", filter.getFromTotalPrice());
        }
        if (filter.getToTotalPrice() != null) {
            query.setParameter("toTotalPrice", filter.getToTotalPrice());
            countQuery.setParameter("toTotalPrice", filter.getToTotalPrice());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public List<OrderKanbanBoardListDTO> getKanbanBoardList(OrderFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select tb.* from (");
        sql.append("select distinct o.id as id, o.order_number as orderNumber, o.order_date as orderDate,");
        sql.append(" h.id as hallId, h.name as hallName,");
        sql.append(" t.id as tableId, t.name as tableName,");
        sql.append(" (select count(i.id) from order_item i where i.order_id = o.id) as totalCount,");
        sql.append(" (select count(i.id) from order_item i where i.order_id=o.id and i.status='READY') as completedCount");
        sql.append(" from orders o ");
        sql.append(" inner join order_item item on o.id = item.order_id ");
        sql.append(" inner join hall_item t on t.id = o.table_id ");
        sql.append(" inner join hall h on t.hall_id = h.id ");
        sql.append(" inner join menu_item mi on mi.id = item.menu_item_id ");
        sql.append(" inner join product p on p.id = mi.product_id ");
        sql.append(" where o.branch_id=:branchId");
        sql.append(" and o.deleted=false");
        sql.append(" and o.status in ('ACTIVE','NEW')");
        if (StringUtils.isNotEmpty(filter.getItemStatus())) {
            sql.append(" and item.status=:status");
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            sql.append(" and item.status in (:statuses)");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and p.department_id=:departmentId");
        }
        if (filter.getChefId() != null) {
            sql.append(" and item.chef_id=:chefId");
        }
        sql.append(" group by o.id, t.id, h.id");
        sql.append(") tb order by tb.orderDate desc");

        Query query = entityManager.createNativeQuery(sql.toString(), "OrderKanbanBoardListMapper");

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getChefId() != null) {
            query.setParameter("chefId", filter.getChefId());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
        }
        if (StringUtils.isNotEmpty(filter.getItemStatus())) {
            query.setParameter("status", filter.getItemStatus());
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            query.setParameter("statuses", filter.getStatuses());
        }
        return query.getResultList();
    }

    @Override
    public List<OrderKanbanBoardDetailDTO> getKanbanBoardDetails(OrderFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select o.id as id, o.order_number as orderNumber, o.order_date as orderDate, o.comment as comment, o.status as orderStatus,");
        sql.append(" t.id as tableId, t.name as tableName,");
        sql.append(" h.id as hallId, h.name as hallName,");
        sql.append(
                " item.id as itemId, item.qty as qty, item.comment as itemComment, item.status as status, item.order_date as itemOrderDate, item.updated_date as lastModifiedDate, item.marks as marks,"
        );
        if (filter.isMine()) {
            sql.append(" chef.id as chefId, chef.first_name as chefFirstName, chef.last_name as chefLastName,");
        } else {
            sql.append(" null as chefId, null as chefFirstName, null chefLastName,");
        }
        sql.append(" waiter.id as waiterId, waiter.first_name as waiterFirstName, waiter.last_name as waiterLastName,");
        sql.append(" mi.id as menuItemId, mi.name as menuItemName,");
        sql.append(" u.id as multiUnitId,");
        sql.append(" u.weight as weight,");
        sql.append(" u.price as price,");
        sql.append(" unit.id as unitId,");
        sql.append(" unit.name_ru as unitName,");
        sql.append(" tc.id as techCardId,");
        sql.append(" tc.size as techCardName,");
        sql.append(" p.has_mark as hasMark");
        sql.append(" from orders o ");
        sql.append(" inner join order_item item on o.id = item.order_id ");
        if (filter.isMine()) {
            sql.append(" inner join users chef on item.chef_id = chef.id ");
        }
        sql.append(" inner join users waiter on item.creator_id = waiter.id ");
        sql.append(" inner join hall_item t on t.id = o.table_id ");
        sql.append(" inner join hall h on h.id = t.hall_id ");
        sql.append(" inner join multi_unit u on u.id = item.multi_unit_id ");
        sql.append(" left join tech_card tc on tc.id = u.tech_card_id ");
        sql.append(" left join unit unit on unit.id = tc.unit_id ");
        sql.append(" inner join menu_item mi on mi.id = item.menu_item_id ");
        sql.append(" inner join product p on p.id = mi.product_id ");
        sql.append(" where o.branch_id =:branchId");
        sql.append(" and o.deleted=false");
        sql.append(" and item.deleted=false");
        if (filter.getStatus() != null) {
            sql.append(" and o.status in (:status)");
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            sql.append(" and o.status in (:statuses)");
        }
        if (StringUtils.isNotEmpty(filter.getItemStatus())) {
            sql.append(" and item.status=:itemStatus");
        } else if (!CollectionUtils.isEmpty(filter.getItemStatuses())) {
            sql.append(" and item.status in (:itemStatuses)");
        }
        if (filter.getExcludeStatus() != null) {
            sql.append(" and o.status != :excludeStatus");
        } else if (!CollectionUtils.isEmpty(filter.getExcludeStatuses())) {
            sql.append(" and o.status not in (:excludeStatuses)");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and p.department_id=:departmentId");
        }
        if (filter.getChefId() != null) {
            sql.append(" and item.chef_id=:chefId");
        }
        if (filter.getWaiterId() != null) {
            sql.append(" and item.creator_id=:waiterId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.order_date>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.order_date<=:toDate");
        }
        sql.append(" order by item.order_date desc");
        Query query = entityManager.createNativeQuery(sql.toString(), "OrderKanbanBoardDetailMapper");

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getChefId() != null) {
            query.setParameter("chefId", filter.getChefId());
        }
        if (filter.getWaiterId() != null) {
            query.setParameter("waiterId", filter.getWaiterId());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getExcludeStatus() != null) {
            query.setParameter("excludeStatus", filter.getExcludeStatus());
        }else if(!CollectionUtils.isEmpty(filter.getExcludeStatuses())){
            query.setParameter("excludeStatuses", filter.getExcludeStatuses());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", filter.getStatus());
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            query.setParameter("statuses", filter.getStatuses());
        }
        if (StringUtils.isNotEmpty(filter.getItemStatus())) {
            query.setParameter("itemStatus", filter.getItemStatus());
        } else if (!CollectionUtils.isEmpty(filter.getItemStatuses())) {
            query.setParameter("itemStatuses", filter.getItemStatuses());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        List<OrderKanbanBoardDetailMapperDTO> items = query.getResultList();

        Map<Long, OrderKanbanBoardDetailDTO> orderMap = new HashMap<>();
        List<OrderKanbanBoardDetailDTO> result = new ArrayList<>();
        for (OrderKanbanBoardDetailMapperDTO item : items) {
            OrderKanbanBoardDetailDTO orderKanbanBoardDetailDTO = orderMap.get(item.getId());
            boolean isNew = false;
            if (orderKanbanBoardDetailDTO == null) {
                isNew = true;
                orderKanbanBoardDetailDTO = new OrderKanbanBoardDetailDTO();
                orderKanbanBoardDetailDTO.setId(item.getId());
                orderKanbanBoardDetailDTO.setOrderNumber(item.getOrderNumber());
                orderKanbanBoardDetailDTO.setOrderDate(item.getOrderDate());
                orderKanbanBoardDetailDTO.setComment(item.getComment());
                orderKanbanBoardDetailDTO.setStatus(item.getOrderStatus());
                orderKanbanBoardDetailDTO.setTable(new CommonDTO(item.getTableId(), item.getTableName()));
                orderKanbanBoardDetailDTO.setHall(new CommonDTO(item.getHallId(), item.getHallName()));
                orderMap.put(orderKanbanBoardDetailDTO.getId(), orderKanbanBoardDetailDTO);
            }

            OrderItemKanbanBoardDetailDTO orderItemKanbanBoardDetailDTO = new OrderItemKanbanBoardDetailDTO();
            orderItemKanbanBoardDetailDTO.setId(item.getItemId());
            orderItemKanbanBoardDetailDTO.setQty(item.getQty());
            orderItemKanbanBoardDetailDTO.setComment(item.getItemComment());
            orderItemKanbanBoardDetailDTO.setStatus(item.getStatus());
            orderItemKanbanBoardDetailDTO.setOrderDate(item.getItemOrderDate());
            orderItemKanbanBoardDetailDTO.setMenuItem(new CommonDTO(item.getMenuItemId(), item.getMenuItemName()));
            MultiUnitListDTO multiUnitListDTO = new MultiUnitListDTO();
            multiUnitListDTO.setId(item.getMultiUnitId());
            multiUnitListDTO.setWeight(item.getWeight());
            multiUnitListDTO.setPrice(item.getPrice());
            if (item.getUnitId() != null) {
                multiUnitListDTO.setUnit(new CommonDTO(item.getUnitId(), item.getUnitName()));
            }
            if (item.getTechCardId() != null) {
                multiUnitListDTO.setTechCard(new CommonDTO(item.getTechCardId(), item.getTechCardName().concat(" ").concat(item.getUnitName())));
            }
            orderItemKanbanBoardDetailDTO.setMultiUnit(multiUnitListDTO);
            orderItemKanbanBoardDetailDTO.setLastModifiedDate(item.getLastModifiedDate());
            orderItemKanbanBoardDetailDTO.setHasMark(item.isHasMark());
            orderItemKanbanBoardDetailDTO.setMarks(item.getMarks());
            if (item.getChefId() != null) {
                orderItemKanbanBoardDetailDTO.setChef(new CommonDTO(item.getChefId(), item.getChefName()));
            }
            if (item.getWaiterId() != null) {
                orderItemKanbanBoardDetailDTO.setWaiter(new CommonDTO(item.getWaiterId(), item.getWaiterName()));
            }

            orderKanbanBoardDetailDTO.addItem(orderItemKanbanBoardDetailDTO);
            if (isNew) {
                result.add(orderKanbanBoardDetailDTO);
            }
        }
        return result;
    }

    @Override
    public OrderStatisticsDTO getStatistics(OrderFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(" (case when  o.status = 'ACTIVE' then count(distinct o.id) else 0 end) as activeCount, ");
        sql.append(" (case when  o.status = 'READY' then count(distinct o.id) else 0 end) as readyCount, ");
        sql.append(" (case when  o.status = 'CANCELLED' then count(distinct o.id) else 0 end) as cancelledCount ");
        sql.append(getBaseSql(filter));
        sql.append(" group by o.status ");

        TypedQuery<Object[]> query = entityManager.createQuery(sql.toString(), Object[].class);
        query.setParameter("branchId", filter.getBranchId());

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", OrderStatus.valueOf(filter.getStatus()));
        }
        if (filter.getPaymentType() != null) {
            query.setParameter("paymentType", filter.getPaymentType());
        }
        if (filter.getItemsCount() != null) {
            query.setParameter("itemsCount", filter.getItemsCount());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getClientId() != null) {
            query.setParameter("clientId", filter.getClientId());
        }
        if (filter.getTableId() != null) {
            query.setParameter("tableId", filter.getTableId());
        }
        if (filter.getWaiterId() != null) {
            query.setParameter("waiterId", filter.getWaiterId());
        }
        if (filter.getChefId() != null) {
            query.setParameter("chefId", filter.getChefId());
        }
        if (filter.getShiftId() != null) {
            query.setParameter("shiftId", filter.getShiftId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and o.status=:status");
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            sql.append(" and o.status in (:statuses)");
        }
        if (filter.getFromTotalPrice() != null) {
            query.setParameter("fromTotalPrice", filter.getFromTotalPrice());
        }
        if (filter.getToTotalPrice() != null) {
            query.setParameter("toTotalPrice", filter.getToTotalPrice());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }

        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
        }

        List<Object[]> result = query.getResultList();

        Long activeCount = 0L;

        Long readyCount = 0L;

        Long cancelledCount = 0L;

        for (Object[] row : result) {
            activeCount += (Long) row[0];
            readyCount += (Long) row[1];
            cancelledCount += (Long) row[2];
        }
        return new OrderStatisticsDTO(activeCount, readyCount, cancelledCount);
    }

    private StringBuilder getBaseSql(OrderFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from Order o");

        sql.append(" inner join OrderItem ot on ot.orderId = o.id ");
        if (filter.getPaymentType() != null) {
            sql.append(" inner join OrderPayment op on o.id = op.orderId and op.paymentType =:paymentType");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" inner join MenuItem mi on ot.menuItemId = mi.id ");
            sql.append(" inner join Product p on mi.productId = p.id ");
        }
        sql.append(" where o.deleted = false");
        sql.append(" and o.branchId=:branchId");

        if (filter.getItemsCount() != null) {
            sql.append(" and o.itemsCount =:itemsCount ");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and p.departmentId =:departmentId ");
        }
        if (filter.getClientId() != null) {
            sql.append(" and o.clientId=:clientId ");
        }
        if (filter.getTableId() != null) {
            sql.append(" and o.tableId=:tableId ");
        }
        if (filter.getWaiterId() != null) {
            sql.append(" and o.waiterId=:waiterId ");
        }
        if (filter.getChefId() != null) {
            sql.append(" and ot.chefId=:chefId ");
        }
        if (filter.getShiftId() != null) {
            sql.append(" and o.shiftId=:shiftId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and o.status=:status");
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            sql.append(" and o.status in (:statuses)");
        }
        if (filter.getFromTotalPrice() != null) {
            sql.append(" and o.totalPrice>=:fromTotalPrice ");
        }
        if (filter.getToTotalPrice() != null) {
            sql.append(" and o.totalPrice<=:toTotalPrice ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and o.orderDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and o.orderDate<=:toDate");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(o.orderNumber)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }
}
