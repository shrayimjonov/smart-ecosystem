package uz.uzkassa.silen.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.ExportReportDTO;
import uz.uzkassa.silen.dto.filter.ReportFilter;
import uz.uzkassa.silen.dto.filter.UnitCodesReportFilter;
import uz.uzkassa.silen.enumeration.ReportStatus;
import uz.uzkassa.silen.service.ExportReportService;
import uz.uzkassa.silen.service.ExportReportUploadService;

import java.util.EnumSet;

@RestController
@RequestMapping("/api/export-reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExportReportResource {

    ExportReportService exportReportService;
    ExportReportUploadService exportReportUploadService;

    @GetMapping
    public ResponseEntity<Page<ExportReportDTO>> getList(ReportFilter filter) {
        return ResponseEntity.ok(exportReportService.getList(filter));
    }

    @GetMapping("/statuses")
    public ResponseEntity<EnumSet<ReportStatus>> getStatuses() {
        return ResponseEntity.ok().body(EnumSet.allOf(ReportStatus.class));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> download(@PathVariable Long id) {
        return exportReportUploadService.download(id);
    }

    @GetMapping("/unit-codes")
    public ResponseEntity<Void> createUnitReport(@ModelAttribute UnitCodesReportFilter filter) {
        exportReportService.createUnitReport(filter);
        return ResponseEntity.ok().build();
    }
}
