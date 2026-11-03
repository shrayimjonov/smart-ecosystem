package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aggregation.*;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.dto.markorder.MarkOrderProductLookUpDTO;
import uz.uzkassa.smartpossupply.enums.AggregationStatus;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.filters.AggregationFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.service.AggregationService;
import uz.uzkassa.smartpossupply.service.ExportReportService;
import uz.uzkassa.smartpossupply.service.ProductService;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetAggregationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetAggregationResource {

    AggregationService aggregationService;
    ProductService productService;
    ExportReportService exportReportService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping()
    public ResponseEntity<String> create(@RequestBody AggregationCreateDTO aggregationCreateDTO) {
        return ResponseEntity.ok(aggregationService.create(aggregationCreateDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<AggregationDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(aggregationService.getPallet(id));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<AggregationListDTO>> getList(@ParameterObject AggregationFilter filter) {
        return ResponseEntity.ok(aggregationService.getList(filter));
    }

    @Operation(summary = ApiConstants.Stamp)
    @GetMapping(ApiConstants.stamps)
    public ResponseEntity<List<String>> getStampList(@ParameterObject AggregationFilter filter) {
        return ResponseEntity.ok(aggregationService.getStampList(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<AggregationStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(AggregationStatus.class));
    }

    @GetMapping(ApiConstants.packageTypes)
    public ResponseEntity<EnumSet<PackageType>> getPackageTypes() {
        return ResponseEntity.ok(EnumSet.of(PackageType.BOX, PackageType.PALLET, PackageType.BLOCK));
    }

    @GetMapping(value = ApiConstants.exportCsv)
    public ResponseEntity<Void> generateCsv(@ParameterObject AggregationFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.AGGREGATION, FileType.CSV, "Aggregation.csv");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Products Look Up")
    @GetMapping("/products/lookup")
    public ResponseEntity<List<MarkOrderProductLookUpDTO>> getProductLookUp(@ParameterObject ProductFilter filter) {
        return ResponseEntity.ok(productService.getLookUpToMarking(filter));
    }

    @PutMapping(ApiConstants.validate)
    public ResponseEntity<String> validate(@RequestBody AggregationValidationDTO aggregationValidationDTO) {
        return ResponseEntity.ok(aggregationService.validateSimple(aggregationValidationDTO));
    }

    @Operation(summary = ApiConstants.Send)
    @PutMapping("/resend/{id}")
    public ResponseEntity<Void> resend(@PathVariable Long id) {
        aggregationService.resend(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check/turon")
    public ResponseEntity<CisInfoResponse[]> checkTuron(@RequestBody List<String> codes) {
        return ResponseEntity.ok(aggregationService.checkTuron(codes));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<AggregationStatisticsDTO> getStatistics(@ParameterObject AggregationFilter filter) {
        return ResponseEntity.ok(aggregationService.getStatistics(filter));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping("remove/from/product/{id}")
    public ResponseEntity<Void> removeFromAggregationGroupProduct(@PathVariable("id") Long id) {
        aggregationService.removeFromPartyProduct(id);
        return ResponseEntity.ok().build();
    }
}
