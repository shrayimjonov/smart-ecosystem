package uz.uzkassa.silen.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.dashboard.DashboardFilter;
import uz.uzkassa.silen.dto.dashboard.OrganizationStatsDTO;
import uz.uzkassa.silen.dto.dashboard.RiskStatsDTO;
import uz.uzkassa.silen.dto.dashboard.StatsV2DTO;
import uz.uzkassa.silen.dto.filter.DailyReportFilter;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.service.DashboardService;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard/alcohol")
@RequiredArgsConstructor
public class DashboardAlcoholResource {
    private final DashboardService dashboardService;

    @GetMapping("/production/stats")
    public ResponseEntity<List<StatsV2DTO>> getProductionStats(@ModelAttribute DailyReportFilter filter) {
        filter.setOrganizationType(OrganizationType.Alcohol);
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/shipping/stats")
    public ResponseEntity<List<StatsV2DTO>> getShippingStats(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationType(OrganizationType.Delivery);
        return ResponseEntity.ok(dashboardService.getStatsTemp(filter));
    }

    @GetMapping("/consumer/stats")
    public ResponseEntity<List<StatsV2DTO>> getConsumerStats(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationTypes(EnumSet.of(OrganizationType.Vine, OrganizationType.Other));
        return ResponseEntity.ok(dashboardService.getStatsTemp(filter));
    }

    @GetMapping("/risk/stats")
    public ResponseEntity<List<StatsV2DTO>> getRiskStats(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationType(OrganizationType.Alcohol);
        return ResponseEntity.ok(dashboardService.getStatsTemp(filter));
    }

    @GetMapping("/production")
    public ResponseEntity<List<OrganizationStatsDTO>> getProduction(@ModelAttribute DailyReportFilter filter) {
        filter.setOrganizationType(OrganizationType.Alcohol);
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/shipping")
    public ResponseEntity<List<OrganizationStatsDTO>> getShipping(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationType(OrganizationType.Delivery);
        return ResponseEntity.ok(dashboardService.getDataTemp(filter));
    }

    @GetMapping("/consumer")
    public ResponseEntity<List<OrganizationStatsDTO>> getConsumer(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationTypes(EnumSet.of(OrganizationType.Vine, OrganizationType.Other));
        return ResponseEntity.ok(dashboardService.getDataTemp(filter));
    }

    @GetMapping("/risk")
    public ResponseEntity<List<RiskStatsDTO>> getRisk(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationType(OrganizationType.Alcohol);
        return ResponseEntity.ok(dashboardService.getRiskDataTemp(filter));
    }

    @GetMapping("/by-product-type")
    public ResponseEntity<List<StatsV2DTO>> getByProductType(@ModelAttribute DailyReportFilter filter) {
        filter.setOrganizationType(OrganizationType.Alcohol);
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/by-product-type-temp")
    public ResponseEntity<List<StatsV2DTO>> getByProductTypeTemp(@ModelAttribute DashboardFilter filter) {
        return ResponseEntity.ok(dashboardService.getProductsTemp(filter));
    }
}
