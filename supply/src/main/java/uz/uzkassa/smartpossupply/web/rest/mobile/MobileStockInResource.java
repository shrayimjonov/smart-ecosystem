package uz.uzkassa.smartpossupply.web.rest.mobile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonNameDTO;
import uz.uzkassa.smartpossupply.dto.factura.FacturaLookUpDTO;
import uz.uzkassa.smartpossupply.dto.factura.Status;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.dto.stock.CommonStockStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stock.StockProductLookUpDTO;
import uz.uzkassa.smartpossupply.dto.stockin.StockInDTO;
import uz.uzkassa.smartpossupply.dto.stockin.StockInDetailDTO;
import uz.uzkassa.smartpossupply.dto.stockin.StockInListDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.RelationType;
import uz.uzkassa.smartpossupply.enums.StockStatus;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.filters.FacturaFilter;
import uz.uzkassa.smartpossupply.filters.StockFilter;
import uz.uzkassa.smartpossupply.filters.StockInProductFilter;
import uz.uzkassa.smartpossupply.service.FacturaService;
import uz.uzkassa.smartpossupply.service.StockInService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.mobileStockInRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileStockInResource {

    StockInService stockInService;
    FacturaService facturaService;

    @Operation(summary = ApiConstants.In)
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody StockInDTO stockInDTO) {
        return ResponseEntity.ok(stockInService.create(stockInDTO));
    }

    @Operation(summary = ApiConstants.In)
    @PostMapping("/aggregation/{aggregationId}")
    public ResponseEntity<Long> createByAggregation(@PathVariable Long aggregationId) {
        return ResponseEntity.ok(stockInService.createByAggregation(aggregationId));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockInDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockInService.get(id, null));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id + "/{customerId}")
    public ResponseEntity<StockInDetailDTO> getByCustomer(@PathVariable Long id, @PathVariable Long customerId) {
        return ResponseEntity.ok(stockInService.get(id, customerId));
    }

    @Operation(summary = "Get Aggregation Codes")
    @GetMapping("/{id}/{stockProductId}/aggregation-codes")
    public ResponseEntity<Map<CodeType, List<CheckedCodeDTO>>> getAggregationCodes(
            @PathVariable Long id,
            @PathVariable Long stockProductId
    ) {
        return ResponseEntity.ok(stockInService.getAggregationCodes(stockProductId));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody StockInDTO stockInDTO) {
        return ResponseEntity.ok(stockInService.update(id, stockInDTO));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<StockInListDTO>> getList(@ParameterObject StockFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(stockInService.getList(filter));
    }

    @Deprecated
    @Operation(summary = ApiConstants.LookUp)
    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<StockProductLookUpDTO>> lookUp(@ParameterObject StockInProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setTransferType(TransferType.IN.name());
        return ResponseEntity.ok(stockInService.lookUpProduct(filter));
    }

    @Operation(summary = "Product Lookup")
    @GetMapping("/product/lookup")
    public ResponseEntity<List<StockProductLookUpDTO>> lookUpProduct(@ParameterObject StockInProductFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setTransferType(TransferType.IN.name());
        return ResponseEntity.ok(stockInService.lookUpProduct(filter));
    }

    @Operation(summary = "Stock Product Lookup")
    @GetMapping("/stock-product/lookup")
    public ResponseEntity<List<CommonNameDTO>> lookUpStockProduct(@ParameterObject StockInProductFilter filter) {
        filter.setTransferType(TransferType.IN.name());
        return ResponseEntity.ok(stockInService.lookUpStockProduct(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(StockStatus.DRAFT, StockStatus.CHECKING, StockStatus.CHECKING_ERROR, StockStatus.COMPLETED, StockStatus.CANCELLED));
    }

    @Operation(summary = ApiConstants.UpdateStatus)
    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestParam("status") String status, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(stockInService.updateStatus(id, StockStatus.valueOf(status), lastModifiedDate));
    }

    @Operation(summary = ApiConstants.Copy)
    @PutMapping(ApiConstants.copy)
    public ResponseEntity<Long> copy(@PathVariable Long id) {
        return ResponseEntity.ok(stockInService.copy(id));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockInService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<CommonStockStatisticsDTO> getStatistics(@ParameterObject StockFilter filter) {
        return ResponseEntity.ok(stockInService.getStatistics(filter));
    }

    @GetMapping("/relation-types")
    public ResponseEntity<EnumSet<RelationType>> getRelationTypes() {
        return ResponseEntity.ok(EnumSet.allOf(RelationType.class));
    }

    @Operation(summary = "Stock in factura lookUp")
    @GetMapping("/factura" + ApiConstants.lookUp)
    public ResponseEntity<List<FacturaLookUpDTO>> lookUp(@ParameterObject FacturaFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        filter.setIncludeStatuses(String.join(",", Status.ACCEPTED.name(), Status.PENDING.name()));
        return ResponseEntity.ok(facturaService.lookUp(filter));
    }
}
