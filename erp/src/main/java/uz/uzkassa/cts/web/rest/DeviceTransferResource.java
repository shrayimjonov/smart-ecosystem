package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.domain.BranchDevice;
import uz.uzkassa.cts.dto.device.DeviceForTransferListDTO;
import uz.uzkassa.cts.dto.device.DeviceTransferDTO;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;
import uz.uzkassa.cts.service.CompanyDeviceService;

/**
 * REST controller for managing {@link BranchDevice}.
 */
@RestController
@RequestMapping("/api/device/transfers")
@Tag(name = "Device Transfers")
@Slf4j
public class DeviceTransferResource {

    private final CompanyDeviceService companyDeviceService;

    public DeviceTransferResource(CompanyDeviceService companyDeviceService) {
        this.companyDeviceService = companyDeviceService;
    }

    @Operation(summary = "Transfer")
    @PostMapping
    public ResponseEntity<Void> transfer(@Valid @RequestBody DeviceTransferDTO deviceTransferDTO) {
        log.debug("REST request to Transfer new DeviceTransferDTO: {}", deviceTransferDTO.getToBranchId());
        companyDeviceService.transfer(deviceTransferDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<DeviceForTransferListDTO>> list(CompanyDeviceFilter filter) {
        log.debug("REST request to all devices: {}", filter);
        return ResponseEntity.ok(companyDeviceService.listForTransfer(filter));
    }
}
