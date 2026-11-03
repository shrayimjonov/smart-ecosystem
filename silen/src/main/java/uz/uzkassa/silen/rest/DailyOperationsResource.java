package uz.uzkassa.silen.rest;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.*;
import uz.uzkassa.silen.enumeration.DailyOperationTypes;
import uz.uzkassa.silen.enumeration.ExportReportType;
import uz.uzkassa.silen.enumeration.FileType;
import uz.uzkassa.silen.service.DailyOperationsService;
import uz.uzkassa.silen.service.ExportReportService;

import java.util.List;

@RestController
@RequestMapping("/api/daily/operations")
@RequiredArgsConstructor
@Validated
public class DailyOperationsResource {
    private final DailyOperationsService dailyOperationsService;
    private final ExportReportService exportReportService;


    @GetMapping
    public ResponseEntity<Page<DailyOperationsDTO>> getAll(@ModelAttribute @Valid DailyOperationsFilter filter) {
        return ResponseEntity.ok(dailyOperationsService.findAll(filter));
    }

    @GetMapping("/export")
    public ResponseEntity<Page<DailyOperationsDTO>> exportAll(@ModelAttribute @Valid DailyOperationsFilter filter) {
        exportReportService.export(filter, ExportReportType.DAILY_OPERATIONS, FileType.EXCEL, "Material report");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<DailyOperationsStats> getStats(@ModelAttribute @Valid DailyOperationsFilter filter) {
        return ResponseEntity.ok(dailyOperationsService.getStats(filter));
    }

    @GetMapping("/material")
    public ResponseEntity<List<DailyOperationsMaterialStats>> materialReport(@ModelAttribute @Valid DailyOperationsFilter filter) {
        return ResponseEntity.ok(dailyOperationsService.materialReport(filter));
    }

    @GetMapping("/material/export")
    public ResponseEntity<List<DailyOperationsMaterialStats>> materialReportExport(@ModelAttribute @Valid DailyOperationsFilter filter) {
        exportReportService.export(filter, ExportReportType.MATERTIAL, FileType.EXCEL, "Material report");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/type")
    public ResponseEntity<List<SelectItem>> getStats()  {
        return ResponseEntity.ok(DailyOperationTypes.getAll());
    }


    @GetMapping("/only-for-check")
    public ResponseEntity<Void> createFromAggregationCheck(@ModelAttribute @Valid DailyOperationsFilter filter) {
        dailyOperationsService.createFromShipmentCheck(filter);
        return ResponseEntity.ok().build();
    }
}
