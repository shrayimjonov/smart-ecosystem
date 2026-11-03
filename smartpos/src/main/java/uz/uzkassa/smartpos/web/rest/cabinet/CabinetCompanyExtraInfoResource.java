package uz.uzkassa.smartpos.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.company.CompanyExtraInfoDTO;
import uz.uzkassa.smartpos.dto.company.DevicesByTypeDto;
import uz.uzkassa.smartpos.enums.DeviceType;
import uz.uzkassa.smartpos.service.CompanyExtraInfoService;
import uz.uzkassa.smartpos.service.DeviceService;
import uz.uzkassa.smartpos.service.ProductService;

@RestController
@RequestMapping("/api/v1/cabinet/companies")
@RequiredArgsConstructor
public class CabinetCompanyExtraInfoResource {
    private final CompanyExtraInfoService companyExtraInfoService;
    private final ProductService productService;
    private final DeviceService deviceService;

    @Deprecated
    @GetMapping("/excise-status")
    public ResponseEntity<CompanyExtraInfoDTO> getInfoAboutExciseDisableOrNot() {
        return companyExtraInfoService.getExtraInfo();
    }

    @GetMapping("/extra-info")
    public ResponseEntity<CompanyExtraInfoDTO> getCompanyExtraInfo() {
        return companyExtraInfoService.getExtraInfo();
    }

    @PutMapping("/excise-disable")
    public ResponseEntity<Boolean> makeCompanyExciseVisible(@RequestParam Boolean exciseEnable) {
        productService.updateProductHasExcise(exciseEnable);
        companyExtraInfoService.saveCompanyExtraInfo(exciseEnable);
        return ResponseEntity.ok(Boolean.TRUE);
    }

    @PutMapping("/marking-check/toggle")
    public void toggleMarkingCheck(@RequestParam boolean markingCheckEnabled) {
        companyExtraInfoService.toggleMarkingCheck(markingCheckEnabled);
    }

    @GetMapping("/devices")
    public DevicesByTypeDto getCompanyDevices() {
        return deviceService.getCompanyDevices();
    }

    @GetMapping("/devices/types")
    public DeviceType[] getDeviceTypes() {
        return DeviceType.values();
    }
}
