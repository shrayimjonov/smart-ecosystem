package uz.uzkassa.smartpossupply.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDetailDTO;
import uz.uzkassa.smartpossupply.service.CompanyBankService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCompanyBankRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompanyBankResource {

    CompanyBankService companyBankService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CompanyBankDTO companyBankDTO) {
        return ResponseEntity.ok(companyBankService.create(companyBankDTO));
    }

    @GetMapping
    public ResponseEntity<CompanyBankDetailDTO> get() {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new CompanyBankDetailDTO());
        }
        return ResponseEntity.ok(companyBankService.get(SecurityUtils.getCurrentCompanyId()));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody CompanyBankDTO companyBankDTO) {
        return ResponseEntity.ok(companyBankService.update(id, companyBankDTO));
    }
}
