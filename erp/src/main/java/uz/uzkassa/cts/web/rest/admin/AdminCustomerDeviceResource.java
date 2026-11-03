package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.customer.admin.CustomerDevicesByDeviceTypeDTO;
import uz.uzkassa.cts.dto.customerdevice.CustomerDeviceListAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceDetailAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CustomerDeviceService;
import uz.uzkassa.cts.service.ExportReportService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCustomerDeviceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCustomerDeviceResource {

    CustomerDeviceService customerDeviceService;
    ExportReportService exportReportService;

    @GetMapping
    public ResponseEntity<Page<CustomerDeviceListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(customerDeviceService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<DeviceDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerDeviceService.getForAdmin(id));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<DeviceStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(DeviceStatus.REGISTERED, DeviceStatus.TRANSFERRED));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<List<DeviceStatisticsAdminDTO>> getStatistics(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(customerDeviceService.getStatisticsForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CUSTOMER_DEVICE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CUSTOMER_DEVICE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{tin}/active-devices-by-tin")
    public ResponseEntity<List<CustomerDevicesByDeviceTypeDTO>> countActiveDevices(@PathVariable String tin,
                                                                                   @RequestParam(value = "pinfl",required = false) String pinfl) {
        return ResponseEntity.ok(customerDeviceService.getActiveDeviceCountByTin(tin, pinfl));
    }
}
