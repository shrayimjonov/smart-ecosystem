package uz.uzkassa.smartpos.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.company.DevicesByTypeDto;
import uz.uzkassa.smartpos.service.DeviceService;

@RestController
@RequestMapping("/api/v1/cabinet/branches")
@RequiredArgsConstructor
public class CabinetBranchResource {
    private final DeviceService deviceService;

    @GetMapping("/devices")
    public DevicesByTypeDto getBranchDevices() {
        return deviceService.getBranchDevices();
    }
}
