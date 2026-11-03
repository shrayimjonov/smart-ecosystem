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
import uz.uzkassa.cts.service.BranchFiscalModuleService;

/**
 * REST controller for managing {@link uz.uzkassa.cts.domain.FiscalModule}.
 */
@RestController
@RequestMapping("/api/branch/fiscal/modules")
@Tag(name = "Branch Fiscal Modules")
@Slf4j
@Deprecated
public class BranchFiscalModuleResource {

    private final BranchFiscalModuleService branchFiscalModuleService;

    public BranchFiscalModuleResource(BranchFiscalModuleService branchFiscalModuleService) {
        this.branchFiscalModuleService = branchFiscalModuleService;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<BaseDeviceFiscalModuleListDTO>> list(BaseFilter filter) {
        log.debug("REST request to get branch fiscal module BaseDeviceFiscalModuleListDTO: {}", filter);
        return ResponseEntity.ok(branchFiscalModuleService.list(filter));
    }

    @Operation(summary = "List By Employee")
    @GetMapping("/by-employee")
    public ResponseEntity<Page<BaseDeviceFiscalModuleListDTO>> listByEmployee(BaseFilter filter) {
        log.debug("REST request to get all branch fiscal modules BaseDeviceFiscalModuleListDTO : {}", filter);
        return ResponseEntity.ok(branchFiscalModuleService.listByEmployee(filter));
    }

    @Operation(summary = "List By Branch")
    @GetMapping("/by-branch")
    public ResponseEntity<Page<BaseDeviceFiscalModuleListDTO>> listByBranch(BaseFilter filter) {
        log.debug("REST request to get all branch fiscal modules BaseDeviceFiscalModuleListDTO : {}", filter);
        return ResponseEntity.ok(branchFiscalModuleService.listByEmployee(filter));
    }

    @Operation(summary ="List Terminals")
    @GetMapping("/all")
    public ResponseEntity<Page<CompanyTerminalListDTO>> listAll(CompanyBranchDeviceFilter filter) {
        log.debug("REST request to get all branch devices");
        return ResponseEntity.ok(branchFiscalModuleService.listAll(filter));
    }
}
