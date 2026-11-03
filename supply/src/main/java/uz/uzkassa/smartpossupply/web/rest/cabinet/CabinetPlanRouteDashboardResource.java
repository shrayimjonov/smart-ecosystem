package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.planroute.dashboard.*;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.filters.CustomerBranchProductHistoryFilter;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.service.PlanRouteDashboardService;

import java.util.List;


@RestController
@RequestMapping(ApiConstants.cabinetPlanRouteDashboardRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetPlanRouteDashboardResource {

    PlanRouteDashboardService planRouteDashboardService;

    @GetMapping("/{planRouteCustomerBranchId}/plan-route-details")
    public ResponseEntity<PlanRouteDashboardPlanRouteDetailDTO> getPlanRouteDetails(@PathVariable Long planRouteCustomerBranchId) {
        return ResponseEntity.ok(planRouteDashboardService.getPlanRouteDetails(planRouteCustomerBranchId));
    }

    @GetMapping("/{planRouteCustomerBranchId}/customer-details")
    public ResponseEntity<PlanRouteDashboardCustomerDetailDTO> getCustomerDetails(@PathVariable Long planRouteCustomerBranchId) {
        return ResponseEntity.ok(planRouteDashboardService.getCustomerDetails(planRouteCustomerBranchId));
    }

    @GetMapping("/{planRouteCustomerBranchId}/orders")
    public ResponseEntity<Page<PlanRouteDashboardOrderListDTO>> getOrders(@PathVariable Long planRouteCustomerBranchId, @ParameterObject OrderFilter filter) {
        return ResponseEntity.ok(planRouteDashboardService.getOrders(planRouteCustomerBranchId, filter));
    }

    @GetMapping("/{planRouteCustomerBranchId}/photo-reports")
    public ResponseEntity<List<PlanRouteDashboardPhotoReportListDTO>> getPhotoReport(@PathVariable Long planRouteCustomerBranchId) {
        return ResponseEntity.ok(planRouteDashboardService.getPhotoReport(planRouteCustomerBranchId));
    }

    @GetMapping("/{planRouteCustomerBranchId}/customer-branch-products")
    public ResponseEntity<Page<PlanRouteDashboardCustomerBranchProductDTO>> getCustomerBranchProductHistory(@PathVariable Long planRouteCustomerBranchId, @ParameterObject CustomerBranchProductHistoryFilter filter) {
        return ResponseEntity.ok(planRouteDashboardService.getCustomerBranchProductHistory(planRouteCustomerBranchId,filter));
    }

    @GetMapping("/{customerBranchId}/plan-routes")
    public ResponseEntity<Page<PlanRouteListDashboardDTO>> getPlanRoutes(@PathVariable Long customerBranchId, @ParameterObject CustomerBranchFilter filter) {
        return ResponseEntity.ok(planRouteDashboardService.getPlanRoutes(customerBranchId,filter));
    }
}
