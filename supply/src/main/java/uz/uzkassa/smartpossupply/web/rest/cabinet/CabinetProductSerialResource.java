package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.product.ProductCommonDTO;
import uz.uzkassa.smartpossupply.dto.report.ProductSerialReportDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialDetailDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialListDTO;
import uz.uzkassa.smartpossupply.dto.serial.ProductSerialLookUpDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.ProductSerialStatus;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.filters.ProductSerialFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.interfaces.CrudRest;
import uz.uzkassa.smartpossupply.interfaces.LookUpRest;
import uz.uzkassa.smartpossupply.service.ProductSerialService;
import uz.uzkassa.smartpossupply.service.ProductService;
import uz.uzkassa.smartpossupply.service.handlers.excel.ProductReportBySerialStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Rayimjonov Shuxratjon
 * Date: 11.05.2023
 */
@RestController
@RequestMapping(ApiConstants.cabinetProductSerialRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetProductSerialResource implements CrudRest<Long, ProductSerialDTO, ProductSerialDetailDTO, ProductSerialListDTO, ProductSerialFilter>, LookUpRest<ProductSerialLookUpDTO, ProductSerialFilter> {

    ProductSerialService productSerialService;
    ProductService productService;
    ProductReportBySerialStrategy productReportBySerialStrategy;

    @Override
    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ProductSerialDTO productSerialDTO) {
        return ResponseEntity.ok(productSerialService.create(productSerialDTO));
    }

    @Override
    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<ProductSerialDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(productSerialService.get(id));
    }

    @Override
    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ProductSerialDTO productSerialDTO) {
        return ResponseEntity.ok(productSerialService.update(id, productSerialDTO));
    }

    @Override
    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<ProductSerialListDTO>> getList(@ParameterObject ProductSerialFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(productSerialService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping("/products/lookup")
    public ResponseEntity<List<ProductCommonDTO>> productsLookUp(@ParameterObject ProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(productService.lookUpToSerial(filter));
    }

    @Override
    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<ProductSerialLookUpDTO>> lookUp(@ParameterObject ProductSerialFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setHideZero(true);
        return ResponseEntity.ok(productSerialService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<ProductSerialStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(ProductSerialStatus.class));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") ProductSerialStatus status) {
        productSerialService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/qty-statistics")
    public ResponseEntity<StockProductQtyStatisticsDTO> getQtyStatistics(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(productSerialService.getQtyStatistics(filter));
    }

    @GetMapping("/qty-details")
    public ResponseEntity<Page<StockProductQtyDetailListDTO>> getQtyDetails(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(productSerialService.getQtyDetails(filter));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productSerialService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/report")
    public ResponseEntity<Page<ProductSerialReportDTO>> getReport(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(productSerialService.getReport(filter));
    }

    @GetMapping("/report-total-qty")
    public ResponseEntity<BigDecimal> getReportTotalQty(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new BigDecimal(0));
        }
        return ResponseEntity.ok(productSerialService.getReportTotalQty(filter));
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Resource> getBalanceReportExportExcel(@ParameterObject BalanceReportFilter filter) {
        productReportBySerialStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportCSV)
    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Void> getBalanceReportExportCsv(@ParameterObject BalanceReportFilter filter) {
        productReportBySerialStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }
}
