package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.report.WarehouseProductReportDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckStatusResponseDTO;
import uz.uzkassa.smartpossupply.dto.stock.CheckedCodeDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.ExpeditorStockTransferDetailDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.ExpeditorStockTransferStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferCreateFromOrderDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferCreateFromWarehouseProductDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferDetailDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferListDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferProductAggregationsDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferProductListDTO;
import uz.uzkassa.smartpossupply.dto.stocktransfer.StockTransferUpdateStatusDTO;
import uz.uzkassa.smartpossupply.enums.CodeType;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.StockTransferReason;
import uz.uzkassa.smartpossupply.enums.StockTransferStatus;
import uz.uzkassa.smartpossupply.filters.BalanceReportFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.filters.StockTransferFilter;
import uz.uzkassa.smartpossupply.service.ProductService;
import uz.uzkassa.smartpossupply.service.StockTransferService;
import uz.uzkassa.smartpossupply.service.handlers.excel.ExpeditorStockTransferDetailStrategy;
import uz.uzkassa.smartpossupply.service.handlers.excel.StockTransferListStrategy;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetStockTransferRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetStockTransferResource {

    StockTransferService stockTransferService;
    ProductService productService;
    StockTransferListStrategy stockTransferListStrategy;
    ExpeditorStockTransferDetailStrategy exportReportStrategy;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody StockTransferDTO stockTransferDTO) {
        return ResponseEntity.ok(stockTransferService.create(stockTransferDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody StockTransferDTO stockTransferDTO) {
        return ResponseEntity.ok(stockTransferService.update(id, stockTransferDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<StockTransferDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockTransferService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<StockTransferListDTO>> getList(@ParameterObject StockTransferFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(stockTransferService.getList(filter));
    }

    @GetMapping("/products")
    public ResponseEntity<Page<StockTransferProductListDTO>> getProductList(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(productService.getListToStockTransfer(filter));
    }

    @GetMapping("/products/lookup")
    public ResponseEntity<List<StockTransferProductListDTO>> productsLookup(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(productService.getLookupToStockTransfer(filter));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<StockTransferStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(StockTransferStatus.class));
    }

    @GetMapping(ApiConstants.reasons)
    public ResponseEntity<EnumSet<StockTransferReason>> getReason() {
        return ResponseEntity.ok(EnumSet.allOf(StockTransferReason.class));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<CheckStatusResponseDTO> updateStatus(@PathVariable Long id, @RequestBody StockTransferUpdateStatusDTO updateStatusDTO, @RequestParam("lastModifiedDate") LocalDateTime lastModifiedDate) {
        return ResponseEntity.ok(stockTransferService.updateStatus(id, updateStatusDTO, lastModifiedDate));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockTransferService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCsv)
    public ResponseEntity<Void> exportCsv(@ParameterObject StockTransferFilter filter) {
        stockTransferListStrategy.generate(filter, FileType.CSV);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Resource> exportExcel(@ParameterObject StockTransferFilter filter) {
        stockTransferListStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.check)
    public ResponseEntity<List<CisInfoResponse>> check(@RequestBody @Valid BatchValidationDTO validationDTO) {
        return ResponseEntity.ok(stockTransferService.check(validationDTO));
    }

    @PostMapping("/convert/from-orders")
    public ResponseEntity<Long> convertFromOrder(@RequestBody StockTransferCreateFromOrderDTO stockTransferCreateFromOrderDTO) {
        return ResponseEntity.ok(stockTransferService.createFromOrder(stockTransferCreateFromOrderDTO));
    }

    @PostMapping("/convert/from-warehouse-products")
    public ResponseEntity<Long> convertFromWarehouseProduct(@RequestBody StockTransferCreateFromWarehouseProductDTO transferCreateFromWarehouseProductDTO) {
        return ResponseEntity.ok(stockTransferService.createFromWarehouseProduct(transferCreateFromWarehouseProductDTO));
    }

    @GetMapping("/current-stock")
    public ResponseEntity<List<WarehouseProductReportDTO>> currentStock(@ParameterObject BalanceReportFilter filter) {
        return ResponseEntity.ok(stockTransferService.getCurrentStock(filter));
    }

    @GetMapping("/{stockTransferProductId}/aggregation-codes")
    public ResponseEntity<Map<CodeType, List<CheckedCodeDTO>>> getAggregationCodes(@PathVariable Long stockTransferProductId) {
        return ResponseEntity.ok(stockTransferService.getAggregationCodes(stockTransferProductId));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject StockTransferFilter filter) {
        return ResponseEntity.ok(stockTransferService.lookUp(filter));
    }

    @GetMapping("{id}/expeditor-detail")
    public ResponseEntity<ExpeditorStockTransferDetailDTO> getDetailForExpeditor(@PathVariable Long id) {
        return ResponseEntity.ok(stockTransferService.getDetailForExpeditor(id));
    }

    @GetMapping("/expeditor-detail" + ApiConstants.exportExcel)
    public ResponseEntity<Resource> exportExpeditorDetailExcel(@ParameterObject BaseFilter filter) {
        exportReportStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{id}" + ApiConstants.statistics)
    public ResponseEntity<ExpeditorStockTransferStatisticsDTO> getStatisticsForExpeditor(@PathVariable Long id) {
        return ResponseEntity.ok(stockTransferService.getStatisticsForExpeditor(id));
    }

    @GetMapping("{id}/products/{productId}/aggregations")
    public ResponseEntity<StockTransferProductAggregationsDTO> getProductAggregations(@PathVariable Long id, @PathVariable Long productId) {
        return ResponseEntity.ok(stockTransferService.getProductAggregations(id, productId));
    }
}
