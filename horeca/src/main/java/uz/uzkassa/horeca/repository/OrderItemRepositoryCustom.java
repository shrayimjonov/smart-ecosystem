package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.domain.order.OrderItem;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.order.kanban.OrderItemStatisticsDTO;
import uz.uzkassa.horeca.dto.order.mobile.OrderItemChefStatsMapperDTO;
import uz.uzkassa.horeca.filters.ChefStatsFilter;
import uz.uzkassa.horeca.filters.OrderItemFilter;

import java.util.List;

public interface OrderItemRepositoryCustom {

    ResultList<OrderItem> getResultList(OrderItemFilter filter);

    OrderItemStatisticsDTO getStatistics(OrderItemFilter filter);

    OrderItemChefStatsMapperDTO getMobileChefStatistics(ChefStatsFilter filter);

    List<Order> findFirstOrderByCodeAndStatus(Long companyId, String status, String itemStatus, String code);
}
