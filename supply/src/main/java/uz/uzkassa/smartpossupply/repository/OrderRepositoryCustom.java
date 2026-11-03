package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.order.Order;
import uz.uzkassa.smartpossupply.domain.warehouse.Warehouse;
import uz.uzkassa.smartpossupply.dto.agent.performancereport.AgentPerformanceReportOrderCategoryDTO;
import uz.uzkassa.smartpossupply.dto.agent.performancereport.AgentPerformanceReportOrderDTO;
import uz.uzkassa.smartpossupply.dto.agent.performancereport.AgentPerformanceReportOrderPaymentDTO;
import uz.uzkassa.smartpossupply.dto.agent.performancereport.AgentPerformanceReportOrderProductDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.order.OrderExpeditorReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderExpeditorTotalQtyReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderReportDTO;
import uz.uzkassa.smartpossupply.dto.order.OrderStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.order.SalesAndCustomerReportDTO;
import uz.uzkassa.smartpossupply.dto.order.SalesAndCustomerSummaryDTO;
import uz.uzkassa.smartpossupply.dto.order.mobile.MobileOrderStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.order.terminal.TerminalOrderStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.user.supervisor.AgentStatisticByCategoryRowDTO;
import uz.uzkassa.smartpossupply.dto.user.supervisor.AgentStatisticByProductRowDTO;
import uz.uzkassa.smartpossupply.filters.AgentPerformanceReportFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.filters.OrderReportFilter;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;
import uz.uzkassa.smartpossupply.filters.SupervisorOrderFilter;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface OrderRepositoryCustom {

    List<Order> lookUp(OrderFilter filter);

    ResultList<Order> getResultList(OrderFilter filter);

    ResultList<OrderReportDTO> getResultListForReport(OrderReportFilter filter);

    ResultList<Order> getResultListForExpeditor(OrderFilter filter);

    List<Order> getListForTransfer(OrderFilter filter);

    ResultList<Order> getResultListForTerminal(OrderFilter filter);

    OrderStatisticsDTO getStatistics(OrderFilter filter);

    MobileOrderStatisticsDTO getOrderStatisticsForMobile(BaseFilter filter);

    List<AgentPerformanceReportOrderPaymentDTO> getAgentPerformanceReportPayments(AgentPerformanceReportFilter filter);

    BigDecimal getAgentPerformanceReportOrderTotalPrice(AgentPerformanceReportFilter filter);

    Page<OrderProductQtyDetailListDTO> getQtyDetails(OrderFilter filter);

    SalesAndCustomerSummaryDTO getSalesAndCustomerSummary(OrderFilter filter);

    Page<SalesAndCustomerReportDTO> getSalesAndCustomerReport(OrderFilter filter);

    List<AgentStatisticByCategoryRowDTO> getAgentStatisticsByCategory(Collection<Long> agentIds, SimpleFilter filter);

    List<AgentStatisticByProductRowDTO> getAgentStatisticsByProduct(Collection<Long> agentIds, SimpleFilter filter);

    List<AgentPerformanceReportOrderDTO> getAgentPerformanceReport(AgentPerformanceReportFilter filter);

    List<AgentPerformanceReportOrderProductDTO> getAgentPerformanceReportOrderProducts(AgentPerformanceReportFilter filter);

    List<AgentPerformanceReportOrderCategoryDTO> getAgentPerformanceReportOrderCategories(AgentPerformanceReportFilter filter);

    ResultList<Order> getSupervisorOrder(SupervisorOrderFilter filter);

    ResultList<OrderExpeditorReportDTO> getOrderExpeditorReport(OrderFilter filter);

    OrderExpeditorTotalQtyReportDTO getOrderExpeditorTotalQtyReport(OrderFilter filter);

    TerminalOrderStatisticsDTO getOrderStatisticsForTerminal(OrderFilter filter);
}
