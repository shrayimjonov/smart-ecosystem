package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.smartpos.SmartposFiscalizationDetailDTO;
import uz.uzkassa.cts.dto.smartpos.company.CustomerCtoDTO;
import uz.uzkassa.cts.filter.DeviceModelFilter;
import uz.uzkassa.cts.service.SmartposService;


@RestController
@RequestMapping("/api/smartpos")
@Tag(name = "Smartpos")
@Slf4j
@RequiredArgsConstructor
public class SmartposResource {

    private final SmartposService smartposService;

    @Operation(summary = "Get Fiscalization Detail")
    @GetMapping("/fiscalization/detail")
    public ResponseEntity<SmartposFiscalizationDetailDTO> getFiscalizationDetail(DeviceModelFilter deviceModelFilter) {
        log.debug("REST request to get Device Data : {}", deviceModelFilter);
        return ResponseEntity.ok(smartposService.getFiscalizationDetail(deviceModelFilter));
    }

    @Operation(summary = "Update Customer")
    @PutMapping("/customer/{customerId}/{customerBranchId}")
    public ResponseEntity<SmartposFiscalizationDetailDTO> updateCustomer(@PathVariable(value = "customerId") Long customerId,
                                                                         @PathVariable(value = "customerBranchId") Long customerBranchId) {
        log.debug("REST request to update Customer Data : {}", customerId);
        return ResponseEntity.ok(smartposService.updateCustomer(customerId, customerBranchId));
    }

    @Operation(summary = "Get Customer Details")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomerCtoDTO> getCustomerDetails(@PathVariable(value = "customerId") Long customerId) {
        log.debug("REST request to get company data from Smartpos : {}", customerId);
        return ResponseEntity.ok(smartposService.getCustomerDetails(customerId));
    }

    @Operation(summary = "Get Device Status")
    @GetMapping("/device/{serialNumber}")
    public ResponseEntity<Boolean> getDeviceStatus(@PathVariable(value = "serialNumber") String serialNumber) {
        return ResponseEntity.ok(smartposService.getDeviceStatusBySerialNumber(serialNumber));
    }
}
