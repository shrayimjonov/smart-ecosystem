package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.reconciliationact.PackageQuantityDTO;
import uz.uzkassa.smartpossupply.dto.stock.*;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutListDTO;
import uz.uzkassa.smartpossupply.dto.stockout.StockOutLookUpDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.filters.StockInProductFilter;
import uz.uzkassa.smartpossupply.service.PackageQuantityService;
import uz.uzkassa.smartpossupply.service.StockInService;
import uz.uzkassa.smartpossupply.service.StockOutService;
import uz.uzkassa.smartpossupply.service.handlers.excel.StockOutListStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping(ApiConstants.cabinetStockOutRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetStockOutResource {

    StockOutService stockOutService;
    StockInService stockInService;
    StockOutListStrategy stockOutListStrategy;
    private final PackageQuantityService packageQuantityService;

    @Operation(summary = ApiConstants.Out)
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody StockOutDTO stockDTO) {
        return ResponseEntity.ok(stockOutService.create(stockDTO));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody StockOutDTO stockDTO) {
        return ResponseEntity.ok(stockOutService.update(id, stockDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockOutDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockOutService.get(id));
    }

    @Operation(summary = "Convert by Stock In")
    @GetMapping("/convert/{stockInId}/")
    public ResponseEntity<StockOutDetailDTO> convertByStockIn(
            @PathVariable Long stockInId
    ) {
        return ResponseEntity.ok(stockOutService.convertByStockIn(stockInId));
    }

    @Operation(summary = "Get Aggregation Codes")
    @GetMapping("/{stockProductId}/aggregation-codes")
    public ResponseEntity<Map<CodeType, List<CheckedCodeDTO>>> getAggregationCodes(
            @PathVariable Long stockProductId
    ) {
        return ResponseEntity.ok(stockOutService.getAggregationCodes(stockProductId));
    }

    @Operation(summary = ApiConstants.Check)
    @PostMapping(ApiConstants.check)
    public ResponseEntity<List<CisInfoResponse>> batchCheck(@RequestBody @Valid BatchValidationDTO validationDTO) {
        log.info("[stock-out/check] STEP 1 - request from front | dto={}", validationDTO);
        List<CisInfoResponse> result = stockOutService.batchCheck(validationDTO);
        log.info("[stock-out/check] STEP 4 - response to front | result={}", result);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<StockOutListDTO>> getList(@ParameterObject StockFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(stockOutService.getList(filter));
    }

    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<StockOutLookUpDTO>> lookUp(@ParameterObject StockFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(stockOutService.lookUp(filter));
    }

    @Operation(summary = ApiConstants.ExportCSV)
    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Void> exportCsv(@ParameterObject StockFilter filter) {
        stockOutListStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.ExportExcel)
    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Resource> exportExcel(@ParameterObject StockFilter filter) {
        stockOutListStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(StockStatus.DRAFT, StockStatus.COMPLETED, StockStatus.CANCELLED));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestParam("status") String status, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(stockOutService.updateStatus(id, StockStatus.valueOf(status), lastModifiedDate));
    }

    @Operation(summary = "Assign cashier")
    @PatchMapping("/assign/cashier/{id}")
    public ResponseEntity<Long> assignCashier(@PathVariable Long id, @RequestParam(name = "cashierId") Long cashierId) {
        stockOutService.assignCashier(id, cashierId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Copy)
    @PutMapping(ApiConstants.copy)
    public ResponseEntity<Long> copy(@PathVariable Long id) {
        return ResponseEntity.ok(stockOutService.copy(id));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockOutService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<CommonStockStatisticsDTO> getStatistics(@ParameterObject StockFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new CommonStockStatisticsDTO());
        }
        return ResponseEntity.ok(stockOutService.getStatistics(filter));
    }

    @Operation(summary = "Product Lookup")
    @GetMapping("/product/lookup")
    public ResponseEntity<List<StockProductLookUpDTO>> lookUpProduct(@ParameterObject StockInProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        if (filter.getCustomerId() == null) {
            throw new BadRequestException("Выберите клиента", "Send customerId on the filter");
        }
        filter.setTransferType(TransferType.OUT.name());
        return ResponseEntity.ok(stockInService.lookUpProduct(filter));
    }

    @GetMapping(ApiConstants.exportCsv + "/products/{stockProductId}")
    public ResponseEntity<?> exportToCsvProducts(@PathVariable Long stockProductId) {
        return stockOutService.exportToCsvProducts(stockProductId);
    }

    @GetMapping(ApiConstants.exportExcel + "/products/{stockProductId}")
    public ResponseEntity<?> exportToExcelProducts(@PathVariable Long stockProductId) {
        return stockOutService.exportToExcelProducts(stockProductId);
    }

    @GetMapping("/{id}/waybill")
    public ResponseEntity<StockWaybillCreateDTO> getWayBill(@PathVariable Long id) {
        return ResponseEntity.ok(stockOutService.getWayBill(id));
    }


    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id + "/check")
    public ResponseEntity<Void> check(@PathVariable Long id, @RequestBody List<PackageQuantityDTO> quantityDTOS) {
        packageQuantityService.saveTest(id, quantityDTOS);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id + "/read")
    public ResponseEntity<List<PackageQuantityDTO>> readTest(@PathVariable Long id) {
        return ResponseEntity.ok(packageQuantityService.getByCompositeId(id));
    }
}
