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
import uz.uzkassa.cts.dto.device.DeviceDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceDetailAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceListAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.DeviceService;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.ImportReportService;
import uz.uzkassa.cts.utils.RandomUtils;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminDeviceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminDeviceResource {

    DeviceService deviceService;
    ExportReportService exportReportService;
    ImportReportService importReportService;

    @GetMapping(value = "/generate-device-serial")
    public ResponseEntity<String> generateDeviceSerial() {
        return ResponseEntity.ok(RandomUtils.generateVirtualDeviceSerial());
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody DeviceDTO deviceDTO) {
        return ResponseEntity.ok(deviceService.create(deviceDTO));
    }

    @GetMapping
    public ResponseEntity<Page<DeviceListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(deviceService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<DeviceDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getForAdmin(id));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<DeviceStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(DeviceStatus.NOT_REGISTERED, DeviceStatus.REGISTERED, DeviceStatus.UNREGISTERED));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<List<DeviceStatisticsAdminDTO>> getStatistics(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(deviceService.getStatisticsForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.DEVICE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.DEVICE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = ApiConstants.importExcel, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importExcel(@RequestParam("file") MultipartFile file) {
        importReportService.create(file, EntityTypeEnum.DEVICE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }
}
