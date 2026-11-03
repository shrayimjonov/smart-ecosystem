package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.genericsettings.CompanySettingsCreateDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.CompanySettingsListDTO;
import uz.uzkassa.smartpossupply.service.CompanySettingsService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2024
 */
@RestController
@RequestMapping(ApiConstants.cabinetCompanySettingsRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompanySettingsResource {

    CompanySettingsService companySettingsService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CompanySettingsCreateDTO settingsCreateDTO) {
        settingsCreateDTO.setCompanyId(SecurityUtils.getCurrentCompanyId());
        companySettingsService.create(settingsCreateDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping
    public ResponseEntity<List<CompanySettingsListDTO>> getList() {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(companySettingsService.getList());
    }
}