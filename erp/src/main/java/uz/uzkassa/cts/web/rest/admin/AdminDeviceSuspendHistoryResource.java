package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.devicesuspendhistory.admin.DeviceSuspendHistoryListAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.DeviceSuspendHistoryFilter;
import uz.uzkassa.cts.service.DeviceSuspendHistoryService;
import uz.uzkassa.cts.service.ExportReportService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminDeviceSuspendHistoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminDeviceSuspendHistoryResource {

    DeviceSuspendHistoryService deviceSuspendHistoryService;
    ExportReportService exportReportService;

    @GetMapping
    public ResponseEntity<Page<DeviceSuspendHistoryListAdminDTO>> getList(@ParameterObject DeviceSuspendHistoryFilter filter) {
        return ResponseEntity.ok(deviceSuspendHistoryService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject DeviceSuspendHistoryFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.DEVICE_SUSPEND_HISTORY, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject DeviceSuspendHistoryFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.DEVICE_SUSPEND_HISTORY, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
