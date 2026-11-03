package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.mark.MarkListDTO;
import uz.uzkassa.smartpossupply.dto.mark.MarkStatisticsDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.enums.MarkStatus;
import uz.uzkassa.smartpossupply.filters.MarkFilter;
import uz.uzkassa.smartpossupply.service.ExportReportService;
import uz.uzkassa.smartpossupply.service.MarkService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetMarkRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetMarkResource {

    MarkService markService;
    ExportReportService exportReportService;

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<MarkListDTO>> getList(@ParameterObject MarkFilter filter) {
        return ResponseEntity.ok(markService.getList(filter));
    }

    @Operation(summary = ApiConstants.Stamp)
    @GetMapping(ApiConstants.stamps)
    public ResponseEntity<List<MarkListDTO>> getStampList(@ParameterObject MarkFilter filter) {
        filter.setPage(0);
        filter.setSize(10000);
        return ResponseEntity.ok(markService.getList(filter).getContent());
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<MarkStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(MarkStatus.class));
    }

    @GetMapping(value = ApiConstants.exportCsv)
    public ResponseEntity<Void> generateCsv(@ParameterObject MarkFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.MARK, FileType.CSV, "Mark.csv");
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<MarkStatisticsDTO> getStatistics(@ParameterObject MarkFilter filter) {
        return ResponseEntity.ok(markService.getStatistics(filter));
    }

    @Operation(summary = ApiConstants.Delete)
    @DeleteMapping("remove/from/product/{id}")
    public ResponseEntity<Void> removeFromAggregationGroupProduct(@PathVariable("id") Long id) {
        markService.removeFromAggregationGroupProduct(id);
        return ResponseEntity.ok().build();
    }
}
