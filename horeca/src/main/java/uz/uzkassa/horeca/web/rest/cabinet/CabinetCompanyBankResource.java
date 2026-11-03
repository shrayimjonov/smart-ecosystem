package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.companybank.CompanyBankDTO;
import uz.uzkassa.horeca.dto.companybank.CompanyBankDetailDTO;
import uz.uzkassa.horeca.service.CompanyBankService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCompanyBankRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompanyBankResource {

    CompanyBankService companyBankService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CompanyBankDTO companyBankDTO) {
        return ResponseEntity.ok(companyBankService.create(companyBankDTO));
    }

    @GetMapping
    public ResponseEntity<CompanyBankDetailDTO> get() {
        return ResponseEntity.ok(companyBankService.get());
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CompanyBankDTO companyBankDTO) {
        return ResponseEntity.ok(companyBankService.update(id, companyBankDTO));
    }
}
