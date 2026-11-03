package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.domain.CustomerDevice;
import uz.uzkassa.cts.dto.device.CustomerBranchDeviceListDTO;
import uz.uzkassa.cts.dto.device.CustomerDeviceCheckDTO;
import uz.uzkassa.cts.dto.device.CustomerDeviceDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerDeviceListDTO;
import uz.uzkassa.cts.enums.DevicePaymentType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CustomerDeviceService;

import java.util.EnumSet;

/**
 * REST controller for managing {@link CustomerDevice}.
 */

@Slf4j
@RestController
@RequestMapping("/api/customer/devices")
@Tag(name = "Customer Device")
@RequiredArgsConstructor
@Deprecated
public class CustomerDeviceResource {

    private final CustomerDeviceService customerDeviceService;

    @Operation(summary = "Payment Types")
    @GetMapping("/payment-types")
    public ResponseEntity<EnumSet<DevicePaymentType>> getPaymentTypes() {
        return ResponseEntity.ok(EnumSet.of(DevicePaymentType.FULL_PAYMENT,
                DevicePaymentType.LEASING_PRIME, DevicePaymentType.REREGISTRATION,
                DevicePaymentType.A_PAY, DevicePaymentType.RENTAL_CDTI));
    }

    @Operation(summary = "Register")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody CustomerDeviceDTO customerDeviceDTO) {
        log.debug("REST request to add device to Customer: {}", customerDeviceDTO.getCustomerId());
        return ResponseEntity.ok(customerDeviceService.register(customerDeviceDTO));
    }

    @Operation(summary = "Get Paid Device Count")
    @GetMapping("/paid-count/{tin}")
    public ResponseEntity<CustomerDeviceCheckDTO> getPaidDeviceCount(@PathVariable String tin) {
        log.debug("REST request to Get Paid Device Count TIN : {}", tin);
        return ResponseEntity.ok(customerDeviceService.getPaidDeviceCount(tin));
    }

    @Operation(summary = "List")
    @GetMapping
    public ResponseEntity<Page<CustomerBranchDeviceListDTO>> list(BaseFilter filter) {
        log.debug("REST request to get customer devices: {}", filter);
        return ResponseEntity.ok(customerDeviceService.list(filter));
    }

    @Operation(summary = "Look Up")
    @GetMapping("/lookup")
    public ResponseEntity<Page<SerialNumberDTO>> lookUp(BaseFilter filter) {
        log.debug("REST request to get customer device items:{}", filter.getCustomerId());
        return ResponseEntity.ok(customerDeviceService.lookUp(filter));
    }

    @Operation(summary = "Delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete customer device: {}", id);
        customerDeviceService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Count List to Sync")
    @GetMapping("/sync/count")
    public ResponseEntity<Long> getListCountToSync() {
        log.debug("REST request to get all customer Device count");
        return ResponseEntity.ok(customerDeviceService.getListCountToSync());
    }

    @Operation(summary = "Get List to Sync")
    @GetMapping("/sync/list")
    public ResponseEntity<Page<SyncCustomerDeviceListDTO>> getListToSync(BaseFilter filter) {
        log.debug("REST request to get all customer Devices : {}", filter);
        return ResponseEntity.ok(customerDeviceService.getListToSync(filter));
    }
}
