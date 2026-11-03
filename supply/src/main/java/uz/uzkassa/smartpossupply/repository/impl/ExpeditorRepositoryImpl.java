package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.collections.CollectionUtils;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorListDTO;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.OrderStatus;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.ExpeditorFilter;
import uz.uzkassa.smartpossupply.repository.ExpeditorRepositoryCustom;

import java.util.List;
import java.util.stream.Collectors;


public class ExpeditorRepositoryImpl extends BaseRepositoryImpl implements ExpeditorRepositoryCustom {

    @Override
    public ResultList<ExpeditorListDTO> getResultList(ExpeditorFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        ResultList<ExpeditorListDTO> resultList = new ResultList<>();
        StringBuilder with = new StringBuilder();
        with.append("""
                with str as (select u.id as expeditor_id, coalesce(sum(st.total_qty), 0) as total_qty
                             from users u
                                      left join stock_transfer st on st.expeditor_id = u.id
                             where u.deleted is false
                               and st.deleted is false
                               and st.status = '""");
        with.append(StockTransferStatus.APPROVED.getCode()).append("'");
        with.append("""
                             group by u.id),
                     sout as (select u.id as expeditor_id, coalesce(sum(s.total_qty), 0) as total_qty
                              from users u
                                       left join orders o on u.id = o.expeditor_id
                                       left join stock s on s.order_id = o.id
                              where u.deleted is false
                                and s.deleted is false
                                and s.transfer_type = '""");
        with.append(TransferType.OUT.name()).append("'");
        with.append("""                      
                group by u.id),
                pre as (select u.id as expeditor_id, jsonb_agg(jsonb_build_object('id', pr.id, 'name', pr.name)) as plan_routes
                        from users u
                                 left join plan_route_expeditor pre on u.id = pre.expeditor_id
                                 left join plan_route pr on pre.plan_route_id = pr.id
                        group by u.id)
                """);
        StringBuilder sql = new StringBuilder();
        sql.append("""
                select u.id                                                                    as id,
                       u.first_name                                                            as firstName,
                       u.last_name                                                             as lastName,
                       u.patronymic                                                            as patronymic,
                       pr.plan_routes                                                          as planRoutes,
                       coalesce(count(distinct o.id), 0)                                       as orderCount,
                       coalesce(count(distinct o.id) filter (where o.status = 'COMPLETED'), 0) as completedOrderCount,
                       coalesce(st.total_qty, 0)                                               as incomingOrderProductCount,
                       coalesce(so.total_qty, 0)                                               as outgoingOrderProductCount,
                       coalesce(st.total_qty, 0) - coalesce(so.total_qty, 0)                   as remainderOrderProductCount
                """);
        sql.append(getBaseSql(filter));
        sql.append(" group by u.id, pr.plan_routes, st.total_qty, so.total_qty");
        sql.append(" order by u.id");
        Query query = entityManager.createNativeQuery(with.toString() + sql, "ExpeditorListDTOMapper")
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        StringBuilder countSql = new StringBuilder();
        countSql.append("select count(distinct u.id) ").append(getBaseSql(filter));

        Query countQuery = entityManager.createNativeQuery(with.toString() + countSql);

        long count = (long) (countQuery.getSingleResult());
        if (count > 0) {
            List<ExpeditorListDTO> result = query.getResultList();
            resultList.setList(result);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseSql(ExpeditorFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from users u");
        sql.append(" left join pre pr on u.id = pr.expeditor_id");
        sql.append(" left join orders o on u.id = o.expeditor_id and o.deleted is false");
        sql.append(" left join str st on u.id = st.expeditor_id");
        sql.append(" left join sout so on u.id = so.expeditor_id");
        sql.append(" where u.deleted is false");
        sql.append(" and u.company_id = ").append(filter.getCompanyId());
        sql.append(" and u.role = '").append(Role.ROLE_EXPEDITOR.getCode()).append("'");
        if (CollectionUtils.isNotEmpty(filter.getExpeditorIds())) {
            sql.append(" and u.id in (").append(filter.getExpeditorIds().stream().map(Object::toString).collect(Collectors.joining(","))).append(")");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and u.branch_id = ").append(filter.getBranchId());
        } else if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and u.branch_id in (").append(filter.getBranchIds().stream().map(Object::toString).collect(Collectors.joining(","))).append(")");
        }
        if (CollectionUtils.isNotEmpty(filter.getPlanRouteIds())) {
            sql.append("and exists (select 1 from jsonb_array_elements(pr.plan_routes) as prs where (prs ->> 'id')::bigint in (").append(filter.getPlanRouteIds().stream().map(Object::toString).collect(Collectors.joining(","))).append("))");
        }
        return sql;
    }

    @Override
    public ExpeditorStatisticsDTO getStatistics(ExpeditorFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
        StringBuilder sql = new StringBuilder();
        sql.append("""
                select count(expeditorId)       as expeditorTotal,
                       sum(planRouteCount)      as planRouteTotal,
                       sum(orderCount)          as orderTotal,
                       sum(completedOrderCount) as completedOrderTotal
                from (select u.id                                                         as expeditorId,
                             count(distinct pre.id)                                       as planRouteCount,
                             count(distinct o.id)                                         as orderCount,
                             count(distinct o.id) filter ( where o.status = '""");
        sql.append(OrderStatus.COMPLETED.getCode()).append("' ) as completedOrderCount ");
        sql.append("""
                      from users u
                               left join orders o on u.id = o.expeditor_id
                               left join plan_route_expeditor pre on u.id = pre.expeditor_id
                               left join plan_route pr on pre.plan_route_id = pr.id
                      where u.deleted is false
                        and u.role = '""");
        sql.append(Role.ROLE_EXPEDITOR.getCode()).append("'");
        sql.append(" and u.company_id = ").append(filter.getCompanyId());
        if (CollectionUtils.isNotEmpty(filter.getExpeditorIds())) {
            sql.append(" and u.id in (").append(filter.getExpeditorIds().stream().map(Object::toString).collect(Collectors.joining(","))).append(")");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and u.branch_id = ").append(filter.getBranchId());
        } else if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and u.branch_id in (").append(filter.getBranchIds().stream().map(Object::toString).collect(Collectors.joining(","))).append(")");
        }
        if (CollectionUtils.isNotEmpty(filter.getPlanRouteIds())) {
            sql.append("and pr.id in (").append(filter.getPlanRouteIds().stream().map(Object::toString).collect(Collectors.joining(","))).append(")");
        }
        sql.append(" group by u.id) as sub");

        Query statisticsQuery = entityManager.createNativeQuery(sql.toString(), "ExpeditorStatisticsDTOMapper");
        return (ExpeditorStatisticsDTO) statisticsQuery.getSingleResult();
    }
}