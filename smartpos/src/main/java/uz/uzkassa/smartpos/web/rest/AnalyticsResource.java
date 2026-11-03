package uz.uzkassa.smartpos.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.CategoryDTO;
import uz.uzkassa.smartpos.dto.analytics.*;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesDynamicsFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.service.AnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@Slf4j
public class AnalyticsResource {
    private final AnalyticsService analyticsService;

    public AnalyticsResource(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping(value = "/sales-stats")
    public OverallSalesStatsDTO getSalesStats(@ParameterObject SalesStatsFilter filter) {
        return analyticsService.getSalesStats(filter);
    }

    @GetMapping(value = "/sales-dynamics")
    public List<OverallSalesStatsDTO> getSalesDynamics(@ParameterObject SalesDynamicsFilter filter) {
        return analyticsService.getSalesDynamic(filter);
    }

    @GetMapping(value = "/sales-stats/products")
    public Page<SalesStatsDTO> getProductSalesStats(@ParameterObject SalesStatsFilter filter) {
        return analyticsService.getProductSalesStats(filter);
    }

    @GetMapping(value = "/sales-stats/products/header")
    public ResponseEntity<SalesStatsProductsHeader> getSalesStatsProductHeader(@ParameterObject SalesStatsFilter filter) {
        return ResponseEntity.ok(analyticsService.getProductSalesStatsHeader(filter));
    }

    @GetMapping(value = "/sales-stats/users")
    public ResponseEntity<Page<UserSalesStatsDTO>> getUserSalesStats(@ParameterObject SalesStatsFilter filter) {
        return ResponseEntity.ok().body(analyticsService.getUserSalesStats(filter));
    }

    @GetMapping(value = "/sales-dynamics/product")
    public Page<SalesStatsDTO> getProductSalesDynamic(@ParameterObject SalesDynamicsFilter filter) {
        return analyticsService.getProductSalesDynamic(filter);
    }

    @GetMapping("/sales-stats/employee/stat")
    public EmployeeSalesStatDTO getEmployeeSalesStat(@ParameterObject SalesStatsFilter filter) {
        return analyticsService.getEmployeeSalesStat(filter);
    }

    @GetMapping(value = "/sales-stats/categories")
    public List<CategoryDTO> getCategorySalesStats(@ParameterObject SalesStatsFilter filter) {
        return analyticsService.getCategorySalesStats(filter);
    }

    @GetMapping(value = "/sales-stats/categories/stat")
    public CategorySalesStats getSalesStatCategoryStat(@ParameterObject SalesStatsFilter filter) {
        return analyticsService.getSalesStatCategoryStat(filter);
    }
}
