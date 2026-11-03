package uz.uzkassa.smartpos.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.device.DeviceDto;
import uz.uzkassa.smartpos.filter.DeviceFilter;
import uz.uzkassa.smartpos.service.DeviceService;

@RestController
@RequestMapping("/api/v1/cabinet/devices")
@RequiredArgsConstructor
public class CabinetDevicesResource {
    private final DeviceService deviceService;

    @GetMapping
    public Page<DeviceDto> getDevices(@ParameterObject DeviceFilter filter) {
        return deviceService.findAllByFilter(filter);
    }
}
