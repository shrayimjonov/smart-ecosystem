package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.SystemUpgradeDTO;
import uz.uzkassa.horeca.dto.SystemUpgradeDetailDTO;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.branch.BranchDTO;
import uz.uzkassa.horeca.dto.branch.cabinet.BranchDetailCabinetDTO;
import uz.uzkassa.horeca.dto.branch.cabinet.BranchListCabinetDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.filters.SystemUpgradeFilter;
import uz.uzkassa.horeca.service.SystemUpgradeService;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetSystemUpgradeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetSystemUpgradeResource {

    SystemUpgradeService systemUpgradeService;

    @GetMapping("/available-versions")
    public ResponseEntity<List<SystemUpgradeDetailDTO>> getAvailableVersions(@ParameterObject SystemUpgradeFilter filter) {
        return ResponseEntity.ok(systemUpgradeService.getAvailableVersions(filter));
    }
}
