package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationHistoryExportDTO;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationHistoryListDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.BatchValidationDTO;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponseResult;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.ClickhouseFilter;
import uz.uzkassa.smartpossupply.service.AggregationHistoryService;
import uz.uzkassa.smartpossupply.service.handlers.excel.AggregationHistoryStrategy;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetAggregationHistoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetAggregationHistoryResource {

    AggregationHistoryService aggregationHistoryService;
    AggregationHistoryStrategy aggregationHistoryStrategy;

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<AggregationHistoryListDTO>> getList(@ParameterObject ClickhouseFilter filter) {
        return ResponseEntity.ok(aggregationHistoryService.getList(filter));
    }

    @PostMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportExcel(@RequestBody List<AggregationHistoryExportDTO> items) {
        aggregationHistoryStrategy.generate(items, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Check)
    @PostMapping(ApiConstants.check)
    public ResponseEntity<CisInfoResponseResult> check(@RequestBody BatchValidationDTO validationDTO) {
        return ResponseEntity.ok(aggregationHistoryService.check(validationDTO));
    }

    @Operation(summary = "Public Check")
    @PostMapping("/public" + ApiConstants.check)
    public ResponseEntity<CisInfoResponseResult> publicCheck(@RequestBody BatchValidationDTO validationDTO) {
        return ResponseEntity.ok(aggregationHistoryService.publicCheck(validationDTO));
    }

    @Operation(summary = "Xtrace private check")
    @PostMapping("/xtrace/private" + ApiConstants.check)
    public ResponseEntity<CisInfoResponseResult> checkPrivateCodes(@RequestBody BatchValidationDTO validationDTO) {
        return ResponseEntity.ok(aggregationHistoryService.checkPrivateInfo(validationDTO));
    }

    @Operation(summary = "Xtrace public check")
    @PostMapping("/xtrace/public" + ApiConstants.check)
    public ResponseEntity<CisInfoResponseResult> checkPublicCodes(@RequestBody BatchValidationDTO validationDTO) {
        return ResponseEntity.ok(aggregationHistoryService.checkPublicInfo(validationDTO));
    }

    @Operation(summary = "GET Entity Types", operationId = "getEntityTypes")
    @GetMapping("/entity-types")
    public ResponseEntity<EnumSet<EntityTypeEnum>> getTransferTypes() {
        return ResponseEntity.ok(EnumSet.of(EntityTypeEnum.STOCK_IN, EntityTypeEnum.STOCK_OUT, EntityTypeEnum.STOCK_RETURN, EntityTypeEnum.STOCK_ADJUSTMENT, EntityTypeEnum.STOCK_TRANSFER));
    }
}
