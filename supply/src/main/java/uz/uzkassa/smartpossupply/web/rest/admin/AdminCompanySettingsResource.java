package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.genericsettings.CompanySettingsCreateDTO;
import uz.uzkassa.smartpossupply.dto.genericsettings.CompanySettingsListDTO;
import uz.uzkassa.smartpossupply.service.CompanySettingsService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.02.2024
 */
@RestController
@RequestMapping(ApiConstants.adminCompanySettingsRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCompanySettingsResource {

    CompanySettingsService companySettingsService;

    @GetMapping
    public ResponseEntity<List<CompanySettingsListDTO>> getList(@RequestParam Long companyId) {
        return ResponseEntity.ok(companySettingsService.getListForAdmin(companyId));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CompanySettingsCreateDTO settingsCreateDTO) {
        companySettingsService.create(settingsCreateDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.sync)
    public ResponseEntity<Void> sync() {
        companySettingsService.sync();
        return ResponseEntity.ok().build();
    }
}
