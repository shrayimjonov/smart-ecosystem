package uz.uzkassa.silen.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.Result;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.TopSelectItem;
import uz.uzkassa.silen.dto.dashboard.DashboardStatsDTO;
import uz.uzkassa.silen.dto.dashboard.OrganizationsDTO;
import uz.uzkassa.silen.dto.dashboard.StatsDTO;
import uz.uzkassa.silen.dto.filter.DailyReportFilter;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.dto.filter.TopFilter;
import uz.uzkassa.silen.enumeration.DailyOperationTypes;
import uz.uzkassa.silen.enumeration.FilterMode;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.service.DashboardService;
import uz.uzkassa.silen.service.OrganizationService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardResource {
    private final DashboardService dashboardService;
    private final OrganizationService organizationService;

    @GetMapping("/stats")
    public ResponseEntity<Result<List<StatsDTO>>> getStats(@ModelAttribute DailyReportFilter filter) {
        return ResponseEntity.ok(Result.success(new ArrayList<>()));
    }

    @GetMapping("/top/errors")
    public ResponseEntity<List<TopSelectItem>> topErrors(@ModelAttribute Filter filter) {
        return ResponseEntity.ok(dashboardService.topErrors(filter));
    }

    @GetMapping("/top/orders")
    public ResponseEntity<List<TopSelectItem>> topOrders(@ModelAttribute Filter filter) {
        return ResponseEntity.ok(dashboardService.topOrders(filter));
    }

    @GetMapping("/top/dropout")
    public ResponseEntity<List<TopSelectItem>> topDropout(@ModelAttribute Filter filter) {
        return ResponseEntity.ok(dashboardService.topDropout(filter));
    }

    @GetMapping("/top/storein")
    public ResponseEntity<List<TopSelectItem>> topStoreIn(@ModelAttribute Filter filter) {
        return ResponseEntity.ok(dashboardService.topStoreIn(filter));
    }
    @GetMapping("/top/shipment")
    public ResponseEntity<List<DashboardStatsDTO>> topShipment(@ModelAttribute TopFilter filter) {
        filter.setType(DailyOperationTypes.Shipment);
        return ResponseEntity.ok(dashboardService.findAllByFilter(filter));
    }

    @GetMapping("/top/aggregation")
    public ResponseEntity<List<DashboardStatsDTO>> topAggregation(@ModelAttribute TopFilter filter) {
        filter.setType(DailyOperationTypes.Production);
        return ResponseEntity.ok(dashboardService.findAllByFilter(filter));
    }

    @GetMapping("/filter-modes")
    public ResponseEntity<List<SelectItem>> getFilterModes() {
        return ResponseEntity.ok(FilterMode.getAll());
    }

    @GetMapping("/organization-types")
    public ResponseEntity<Result<List<SelectItem>>> getTypes() {
        return ResponseEntity.ok(Result.success(OrganizationType.getForFilter()));
    }

    @GetMapping("/organizations")
    public ResponseEntity<Result<List<OrganizationsDTO>>> getOrganizations() {
        return ResponseEntity.ok(Result.success(dashboardService.getOrganizations()));
    }
}
