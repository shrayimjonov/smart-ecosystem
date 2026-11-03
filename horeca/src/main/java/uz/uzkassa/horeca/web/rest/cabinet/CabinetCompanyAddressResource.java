package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.address.AddressDTO;
import uz.uzkassa.horeca.dto.address.AddressDetailDTO;
import uz.uzkassa.horeca.service.CompanyService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCompanyAddressRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCompanyAddressResource {

    CompanyService companyService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody AddressDTO addressDTO) {
        companyService.createAddress(addressDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<AddressDetailDTO> get() {
        return ResponseEntity.ok(companyService.getAddress());
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody AddressDTO addressDTO) {
        companyService.updateAddress(addressDTO);
        return ResponseEntity.ok().build();
    }
}
