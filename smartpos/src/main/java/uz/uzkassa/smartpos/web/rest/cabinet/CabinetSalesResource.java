package uz.uzkassa.smartpos.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.SalesDynamicsDto;
import uz.uzkassa.smartpos.dto.analytics.ReceiptStatDto;
import uz.uzkassa.smartpos.dto.sales.*;
import uz.uzkassa.smartpos.filter.SalesFilter;
import uz.uzkassa.smartpos.service.SalesService;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cabinet/sales")
public class CabinetSalesResource {
    private final SalesService salesService;

    @GetMapping
    public Page<SalesCabinetListDto> findAllByFilter(@ParameterObject SalesFilter filter) {
        return salesService.findAllByFilter(filter, false);
    }

    @GetMapping("/{id}")
    public SalesDto findById(@PathVariable Long id) {
        return salesService.findById(id);
    }

    @GetMapping("/branches/top")
    public List<SalesTopBranchDto> topBranches(@ParameterObject SalesFilter filter) {
        return salesService.topBranches(filter);
    }

    @GetMapping("/branches/top/excel")
    public ResponseEntity<Resource> topBranchesExcel(@ParameterObject SalesFilter filter) {
        return ServerUtils.getResponseEntity(salesService.topBranchesExcel(filter), "топ-филиалов");
    }

    @GetMapping("/products/top")
    public List<SalesTopProductDto> topProducts(@ParameterObject SalesFilter filter) {
        return salesService.topProducts(filter);
    }

    @GetMapping("/products/top/excel")
    public ResponseEntity<Resource> topProductsExcel(@ParameterObject SalesFilter filter) {
        return ServerUtils.getResponseEntity(salesService.topProductsExcel(filter), "топ-продуктов");
    }

    @GetMapping("/dynamics")
    public List<SalesDynamicsDto> salesDynamics(@ParameterObject SalesFilter filter) {
        return salesService.salesDynamics(filter);
    }

    @GetMapping("/detail/dynamics")
    public SalesDetailDynamicsDto salesDetailDynamics(@ParameterObject SalesFilter filter) {
        return salesService.salesDetailDynamics(filter);
    }

    @GetMapping("/report/stats")
    public SalesReportStatsDto getSalesReportStats(@ParameterObject SalesFilter filter) {
        return salesService.getSalesReportStats(filter);
    }

    @GetMapping("/stat")
    public ReceiptStatDto statistics(@ParameterObject SalesFilter filter) {
        return salesService.statistics(filter, false);
    }
}
