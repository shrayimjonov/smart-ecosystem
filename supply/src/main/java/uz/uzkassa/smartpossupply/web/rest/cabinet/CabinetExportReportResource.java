package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.report.ExportReportListDTO;
import uz.uzkassa.smartpossupply.enums.ReportStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.ExportReportService;
import uz.uzkassa.smartpossupply.service.FileUploadService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetExportReportRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetExportReportResource {

    ExportReportService exportReportService;
    FileUploadService fileUploadService;


    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<ExportReportListDTO>> getList(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(exportReportService.getList(filter));
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<ReportStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(ReportStatus.class));
    }

    @Operation(summary = "Download")
    @GetMapping(ApiConstants.download + "/{fileId}")
    public ResponseEntity<?> download(@PathVariable Long fileId) {
        return fileUploadService.download(fileId);
    }
}
