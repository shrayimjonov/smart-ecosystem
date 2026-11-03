package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationBlockDetailDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationBlockListDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationValidationDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.AggregationBlockFilter;
import uz.uzkassa.smartpossupply.filters.AggregationFilter;
import uz.uzkassa.smartpossupply.service.AggregationBlockService;
import uz.uzkassa.smartpossupply.service.ExportReportService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetAggregationBlockRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetAggregationBlockResource {

    AggregationBlockService aggregationBlockService;
    ExportReportService exportReportService;

    @GetMapping(ApiConstants.id)
    public ResponseEntity<AggregationBlockDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(aggregationBlockService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<AggregationBlockListDTO>> getList(@ParameterObject AggregationBlockFilter filter) {
        return ResponseEntity.ok(aggregationBlockService.getList(filter));
    }

    @PutMapping(ApiConstants.validate)
    public ResponseEntity<String> validate(@RequestBody AggregationValidationDTO aggregationValidationDTO) {
        return ResponseEntity.ok(aggregationBlockService.validate(aggregationValidationDTO));
    }

    @PutMapping("/resend/{id}")
    public ResponseEntity<Void> resend(@PathVariable Long id) {
        aggregationBlockService.resend(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = ApiConstants.exportCsv)
    public ResponseEntity<Void> generateCsv(@ParameterObject AggregationBlockFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.AGGREGATION_BLOCK, FileType.CSV, "AggregationBlocks.csv");
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<AggregationStatisticsDTO> getStatistics(@ParameterObject AggregationFilter filter) {
        return ResponseEntity.ok(aggregationBlockService.getStatistics(filter));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping("/remove/from/product/{id}")
    public ResponseEntity<Void> removeFromAggregationGroupProduct(@PathVariable("id") Long id) {
        aggregationBlockService.unsetPartyProductIdById(id);
        return ResponseEntity.ok().build();
    }
}
