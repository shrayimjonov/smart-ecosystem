package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceChangeTariffDto;
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceListDto;
import uz.uzkassa.cts.service.CustomerBranchDeviceService;

@RestController
@RequestMapping("/api/device-suspend")
@Tag(name = "Customer Branch Devices suspend")
@Slf4j
public class DeviceSuspendResource {

    private final CustomerBranchDeviceService customerBranchDeviceService;

    public DeviceSuspendResource(CustomerBranchDeviceService customerBranchDeviceService) {
        this.customerBranchDeviceService = customerBranchDeviceService;
    }


    @Operation(summary ="Chang device status to suspend")
    @PostMapping("/suspend")
    public ResponseEntity<CustomerBranchDeviceListDto> suspend(@Valid @RequestBody CustomerBranchDeviceChangeTariffDto dto) {
        log.debug("REST request get customer devices by id: {}", dto.getCustomerBranchDeviceId());
        CustomerBranchDeviceListDto list = customerBranchDeviceService.suspend(dto.getCustomerBranchDeviceId());
        return ResponseEntity.ok(list);
    }

    @Operation(summary ="Chang device status to active")
    @PostMapping("/activate")
    public ResponseEntity<CustomerBranchDeviceListDto> activate(@Valid @RequestBody CustomerBranchDeviceChangeTariffDto dto) {
        log.debug("REST request get customer devices by id: {}", dto.getCustomerBranchDeviceId());
        CustomerBranchDeviceListDto list = customerBranchDeviceService.activate(dto.getCustomerBranchDeviceId());
        return ResponseEntity.ok(list);
    }

}
