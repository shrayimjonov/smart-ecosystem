package uz.uzkassa.smartpos.web.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.device.DeviceDto;
import uz.uzkassa.smartpos.filter.DeviceFilter;
import uz.uzkassa.smartpos.service.DeviceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/devices")
@RequiredArgsConstructor
public class AdminDevicesResource {
    private final DeviceService deviceService;

    @GetMapping
    public Page<DeviceDto> getDevices(@ParameterObject DeviceFilter filter) {
        return deviceService.findAllByFilter(filter);
    }

    @GetMapping("/lookup")
    public List<CommonDTO> deviceLookup(@ParameterObject DeviceFilter filter) {
        return deviceService.lookup(filter);
    }
}
