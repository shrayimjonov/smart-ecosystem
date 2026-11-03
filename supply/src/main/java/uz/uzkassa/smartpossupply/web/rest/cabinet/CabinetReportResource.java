package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.company.RevenueByRegionDTO;
import uz.uzkassa.smartpossupply.dto.company.TopClientDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.product.ProductResidueDTO;
import uz.uzkassa.smartpossupply.dto.product.TopProductDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.FacturaStatisticsFilter;
import uz.uzkassa.smartpossupply.service.CustomerService;
import uz.uzkassa.smartpossupply.service.FacturaService;
import uz.uzkassa.smartpossupply.service.ProductService;
import uz.uzkassa.smartpossupply.service.SupplierService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@RestController
@RequestMapping(ApiConstants.cabinetReportRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = PUBLIC)
public class CabinetReportResource {

    ProductService productService;
    CustomerService customerService;
    FacturaService facturaService;
    SupplierService supplierService;

    @Operation(summary = "Top Clients")
    @GetMapping(ApiConstants.topClients)
    public ResponseEntity<List<TopClientDTO>> getTopClient(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        return ResponseEntity.ok(customerService.getTopClients(filter));
    }

    @Operation(summary = "Invoice Statistics")
    @GetMapping(ApiConstants.invoiceStatistics)
    public ResponseEntity<List<FacturaStatisticsDTO>> getInvoiceStatistics(@ParameterObject FacturaStatisticsFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(facturaService.getStatistics(filter));
    }

    @Operation(summary = "Product Residue")
    @GetMapping(ApiConstants.productsResidue)
    public ResponseEntity<List<ProductResidueDTO>> getProductResidueList(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        return ResponseEntity.ok(productService.getProductResidueList(filter));
    }

    @Operation(summary = "Top Products")
    @GetMapping(ApiConstants.topProducts)
    public ResponseEntity<List<TopProductDTO>> getTopProducts(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setCompanyId(SecurityUtils.getCurrentCompanyId());
        return ResponseEntity.ok(productService.getTopProductList(filter));
    }

    @Operation(summary = "Company Revenue By Region")
    @GetMapping(ApiConstants.companyRevenue)
    public ResponseEntity<List<RevenueByRegionDTO>> getRevenueByRegion() {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(supplierService.getRevenueByRegion());
    }
}
