package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranch;
import uz.uzkassa.smartpossupply.dto.agent.performancereport.AgentPerformanceReportVisitDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.kpi.KPICalculateTotalDTO;
import uz.uzkassa.smartpossupply.dto.kpi.KPIPlanRouteCalculateDTO;
import uz.uzkassa.smartpossupply.dto.user.supervisor.SupervisorPlanRouteStatisticDTO;
import uz.uzkassa.smartpossupply.filters.AgentPerformanceReportFilter;
import uz.uzkassa.smartpossupply.filters.KPIFilter;
import uz.uzkassa.smartpossupply.filters.PlanRouteCustomerBranchFilter;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlanRouteCustomerBranchRepositoryCustom {

    List<AgentPerformanceReportVisitDTO> getAgentPerformanceReport(AgentPerformanceReportFilter filter);

    ResultList<KPIPlanRouteCalculateDTO> calculatePlanRouteCount(KPIFilter filter);

    KPICalculateTotalDTO calculatePlanRouteTotal(KPIFilter filter);

    List<SupervisorPlanRouteStatisticDTO> getPlanRouteStatistics(Long companyId, Long templateId, LocalDate from, LocalDate to);

    ResultList<PlanRouteCustomerBranch> getPlanRoutesCustomerBranches(PlanRouteCustomerBranchFilter filter);
}
