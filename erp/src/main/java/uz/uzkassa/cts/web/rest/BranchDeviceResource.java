package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.domain.BranchDevice;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleListDTO;
import uz.uzkassa.cts.dto.device.BranchDeviceDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.sync.SyncBranchDeviceListDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.service.BranchDeviceService;

/**
 * REST controller for managing {@link BranchDevice}.
 */
@RestController
@RequestMapping("/api/branch/devices")
@Tag(name = "Branch Device")
@Slf4j
@Deprecated
public class BranchDeviceResource {

    private final BranchDeviceService branchDeviceService;

    public BranchDeviceResource(BranchDeviceService branchDeviceService) {
        this.branchDeviceService = branchDeviceService;
    }

    @Operation(summary ="Add")
    @PostMapping
    public ResponseEntity<Void> add(@Valid @RequestBody BranchDeviceDTO branchDeviceDTO) {
        log.debug("REST request to add device to Branch: {}", branchDeviceDTO.getBranchId());
        branchDeviceService.add(branchDeviceDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Look Up")
    @GetMapping("/lookup")
    public ResponseEntity<Page<SerialNumberDTO>> lookUp(@Valid BaseFilter filter) {
        log.debug("REST request to get branch device items: {}", filter);
        return ResponseEntity.ok(branchDeviceService.lookUp(filter));
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<BaseDeviceFiscalModuleListDTO>> list(BaseFilter filter) {
        log.debug("REST request to all devices");
        return ResponseEntity.ok(branchDeviceService.list(filter));
    }

    @Operation(summary = "List By Employee")
    @GetMapping("/by-employee")
    public ResponseEntity<Page<BaseDeviceFiscalModuleListDTO>> listByEmployee(BaseFilter filter) {
        log.debug("REST request to get all branch devices BranchDeviceListDTO : {}", filter);
        return ResponseEntity.ok(branchDeviceService.listByEmployee(filter));
    }

    @Operation(summary = "List By Branch")
    @GetMapping("/by-branch")
    public ResponseEntity<Page<BaseDeviceFiscalModuleListDTO>> listByBranch(BaseFilter filter) {
        log.debug("REST request to get all branch devices BranchDeviceListDTO : {}", filter);
        return ResponseEntity.ok(branchDeviceService.listByEmployee(filter));
    }

    @Operation(summary ="List Terminals")
    @GetMapping("/all")
    public ResponseEntity<Page<CompanyTerminalListDTO>> listAll(CompanyBranchDeviceFilter filter) {
        log.debug("REST request to get branch all devices");
        return ResponseEntity.ok(branchDeviceService.listAll(filter));
    }

    @Operation(summary = "Get Count List to Sync")
    @GetMapping("/sync/count")
    public ResponseEntity<Long> getListCountToSync() {
        log.debug("REST request to get all branch Device count");
        return ResponseEntity.ok(branchDeviceService.getListCountToSync());
    }

    @Operation(summary = "Get List to Sync")
    @GetMapping("/sync/list")
    public ResponseEntity<Page<SyncBranchDeviceListDTO>> getListToSync(BaseFilter filter) {
        log.debug("REST request to get all branch Devices : {}", filter);
        return ResponseEntity.ok(branchDeviceService.getListToSync(filter));
    }
}
