package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.CustomerBranchFiscalModuleListAdminDTO;
import uz.uzkassa.cts.dto.fm.admin.FiscalModuleDetailAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CustomerBranchFiscalModuleService;
import uz.uzkassa.cts.service.ExportReportService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCustomerBranchFiscalModuleRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCustomerBranchFiscalModuleResource {

    CustomerBranchFiscalModuleService customerBranchFiscalModuleService;
    ExportReportService exportReportService;

    @GetMapping
    public ResponseEntity<Page<CustomerBranchFiscalModuleListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(customerBranchFiscalModuleService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<FiscalModuleDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerBranchFiscalModuleService.getForAdmin(id));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<DeviceStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.of(DeviceStatus.REGISTERED, DeviceStatus.FISCAL, DeviceStatus.FISCAL_ACTIVE));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<List<DeviceStatisticsAdminDTO>> getStatistics(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(customerBranchFiscalModuleService.getStatisticsForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CUSTOMER_BRANCH_FISCAL_MODULE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.CUSTOMER_BRANCH_FISCAL_MODULE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
