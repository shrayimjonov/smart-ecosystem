package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.filter.ShiftFilter;
import uz.uzkassa.smartpos.dto.shift.ShiftDTO;
import uz.uzkassa.smartpos.dto.shift.ShiftReportDTO;
import uz.uzkassa.smartpos.dto.shift.ShiftReportStatDTO;
import uz.uzkassa.smartpos.dto.shift.ShiftTotalStatisticsDTO;
import uz.uzkassa.smartpos.service.ShiftReportService;

@RestController
@Slf4j
@RequestMapping("/api/shifts")
public class ShiftReportResource {
    private final ShiftReportService shiftReportService;

    public ShiftReportResource(ShiftReportService shiftReportService) {
        this.shiftReportService = shiftReportService;
    }

    @PostMapping
    @Deprecated
    public ResponseEntity<ShiftDTO> createOrGet(@RequestParam(required = false) Long fiscalNumber) {
        return ResponseEntity.ok(shiftReportService.createOrGet(fiscalNumber));
    }

    @PutMapping
    @Deprecated
    public ResponseEntity<Void> closeShift(@RequestParam(required = false) Long fiscalNumber) {
        shiftReportService.closeShift(fiscalNumber, false);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/web")
    public ResponseEntity<ShiftReportDTO> closeShiftWeb(@RequestParam(required = false) Long fiscalNumber) {
        return ResponseEntity.ok(shiftReportService.closeShift(fiscalNumber, true));
    }

    @PutMapping("/session")
    @Deprecated
    public ResponseEntity<Void> closeSession() {
        shiftReportService.closeSession();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/report")
    public ResponseEntity<Void> create(@RequestBody @Valid ShiftReportDTO reportDTO) {
        shiftReportService.save(reportDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/report")
    public Page<ShiftReportDTO> getReport(@ParameterObject BaseFilter filter) {
        return shiftReportService.getReport(filter);
    }

    @GetMapping("/report/{branchId}/{shiftNumber}")
    public ShiftReportDTO getZReport(@PathVariable Long branchId, @PathVariable Long shiftNumber) {
        return shiftReportService.getReportById(branchId, shiftNumber);
    }

    @GetMapping("/report/register")
    public ShiftTotalStatisticsDTO getTotalReportStatistics(@ParameterObject ShiftFilter filter) {
        return shiftReportService.getTotalStatistics(filter);
    }

    @GetMapping("/report/total")
    public ShiftReportStatDTO getStatistics(@ParameterObject ShiftFilter filter) {
        return shiftReportService.getStats(filter);
    }

    @GetMapping("/web/x-report")
    public ShiftReportDTO xReport(@ParameterObject ShiftFilter filter) {
        return shiftReportService.xReport(filter);
    }

    @GetMapping("/report/stats")
    public ShiftReportStatDTO getReportStat(@ParameterObject BaseFilter filter) {
        return shiftReportService.getReportStat(filter);
    }
}
