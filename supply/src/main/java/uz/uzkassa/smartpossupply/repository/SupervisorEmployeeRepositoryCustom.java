package uz.uzkassa.smartpossupply.repository;

import uz.uzkassa.smartpossupply.domain.stock.Stock;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.user.supervisor.*;
import uz.uzkassa.smartpossupply.filters.SupervisorExpeditorFilter;
import uz.uzkassa.smartpossupply.filters.SupervisorFilter;
import uz.uzkassa.smartpossupply.filters.SupervisorOrderFilter;

import java.util.Optional;

public interface SupervisorEmployeeRepositoryCustom {
    ResultList<SupervisorDTO> getAll(SupervisorFilter filter);

    Optional<SupervisorDTO> bySupervisorId(Long supervisorId, Long companyId);

    ResultList<EmployeeAssignmentDTO> getEmployees(SupervisorFilter filter);

    ResultList<AgentInfoDTO> getAssignedEmployees(SupervisorFilter filter);

    ResultList<AgentPlanRouteInfoDTO> getAgentsWithPlanRoute(SupervisorFilter filter);

    CommonStatisticsDTO getPlanRoutesStatistics(SupervisorFilter filter);

    Long getCommonStatistics(SupervisorFilter filter);

    Long getExpeditorOnPlanRoute(SupervisorFilter filter);

    ExpeditorCompletedStockOutDTO getExpeditorStockStatistics(SupervisorFilter filter);

    ExpeditorCancelledStockOutDTO getExpeditorStatistics(SupervisorExpeditorFilter filter);

    ExpeditorOrderDTO getExpeditorOrderStatistics(SupervisorOrderFilter filter);

    ResultList<Stock> getExpeditorStocks(SupervisorExpeditorFilter filter);
}
