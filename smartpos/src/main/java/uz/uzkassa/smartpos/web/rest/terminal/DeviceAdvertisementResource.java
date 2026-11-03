package uz.uzkassa.smartpos.web.rest.terminal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementDeviceListDto;
import uz.uzkassa.smartpos.service.AdvertisementService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/terminals/ads")
@RequiredArgsConstructor
public class DeviceAdvertisementResource {
    private final AdvertisementService advertisementService;

    @GetMapping
    public List<AdvertisementDeviceListDto> getAdsForDevice(@RequestHeader("TERMINAL_SN") String deviceSN) {
        return advertisementService.getAdsForDevice(deviceSN);
    }
}
