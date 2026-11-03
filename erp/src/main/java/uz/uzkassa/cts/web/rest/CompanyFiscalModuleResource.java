package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleListDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.service.CompanyFiscalModuleService;

/**
 * REST controller for managing {@link uz.uzkassa.cts.domain.FiscalModule}.
 */
@RestController
@RequestMapping("/api/company/fiscal/modules")
@Tag(name = "Company Fiscal Modules")
@Slf4j
@Deprecated
public class CompanyFiscalModuleResource {

    private final CompanyFiscalModuleService companyFiscalModuleService;

    public CompanyFiscalModuleResource(CompanyFiscalModuleService companyFiscalModuleService) {
        this.companyFiscalModuleService = companyFiscalModuleService;
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<BaseDeviceFiscalModuleListDTO>> list(BaseFilter filter) {
        log.debug("REST request to get all FiscalModuleListDTO: {}", filter);
        return ResponseEntity.ok(companyFiscalModuleService.list(filter));
    }

    @Operation(summary = "List Fiscal Modules")
    @GetMapping("/all")
    public ResponseEntity<Page<CompanyTerminalListDTO>> listAll(CompanyBranchDeviceFilter filter) {
        log.debug("REST request to get company all fiscal modules {}", filter);
        return ResponseEntity.ok(companyFiscalModuleService.listAll(filter));
    }

}
