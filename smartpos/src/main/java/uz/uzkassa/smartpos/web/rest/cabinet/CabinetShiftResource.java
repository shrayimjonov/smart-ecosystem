package uz.uzkassa.smartpos.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.shift.ShiftReportDTO;
import uz.uzkassa.smartpos.dto.shift.ShiftReportListDto;
import uz.uzkassa.smartpos.dto.shift.ShiftReportStatDTO;
import uz.uzkassa.smartpos.filter.ShiftReportFilter;
import uz.uzkassa.smartpos.report.ShiftExcelReport;
import uz.uzkassa.smartpos.service.ShiftReportService;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/cabinet/shifts/report")
@RequiredArgsConstructor
public class CabinetShiftResource {
    private final ShiftReportService shiftReportService;
    private final ShiftExcelReport shiftExcelReport;

    @GetMapping
    public Page<ShiftReportListDto> getReport(@ParameterObject ShiftReportFilter filter) {
        return shiftReportService.getReportForCabinet(filter);
    }

    @GetMapping("/excel")
    public ResponseEntity<Resource> getExcelReport(@ParameterObject ShiftReportFilter filter) throws IOException {
        return ServerUtils.getResponseEntity(shiftExcelReport.getExcelReportCabinet(filter), "Отчет_по_сменам_Smartpos");
    }

    @GetMapping("/stats")
    public ShiftReportStatDTO getStats(@ParameterObject ShiftReportFilter filter) {
        return shiftReportService.getStatsForCabinet(filter);
    }

    @GetMapping("/{branchId}/{shiftNumber}")
    public ShiftReportDTO getShiftReport(@PathVariable Long branchId, @PathVariable Long shiftNumber) {
        return shiftReportService.getReportById(branchId, shiftNumber);
    }
}
