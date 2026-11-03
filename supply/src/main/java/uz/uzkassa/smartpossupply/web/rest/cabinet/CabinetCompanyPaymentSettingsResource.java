package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.marketplacesettings.CompanyPaymentSettingsDTO;
import uz.uzkassa.smartpossupply.enums.PaymentType;
import uz.uzkassa.smartpossupply.service.CompanyPaymentSettingsService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCompanyPaymentSettingsRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompanyPaymentSettingsResource {

    CompanyPaymentSettingsService companyPaymentSettingsService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CompanyPaymentSettingsDTO companyPaymentSettingsDTO) {
        return ResponseEntity.ok(companyPaymentSettingsService.create(companyPaymentSettingsDTO));
    }

    @GetMapping
    public ResponseEntity<CompanyPaymentSettingsDTO> get() {
        return ResponseEntity.ok(companyPaymentSettingsService.get());
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<PaymentType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(PaymentType.class));
    }
}