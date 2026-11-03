package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.dashboard.response.DashboardCustomerBranchDeviceDto;
import uz.uzkassa.cts.dto.dashboard.response.DashboardStatistics;
import uz.uzkassa.cts.dto.dashboard.response.DashboardStatsByDistrictResponseDto;
import uz.uzkassa.cts.dto.dashboard.response.DashboardStatsByRegionResponseDto;
import uz.uzkassa.cts.filter.DashboardFilter;
import uz.uzkassa.cts.service.DashboardService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard")
@Slf4j
public class DashboardResource {

    private final DashboardService service;

    public DashboardResource(DashboardService service) {
        this.service = service;
    }

    @Operation(summary = "KKM statuses")
    @GetMapping("/stats")
    public ResponseEntity<List<DashboardStatistics>> getKKMStats() {
        log.debug("REST request to get KKM stats");
        return ResponseEntity.ok(service.getStatusesByTariff());
    }

    @Operation(summary = "KKM statuses")
    @GetMapping("regions/stats")
    public ResponseEntity<List<DashboardStatsByRegionResponseDto>> getKKMStatsByRegions() {
        log.debug("REST request to get KKM stats by region");
        return ResponseEntity.ok(service.getStatsByRegions());
    }

    @Operation(summary = "KKM statuses")
    @GetMapping("regions/{id}/stats")
    public ResponseEntity<List<DashboardStatsByDistrictResponseDto>> getKKMStatsByRegions(@PathVariable Long id) {
        log.debug("REST request to get KKM stats by region");
        return ResponseEntity.ok(service.getStatsByRegion(id));
    }

    @Operation(summary = "KKM statuses")
    @PostMapping("/getDevices")
    public ResponseEntity<Page<DashboardCustomerBranchDeviceDto>> getKKMStatsByRegionsTariffAndStatus(@Valid @RequestBody DashboardFilter filter) {
        log.debug("REST request to get KKM stats by filter: {}", filter);
        return ResponseEntity.ok(service.getStatsByRegionTariffAndStatus(filter));
    }
}
