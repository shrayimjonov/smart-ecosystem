package uz.uzkassa.cts.web.rest.admin;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.fm.FiscalModuleReportListDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleReportRequestDTO;
import uz.uzkassa.cts.enums.ArrivalStatus;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.filter.FiscalModuleReportFilter;
import uz.uzkassa.cts.handlers.template.FiscalModuleReportTemplate;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.FiscalModuleReportService;
import uz.uzkassa.cts.service.ImportReportService;

@RestController
@RequestMapping(ApiConstants.adminFiscalModuleReportRootApi)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AdminFiscalModuleReportResource {

    FiscalModuleReportService fiscalModuleReportService;
    ExportReportService exportReportService;
    FiscalModuleReportTemplate fiscalModuleReportTemplate;
    ImportReportService importReportService;

    @GetMapping
    public ResponseEntity<Page<FiscalModuleReportListDTO>> getList(@ParameterObject FiscalModuleReportFilter filter) {
        return ResponseEntity.ok(fiscalModuleReportService.getList(filter));
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody FiscalModuleReportRequestDTO fiscalModuleReportRequestDTO) {
        return ResponseEntity.ok(fiscalModuleReportService.create(fiscalModuleReportRequestDTO));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Long> updateStatus(@PathVariable Long id,
                                             @RequestParam(value = "arrivalStatus", required = false) ArrivalStatus arrivalStatus,
                                             @RequestParam(value = "reason", required = false) FMReportReason reason) {
        return ResponseEntity.ok(fiscalModuleReportService.updateStatus(id, arrivalStatus, reason));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fiscalModuleReportService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject FiscalModuleReportFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.FM_REPORT, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject FiscalModuleReportFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.FM_REPORT, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.importExcel + "/fiscal-modules")
    public ResponseEntity<byte[]> importFiscalModulesFromExcel(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"unchecked_serialNumbers.xlsx\"")
                .body(fiscalModuleReportService.importFiscalModulesFromExcel(file));
    }

    @PostMapping(value = ApiConstants.importExcel, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importFromExcel(@RequestPart("file") MultipartFile file) {
        importReportService.create(file, EntityTypeEnum.FISCAL_MODULE_REPORT, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.downloadExcelTemplate)
    public ResponseEntity<Resource> getTemplate() {
        return fiscalModuleReportTemplate.download();
    }
}
