package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectLastReceiptRequestDTO;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectOperatorStatusRequestDto;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectStatusRequestDto;
import uz.uzkassa.cts.dto.humoconnect.response.HumoConnectResponseDto;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.HumoConnectFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.HumoConnectService;

import java.util.Objects;

@RestController
@RequestMapping("/api/humo-connect")
@Tag(name = "Humo connection")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HumoConnectResource {

    HumoConnectService humoConnectService;
    ExportReportService exportReportService;

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<HumoConnectResponseDto>> list(@ParameterObject HumoConnectFilter filter) {
        return ResponseEntity.ok(humoConnectService.list(filter));
    }

    @Operation(summary = "Update Status")
    @PatchMapping("/status")
    public ResponseEntity<Void> updateStatus(@Valid @RequestBody HumoConnectStatusRequestDto dto) {
        humoConnectService.updateStatus(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update Status")
    @PatchMapping("/operator-status")
    public ResponseEntity<Void> updateOperatorStatus(@Valid @RequestBody HumoConnectOperatorStatusRequestDto dto) {
        humoConnectService.updateOperatorStatus(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update Last Receipt")
    @PatchMapping("/last-receipt")
    public ResponseEntity<Void> updateLastReceipt(HumoConnectLastReceiptRequestDTO dto) {
        humoConnectService.updateLastReceipt(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Doc flow status")
    @PatchMapping(value = "/doc-flow-status/{id}/{status}")
    public ResponseEntity<Long> scanner(@PathVariable("id") Long id,
                                        @PathVariable("status") String docFlowStatus) {
        humoConnectService.changeDocFlowStatus(id, docFlowStatus);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject HumoConnectFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.HUMO_CONNECT, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject HumoConnectFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.HUMO_CONNECT, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "List")
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@ParameterObject HumoConnectFilter filter) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"humoConnect.xlsx\"")
                .body(humoConnectService.download(filter));
    }

    @Operation(summary = "License by import excel")
    @PostMapping(value = "/license", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> changeLicense(@RequestParam("file") MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Загружаемый файл не должен быть нулевым!");
        humoConnectService.changeLicenseByImportExcel(file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Doc flow status")
    @PostMapping(value = "/doc-flow-status/{status}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> changeDocFlowStatus(@RequestParam("file") MultipartFile file,
                                                 @PathVariable("status") String docFlowStatus) {
        if (Objects.isNull(file) || file.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Загружаемый файл не должен быть нулевым!");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"controversialHumoConnects.xlsx\"")
                .body(humoConnectService.changeDocFlowStatusByExcel(file, docFlowStatus));
    }
}
