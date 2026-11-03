package uz.uzkassa.silen.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.dashboard.DashboardFilter;
import uz.uzkassa.silen.dto.dashboard.OrganizationStatsDTO;
import uz.uzkassa.silen.dto.dashboard.StatsV2DTO;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ProductType;
import uz.uzkassa.silen.enumeration.WarehouseOperation;
import uz.uzkassa.silen.service.DashboardService;
import uz.uzkassa.silen.service.WarehouseService;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard/tobacco")
@RequiredArgsConstructor
public class DashboardTobaccoResource {
    private final DashboardService dashboardService;
    private final WarehouseService warehouseService;

    @GetMapping("/production/stats")
    public ResponseEntity<List<StatsV2DTO>> getProductionStats(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationTypes(EnumSet.of(OrganizationType.Tobacco));
        filter.setProductTypes(ProductType.byOrganizationTypes(filter.getOrganizationTypes()));
        return ResponseEntity.ok(warehouseService.getWarehouseStats(filter));
    }

    @GetMapping("/shipping/stats")
    public ResponseEntity<List<StatsV2DTO>> getShippingStats(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationTypes(EnumSet.of(OrganizationType.TobaccoDistribution));
        filter.setProductTypes(ProductType.byOrganizationTypes(filter.getOrganizationTypes()));
        return ResponseEntity.ok(warehouseService.getWarehouseStats(filter));
    }

    @GetMapping("/consumer/stats")
    public ResponseEntity<List<StatsV2DTO>> getConsumerStats(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationTypes(EnumSet.of(OrganizationType.TobaccoRetail));
        filter.setProductTypes(ProductType.byOrganizationTypes(filter.getOrganizationTypes()));
        filter.setConsumer(true);
        return ResponseEntity.ok(warehouseService.getWarehouseStats(filter));
    }

    @GetMapping("/risk/stats")
    public ResponseEntity<List<StatsV2DTO>> getRiskStats(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationType(OrganizationType.Tobacco);
        return ResponseEntity.ok(dashboardService.getStatsTemp(filter));
    }

    @GetMapping("/production")
    public ResponseEntity<List<OrganizationStatsDTO>> getProduction(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationTypes(EnumSet.of(OrganizationType.Tobacco));
        filter.setProductTypes(ProductType.byOrganizationTypes(filter.getOrganizationTypes()));
        filter.setWarehouseOperations(EnumSet.of(WarehouseOperation.Production, WarehouseOperation.ShipmentBuyer, WarehouseOperation.Income));
        return ResponseEntity.ok(warehouseService.getWarehouseStatsByFilter(filter));
    }

    @GetMapping("/shipping")
    public ResponseEntity<List<OrganizationStatsDTO>> getShipping(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationTypes(EnumSet.of(OrganizationType.TobaccoDistribution));
        filter.setProductTypes(ProductType.byOrganizationTypes(filter.getOrganizationTypes()));
        filter.setWarehouseOperations(EnumSet.of(WarehouseOperation.Production, WarehouseOperation.ShipmentBuyer, WarehouseOperation.Income));
        return ResponseEntity.ok(warehouseService.getWarehouseStatsByFilter(filter));
    }

    @GetMapping("/consumer")
    public ResponseEntity<List<OrganizationStatsDTO>> getConsumer(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationTypes(EnumSet.of(OrganizationType.TobaccoRetail));
        filter.setWarehouseOperations(EnumSet.of(WarehouseOperation.Production, WarehouseOperation.ShipmentBuyer, WarehouseOperation.Income));
        filter.setConsumer(true);
        return ResponseEntity.ok(warehouseService.getWarehouseStatsByFilter(filter));
    }

    @GetMapping("/risk")
    public ResponseEntity<List<OrganizationStatsDTO>> getRisk(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationType(OrganizationType.Tobacco);
        return ResponseEntity.ok(dashboardService.getDataTemp(filter));
    }

    @GetMapping("/by-product-type")
    public ResponseEntity<List<StatsV2DTO>> getByProductType(@ModelAttribute DashboardFilter filter) {
        filter.setOrganizationTypes(EnumSet.of(OrganizationType.Tobacco));
        return ResponseEntity.ok(warehouseService.getWarehouseStats(filter));
    }
}
