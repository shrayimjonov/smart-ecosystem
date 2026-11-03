package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceChangePaymentTypeDto;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceChangeTariffDto;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceListDto;
import uz.uzkassa.cts.service.CustomerBranchDeviceService;

import java.util.List;

@RestController
@RequestMapping("/api/customerBranchDevicesChangeTariff")
@Tag(name = "Customer Branch Devices removal")
@Slf4j
public class CustomerBranchDeviceChangeTariff {

    private final CustomerBranchDeviceService customerBranchDeviceService;

    public CustomerBranchDeviceChangeTariff(CustomerBranchDeviceService customerBranchDeviceService) {
        this.customerBranchDeviceService = customerBranchDeviceService;
    }

    @Operation(summary ="Select all customer devices")
    @GetMapping("/customer/{id}")
    public ResponseEntity<List<CustomerBranchDeviceListDto>> getAllDevices(@PathVariable Long id) {
        log.debug("REST request get customer devices by id: {}", id);
        List<CustomerBranchDeviceListDto> list = customerBranchDeviceService.getCustomerBranchDevicesByCustomerId(id);
        return ResponseEntity.ok(list);
    }


    @Operation(summary ="Chang device status to allow in service")
    @PostMapping
    public ResponseEntity<CustomerBranchDeviceListDto> changeTariff(@Valid @RequestBody CustomerBranchDeviceChangeTariffDto dto) {
        log.debug("REST request get customer devices by id: {}", dto.getCustomerBranchDeviceId());
        CustomerBranchDeviceListDto list = customerBranchDeviceService.changeServiceAllowToTrue(dto.getCustomerBranchDeviceId());
        return ResponseEntity.ok(list);
    }

    @Operation(summary ="Chang device status to allow in service")
    @PostMapping("/change-payment-type")
    public ResponseEntity<CustomerBranchDeviceListDto> changeDevicePaymentType(@Valid @RequestBody CustomerBranchDeviceChangePaymentTypeDto dto) {
        CustomerBranchDeviceListDto list = customerBranchDeviceService.changeDevicePaymentType(dto);
        return ResponseEntity.ok(list);
    }
}
