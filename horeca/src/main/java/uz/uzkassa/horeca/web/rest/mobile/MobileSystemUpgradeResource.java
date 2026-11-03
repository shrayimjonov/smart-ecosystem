package uz.uzkassa.horeca.web.rest.mobile;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.SystemUpgradeDetailDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.filters.SystemUpgradeFilter;
import uz.uzkassa.horeca.service.SystemUpgradeService;

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
        return ResponseEntity.ok(systemUpgradeService.getAvailableVersions(filter));
    }
}
