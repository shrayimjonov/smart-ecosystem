package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.report.ContractReportDTO;
import uz.uzkassa.cts.dto.report.ContractReportListDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.ContractReportFilter;
import uz.uzkassa.cts.handlers.template.ContractReportTemplate;
import uz.uzkassa.cts.service.ContractReportService;
import uz.uzkassa.cts.service.ImportReportService;

@RestController
@RequestMapping("/api/report/contract")
@Tag(name = "Contract report")
@Slf4j
@RequiredArgsConstructor
public class ContractReportResource {

    private final ContractReportService contractReportService;
    private final ContractReportTemplate contractReportTemplate;
    private final ImportReportService importReportService;

    @Operation(summary = "Add")
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody ContractReportDTO contractReport) {
        return ResponseEntity.ok(contractReportService.create(contractReport));
    }

    @Operation(summary = "Get")
    @GetMapping("/{id}")
    public ResponseEntity<ContractReportDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contractReportService.get(id));
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<Long> update(@Valid @RequestBody ContractReportDTO contractReport) {
        return ResponseEntity.ok(contractReportService.update(contractReport));
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<ContractReportListDTO>> getAll(ContractReportFilter filter) {
        return ResponseEntity.ok(contractReportService.list(filter));
    }

    @Operation(summary = "Sync")
    @PostMapping(value = "/sync")
    public ResponseEntity<Boolean> sync() {
        contractReportService.sync();
        return ResponseEntity.ok(true);
    }

//    @Operation(summary = "Import")
//    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) {
//        if (Objects.isNull(file) || file.isEmpty())
//            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Загружаемый файл не должен быть нулевым!");
//
//        return ResponseEntity.ok(contractReportService.importExcel(file));
//    }

    @Operation(summary = "Export")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel(ContractReportFilter filter) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contract_report.xlsx\"")
                .body(contractReportService.exportExcel(filter));
    }

    @GetMapping(ApiConstants.downloadExcelTemplate)
    public ResponseEntity<Resource> getTemplate() {
        return contractReportTemplate.download();
    }

    @PostMapping(value = ApiConstants.importExcel, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importExcel(@RequestParam("file") MultipartFile file) {
        importReportService.create(file, EntityTypeEnum.CONTRACT_REPORT, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }
}
