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
import uz.uzkassa.smartpossupply.dto.productcontract.ProductContractDTO;
import uz.uzkassa.smartpossupply.dto.productcontract.ProductContractDetailDTO;
import uz.uzkassa.smartpossupply.dto.productcontract.ProductContractListDTO;
import uz.uzkassa.smartpossupply.dto.productcontract.ProductContractLookupDTO;
import uz.uzkassa.smartpossupply.dto.report.ProductContractReportDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyDetailListDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductQtyStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.interfaces.CrudRest;
import uz.uzkassa.smartpossupply.interfaces.LookUpRest;
import uz.uzkassa.smartpossupply.service.ProductContractService;
import uz.uzkassa.smartpossupply.service.handlers.excel.ProductReportByContractStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.07.2023
 */
@RestController
@RequestMapping(ApiConstants.cabinetProductContractRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetProductContractResource implements CrudRest<Long, ProductContractDTO, ProductContractDetailDTO, ProductContractListDTO, BaseFilter>, LookUpRest<ProductContractLookupDTO, BaseFilter> {

    ProductContractService productContractService;
    ProductReportByContractStrategy productReportByContractStrategy;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    @Override
    public ResponseEntity<Long> create(@RequestBody ProductContractDTO productContractDTO) {
        return ResponseEntity.ok(productContractService.create(productContractDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    @Override
    public ResponseEntity<ProductContractDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(productContractService.get(id));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody ProductContractDTO productContractDTO) {
        return ResponseEntity.ok(productContractService.update(id, productContractDTO));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    @Override
    public ResponseEntity<Page<ProductContractListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(productContractService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    @Override
    public ResponseEntity<List<ProductContractLookupDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(productContractService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productContractService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/qty-statistics")
    public ResponseEntity<StockProductQtyStatisticsDTO> getQtyStatistics(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(productContractService.getQtyStatistics(filter));
    }

    @GetMapping("/qty-details")
    public ResponseEntity<Page<StockProductQtyDetailListDTO>> getQtyDetails(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(productContractService.getQtyDetails(filter));
    }

    @GetMapping("/report")
    public ResponseEntity<Page<ProductContractReportDTO>> getReport(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(productContractService.getReport(filter));
    }

    @GetMapping("/report-total-qty")
    public ResponseEntity<BigDecimal> getReportTotalQty(@ParameterObject BalanceReportFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new BigDecimal(0));
        }
        return ResponseEntity.ok(productContractService.getReportTotalQty(filter));
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Resource> getBalanceReportExportExcel(@ParameterObject BalanceReportFilter filter) {
        productReportByContractStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportCSV)
    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Void> getBalanceReportExportCsv(@ParameterObject BalanceReportFilter filter) {
        productReportByContractStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }
}
