package uz.uzkassa.smartpos.web.rest.terminal;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.filter.ShiftFilter;
import uz.uzkassa.smartpos.dto.shift.ShiftDTO;
import uz.uzkassa.smartpos.dto.shift.ShiftReportDTO;
import uz.uzkassa.smartpos.service.ShiftReportService;

@RestController
@RequestMapping("/api/v1/terminals/shifts")
@RequiredArgsConstructor
public class TerminalShiftResource {
    private final ShiftReportService shiftReportService;

    @PostMapping("/open")
    public ShiftDTO createOrGet() {
        return shiftReportService.createOrGet(null);
    }

    @PutMapping("/close")
    public void close() {
        shiftReportService.closeShift(null, false);
    }

    @GetMapping("/x-report")
    public ShiftReportDTO xReport(@ParameterObject ShiftFilter filter) {
        return shiftReportService.xReport(filter);
    }

    @GetMapping("/total")
    public ShiftReportDTO totalReport(@RequestParam(required = false) Long branchId) {
        return shiftReportService.totalReport(branchId);
    }
}
