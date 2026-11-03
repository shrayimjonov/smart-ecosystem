package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.exportimportreport.ImportReportListDTO;
import uz.uzkassa.cts.enums.ExportReportStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.ImportReportService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminImportReportRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminImportReportResource {

    ImportReportService importReportService;
    FileUploadService fileUploadService;

    @GetMapping
    public ResponseEntity<Page<ImportReportListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(importReportService.getList(filter));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<ExportReportStatus>> getStatuses() {
        return ResponseEntity.ok().body(EnumSet.allOf(ExportReportStatus.class));
    }

    @GetMapping(ApiConstants.download + "/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) {
        return fileUploadService.download(fileId);
    }
}
