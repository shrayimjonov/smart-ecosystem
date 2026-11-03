package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
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
import uz.uzkassa.smartpossupply.dto.marketplacesettings.CompanyDeliverySettingsDTO;
import uz.uzkassa.smartpossupply.dto.marketplacesettings.CompanyDeliverySettingsDetailDTO;
import uz.uzkassa.smartpossupply.enums.DeliveryType;
import uz.uzkassa.smartpossupply.service.CompanyDeliverySettingsService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCompanyDeliverySettingsRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompanyDeliverySettingsResource {

    CompanyDeliverySettingsService companyDeliverySettingsService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CompanyDeliverySettingsDTO companyDeliverySettingsDTO) {
        return ResponseEntity.ok(companyDeliverySettingsService.create(companyDeliverySettingsDTO));
    }

    @GetMapping
    public ResponseEntity<CompanyDeliverySettingsDetailDTO> get(@RequestParam(value = "type", required = false) String type) {
        return ResponseEntity.ok(companyDeliverySettingsService.get(DeliveryType.get(type)));
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<DeliveryType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(DeliveryType.class));
    }
}