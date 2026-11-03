package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.customerbank.CustomBankDTO;
import uz.uzkassa.cts.dto.customerbank.cabinet.CustomBankDetailCabinetDTO;
import uz.uzkassa.cts.service.CustomerBankService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetCustomerBankRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCustomerBankResource {

    CustomerBankService customerBankService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomBankDTO customBankDTO) {
        return ResponseEntity.ok(customerBankService.create(customBankDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomBankDTO customBankDTO) {
        return ResponseEntity.ok(customerBankService.update(id, customBankDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomBankDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerBankService.get(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomBankDetailCabinetDTO> getByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerBankService.getByCustomerId(customerId));
    }

}
