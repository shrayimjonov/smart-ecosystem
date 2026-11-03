package uz.uzkassa.horeca.repository;

import java.util.List;
import uz.uzkassa.horeca.domain.order.Order;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardDetailDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderKanbanBoardListDTO;
import uz.uzkassa.horeca.dto.order.kanban.OrderStatisticsDTO;
import uz.uzkassa.horeca.filters.OrderFilter;

public interface OrderRepositoryCustom {
    ResultList<Order> getResultList(OrderFilter filter);

    OrderStatisticsDTO getStatistics(OrderFilter filter);

    List<OrderKanbanBoardListDTO> getKanbanBoardList(OrderFilter filter);

    List<OrderKanbanBoardDetailDTO> getKanbanBoardDetails(OrderFilter filter);
}
