package uz.uzkassa.silen.rest;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.AggregationHistoryDTO;
import uz.uzkassa.silen.dto.MaterialReportDTO;
import uz.uzkassa.silen.dto.MaterialReportFilter;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.AggregationFilter;
import uz.uzkassa.silen.dto.marking.AggregationStatsDTO;
import uz.uzkassa.silen.enumeration.AggregationStatus;
import uz.uzkassa.silen.enumeration.CodeType;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.excel.MaterialReportExcelHandler;
import uz.uzkassa.silen.integration.asilbelgi.dto.CisInfoResponse;
import uz.uzkassa.silen.service.AggregationHistoryService;
import uz.uzkassa.silen.service.RefundService;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.AggregationHistory}.
 */
@RestController
@RequestMapping("/api/aggregation/histories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AggregationHistoryResource {

    AggregationHistoryService aggregationHistoryService;
    MaterialReportExcelHandler materialReportExcelHandler;
    RefundService refundService;

    @GetMapping("/stats")
    public ResponseEntity<AggregationStatsDTO> getStats(@ModelAttribute AggregationFilter filter) {
        return ResponseEntity.ok(aggregationHistoryService.getStats(filter));
    }

    @GetMapping
    public ResponseEntity<Page<AggregationHistoryDTO>> getAll(@ModelAttribute AggregationFilter filter) {
        return ResponseEntity.ok(aggregationHistoryService.findAll(filter));
    }

    @GetMapping("/package-types")
    public ResponseEntity<List<SelectItem>> getPackageTypes() {
        return ResponseEntity.ok(Arrays.asList(PackageType.BLOCK.toSelectItem(), PackageType.BOX.toSelectItem(), PackageType.PALLET.toSelectItem()));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<SelectItem>> getStatuses() {
        return ResponseEntity.ok(AggregationStatus.getAll());
    }

    @PutMapping("/utilize")
    public ResponseEntity<HashMap<CodeType, List<String>>> utilize(@RequestBody Set<String> printCodes) {
        return ResponseEntity.ok(aggregationHistoryService.utilize(printCodes));
    }

    @PutMapping("/returned")
    public ResponseEntity<String> refundByPrintCode(@RequestParam("printCode") String printCode) {
        return ResponseEntity.ok(refundService.save(printCode));
    }

    @PostMapping("/check")
    public ResponseEntity<CisInfoResponse[]> checkCodeFromTrueApi(@RequestBody Set<String> printCode) {
        return ResponseEntity.ok(aggregationHistoryService.checkMarkFromTrueApi(printCode));
    }

    @GetMapping("/report/material")
    public ResponseEntity<Page<MaterialReportDTO>> report(@ModelAttribute MaterialReportFilter filter) {
        return ResponseEntity.ok(aggregationHistoryService.materialReport(filter));
    }

    @GetMapping("/report/material/excel")
    public void reportToExcel(@ModelAttribute MaterialReportFilter filter, HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=material-report.xlsx");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        materialReportExcelHandler.write(filter, response.getOutputStream());
    }
}
