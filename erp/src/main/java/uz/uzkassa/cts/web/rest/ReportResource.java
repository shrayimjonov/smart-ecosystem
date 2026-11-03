package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.report.*;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.ActivityTypeReportFilter;
import uz.uzkassa.cts.filter.BaseReportFilter;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.handlers.export.excel.ReportDeviceByBranchExportExcelHandler;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.ReportService;
import uz.uzkassa.cts.validator.LocalizationMessage;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.io.IOException;
import java.util.List;

import static uz.uzkassa.cts.constants.MessageConstants.EXPORT_FILE_FAILED;


@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports")
@Slf4j
@Deprecated
public class ReportResource {

    private final ReportService reportService;
    private final ReportDeviceByBranchExportExcelHandler reportDeviceByBranchExcelHandler;
    private final LocalizationMessage localizationMessage;
    private final ExportReportService exportReportService;

    public ReportResource(ReportService reportService, ReportDeviceByBranchExportExcelHandler reportDeviceByBranchExcelHandler, LocalizationMessage localizationMessage, ExportReportService exportReportService) {
        this.reportService = reportService;
        this.reportDeviceByBranchExcelHandler = reportDeviceByBranchExcelHandler;
        this.localizationMessage = localizationMessage;
        this.exportReportService = exportReportService;
    }

    @Operation(summary = "Customer Statistics By Region Report")
    @GetMapping(value = "/customer")
    public ResponseEntity<List<StatisticsByRegionReportDTO>> getCustomerStatisticsByRegionReport(BaseReportFilter filter) {
        log.debug("REST request to get CustomerReportDTO: {}", filter);
        return ResponseEntity.ok(reportService.getStatisticsByRegionReport(filter));
    }

    @Operation(summary = "Statistics By Activity Types")
    @GetMapping(value = "/statistics/by-activity-type")
    public ResponseEntity<List<StatisticsByActivityTypeReportDTO>> getStatisticsByActivityTypeReport(ActivityTypeReportFilter filter) {
        return ResponseEntity.ok(reportService.getStatisticsByActivityTypeReport(filter));
    }

    @Operation(summary = "Statistics By Region Report")
    @GetMapping(value = "/statistics/by-region")
    public ResponseEntity<List<StatisticsByRegionReportDTO>> getStatisticsByRegionReport(BaseReportFilter filter) {
        return getCustomerStatisticsByRegionReport(filter);
    }

    @Operation(summary = "Statistics By Branch Report")
    @GetMapping(value = "/statistics/by-branch")
    public ResponseEntity<List<StatisticsByBranchReportDTO>> getStatisticsByBranchReport(BaseReportFilter filter) {
        return ResponseEntity.ok(reportService.getStatisticsByBranchReport(filter));
    }

    @Operation(summary = "Statistics By Branch Report")
    @GetMapping(value = "/statistics/by-branch/export")
    public ResponseEntity<?> getStatisticsByBranchReportToExcel(BaseReportFilter filter) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", "StatisticsByBranchReport.csv"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(reportDeviceByBranchExcelHandler.generate(filter));
    }

    @Operation(summary = "Statistics By District Report")
    @GetMapping("/statistics/by-district")
    public ResponseEntity<List<StatisticsByDistrictReportDTO>> getStatisticsByDistrictReport(BaseReportFilter filter) {
        log.debug("REST request to get StatisticsByDistrictReportDTO: {}", filter);
        return ResponseEntity.ok(reportService.getStatisticsByDistrictReport(filter));
    }

    @Operation(summary = "Statistics By Employees")
    @GetMapping("/statistics/by-employee")
    public ResponseEntity<List<StatisticsByEmployeeReportDTO>> getStatisticsByEmployeeReport(EmployeeReportFilter filter) {
        log.debug("REST request to get StatisticsByDistrictReportDTO: {}", filter);
        return ResponseEntity.ok(reportService.getStatisticsByEmployeeReport(filter));
    }

    @GetMapping("/statistics/by-employee" + ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject EmployeeReportFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.EMPLOYEE_REPORT, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statistics/by-employee" + ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject EmployeeReportFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.EMPLOYEE_REPORT, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Statistics By Employees Export")
    @GetMapping("/statistics/by-employee/export")
    public ResponseEntity<Resource> exportStatisticsByEmployeeReport(EmployeeReportFilter filter) {
        log.debug("REST request to get StatisticsByDistrictReportDTO: {}", filter);
        Resource resource = reportService.exportStatisticsByEmployeeReport(filter);
        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Employees_statistics.xlsx")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .cacheControl(CacheControl.noCache())
                    .body(resource);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BadRequestException(localizationMessage.localize(EXPORT_FILE_FAILED));
        }
    }
}
