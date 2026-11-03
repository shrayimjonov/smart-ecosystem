package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.companysettings.CompanySettingsDTO;
import uz.uzkassa.horeca.dto.companysettings.CompanySettingsDetailDTO;
import uz.uzkassa.horeca.service.CompanySettingsService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCompanySettingsRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompanySettingsResource {

    CompanySettingsService companySettingsService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CompanySettingsDTO companySettingsDTO) {
        return ResponseEntity.ok(companySettingsService.create(companySettingsDTO));
    }

    @GetMapping
    public ResponseEntity<CompanySettingsDetailDTO> get() {
        return ResponseEntity.ok(companySettingsService.get());
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CompanySettingsDTO companySettingsDTO) {
        return ResponseEntity.ok(companySettingsService.update(id, companySettingsDTO));
    }
}
