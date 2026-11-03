package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.domain.CompanyDevice;
import uz.uzkassa.cts.dto.device.CompanyDeviceListNewDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceListFilter;
import uz.uzkassa.cts.filter.SyncDeviceFilter;
import uz.uzkassa.cts.service.CompanyDeviceService;
import uz.uzkassa.cts.service.ExportReportService;

/**
 * REST controller for managing {@link CompanyDevice}.
 */
@RestController
@RequestMapping("/api/company/devices")
@Tag(name = "Company Devices")
@Slf4j
@Deprecated
public class CompanyDeviceResource {

    private final CompanyDeviceService companyDeviceService;
    private final ExportReportService exportReportService;

    public CompanyDeviceResource(CompanyDeviceService companyDeviceService, ExportReportService exportReportService) {
        this.companyDeviceService = companyDeviceService;
        this.exportReportService = exportReportService;
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<CompanyDeviceListNewDTO>> list(CompanyDeviceListFilter filter) {
        log.debug("REST request to get company devices");
        return ResponseEntity.ok(companyDeviceService.list(filter));
    }

    @Operation(summary = "List Terminals")
    @GetMapping("/all")
    public ResponseEntity<Page<CompanyTerminalListDTO>> listTerminals(CompanyBranchDeviceFilter filter) {
        log.debug("REST request to get company all devices");
        return ResponseEntity.ok(companyDeviceService.listAll(filter));
    }

    @Operation(summary = "Sync")
    @GetMapping("/sync")
    public ResponseEntity<Void> sync(@Valid SyncDeviceFilter syncDeviceFilter) {
        log.debug("REST request to sync all devices by model");
        companyDeviceService.sync(syncDeviceFilter);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject CompanyDeviceListFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.OLD_COMPANY_DEVICE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }
}
