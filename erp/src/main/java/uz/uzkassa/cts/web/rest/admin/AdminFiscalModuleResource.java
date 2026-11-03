package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleListAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.FiscalModuleService;
import uz.uzkassa.cts.service.ImportReportService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminFiscalModuleRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminFiscalModuleResource {

    FiscalModuleService fiscalModuleService;
    ExportReportService exportReportService;
    ImportReportService importReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody FiscalModuleDTO fiscalModuleDTO) {
        return ResponseEntity.ok(fiscalModuleService.create(fiscalModuleDTO));
    }

    @GetMapping
    public ResponseEntity<Page<FiscalModuleListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(fiscalModuleService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<FiscalModuleDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(fiscalModuleService.getForAdmin(id));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<DeviceStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(DeviceStatus.NOT_REGISTERED, DeviceStatus.REGISTERED, DeviceStatus.UNREGISTERED));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<List<DeviceStatisticsAdminDTO>> getStatistics(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(fiscalModuleService.getStatisticsForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.FISCAL_MODULE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.FISCAL_MODULE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = ApiConstants.importExcel, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importExcel(@RequestParam("file") MultipartFile file) {
        importReportService.create(file, EntityTypeEnum.FISCAL_MODULE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

}
