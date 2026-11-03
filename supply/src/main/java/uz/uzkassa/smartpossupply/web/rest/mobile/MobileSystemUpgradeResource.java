package uz.uzkassa.smartpossupply.web.rest.mobile;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.systemupgrade.SystemUpgradeDetailDTO;
import uz.uzkassa.smartpossupply.enums.PlatformType;
import uz.uzkassa.smartpossupply.filters.SystemUpgradeFilter;
import uz.uzkassa.smartpossupply.service.SystemUpgradeService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 04.10.2024
 */
@RestController
@RequestMapping(ApiConstants.mobileSystemUpgradeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MobileSystemUpgradeResource {

    SystemUpgradeService systemUpgradeService;

    @GetMapping("/available-versions")
    public ResponseEntity<List<SystemUpgradeDetailDTO>> getAvailableVersions(@ParameterObject SystemUpgradeFilter filter) {
        filter.setAvailable(true);
        if (StringUtils.isEmpty(filter.getPlatformType())) {
            filter.setPlatformType(PlatformType.ANDROID.name());
        }
        return ResponseEntity.ok(systemUpgradeService.getAvailableVersions(filter));
    }
}
