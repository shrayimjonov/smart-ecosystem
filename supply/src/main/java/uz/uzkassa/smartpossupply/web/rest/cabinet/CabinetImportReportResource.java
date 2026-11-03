package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.importreport.ImportReportListDTO;
import uz.uzkassa.smartpossupply.enums.ReportStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.ImportReportService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.03.2023 15:46
 */
@RestController
@RequestMapping(ApiConstants.cabinetImportReportRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetImportReportResource {

    ImportReportService importReportService;


    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<ImportReportListDTO>> getList(@ParameterObject BaseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(importReportService.getList(filter));
    }

    @Operation(summary = "Download")
    @GetMapping(ApiConstants.id)
    public ResponseEntity<Resource> downloadErrorFile(@PathVariable Long id) {
        return importReportService.downloadErrorFile(id);
    }

    @Operation(summary = ApiConstants.Statuses)
    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<ReportStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(ReportStatus.class));
    }
}
