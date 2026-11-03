package uz.uzkassa.smartpossupply.web.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDetailDTO;
import uz.uzkassa.smartpossupply.service.CompanyBankService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCompanyBankRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCompanyBankResource {

    CompanyBankService companyBankService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CompanyBankDTO companyBankDTO) {
        return ResponseEntity.ok(companyBankService.create(companyBankDTO));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping
    public ResponseEntity<CompanyBankDetailDTO> get(@RequestParam(value = "companyId", required = false) Long companyId) {
        return ResponseEntity.ok(companyBankService.get(companyId));
    }

    @Operation(summary = ApiConstants.Update)
    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody CompanyBankDTO companyBankDTO) {
        return ResponseEntity.ok(companyBankService.update(id, companyBankDTO));
    }
}
