package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.shift.*;
import uz.uzkassa.smartpossupply.filters.ShiftReportFilter;
import uz.uzkassa.smartpossupply.service.ShiftReportService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 27.12.2022 14:40
 */
@RestController
@RequestMapping(ApiConstants.shiftReportRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CabinetShiftReportResource {

    ShiftReportService shiftReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ShiftReportDTO shiftReportDTO) {
        return ResponseEntity.ok(shiftReportService.save(shiftReportDTO));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<ShiftReportStatisticDTO> getStatistics(@ParameterObject ShiftReportFilter filter) {
        return ResponseEntity.ok(shiftReportService.getStatistics(filter));
    }

    @GetMapping("/x-report")
    public ResponseEntity<XReportDetailDTO> xReport() {
        return ResponseEntity.ok(shiftReportService.xReport());
    }

    @GetMapping("/z-report")
    public ResponseEntity<Page<ZReportListDTO>> zReport(@ParameterObject ShiftReportFilter filter) {
        return ResponseEntity.ok(shiftReportService.zReport(filter));
    }

    @GetMapping("/z-report/{branchId}/{shiftId}")
    public ResponseEntity<ZReportDTO> zReportDetails(@PathVariable Long branchId, @PathVariable Long shiftId) {
        return ResponseEntity.ok(shiftReportService.zReportDetails(branchId, shiftId));
    }

    @GetMapping("/registration")
    public ResponseEntity<RegistrationReportDTO> getRegistrationReport() {
        return ResponseEntity.ok(shiftReportService.getRegistrationReport());
    }
}
