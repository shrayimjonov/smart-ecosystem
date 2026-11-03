package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRoute;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteCustomerStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteReportDTO;
import uz.uzkassa.smartpossupply.dto.planroute.PlanRouteReportTotalQtyDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.filters.PlanRouteFilter;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.01.2023 15:51
 */
@Repository
public interface PlanRouteRepositoryCustom {

    ResultList<PlanRoute> getResultList(PlanRouteFilter filter);

    ResultList<PlanRoute> getResultListForExpeditor(BaseFilter filter);

    PlanRouteCustomerStatisticsDTO getCustomerStatistics(PlanRouteFilter filter);

    ResultList<Company> getCustomers(CompanyFilter filter);

    PlanRouteReportTotalQtyDTO getReportByAgentTotalQty(PlanRouteFilter filter);

    Page<PlanRouteReportDTO> getReportByAgent(PlanRouteFilter filter);

    List<PlanRoute> findLastPlanRouteByCustomerBranch(Long customerBranchId, Long companyId);
}
