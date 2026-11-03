package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.auth.CustomerActivationDTO;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.customer.CustomerDTO;
import uz.uzkassa.cts.dto.customer.CustomerQuickDTO;
import uz.uzkassa.cts.dto.customer.cabinet.CustomerDetailCabinetDTO;
import uz.uzkassa.cts.dto.customer.cabinet.CustomerListCabinetDTO;
import uz.uzkassa.cts.filter.CustomerFilter;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.CustomerService;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetCustomerRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCustomerResource {

    CustomerService customerService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.create(customerDTO));
    }

    @PostMapping(value = "/quick-register")
    public ResponseEntity<Long> quickRegister(@RequestBody CustomerQuickDTO customerDTO) {
        return ResponseEntity.ok(customerService.quickRegister(customerDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.update(id, customerDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getForCabinet(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerListCabinetDTO>> getList(@ParameterObject CustomerFilter filter) {
        return ResponseEntity.ok(customerService.getListForCabinet(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CompanyCommonDTO>> lookUp(@ParameterObject CustomerFilter filter) {
        filter.setBranchId(Optional.ofNullable(filter.getBranchId()).orElseGet(SecurityUtils::getBranchId));
        return ResponseEntity.ok(customerService.lookUp(filter));
    }

    @GetMapping(ApiConstants.id + ApiConstants.sendActivationCode)
    public ResponseEntity<Void> sendActivationCode(@PathVariable Long id) {
        customerService.sendActivationCode(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ApiConstants.activate)
    public ResponseEntity<Void> activate(@RequestBody CustomerActivationDTO customerActivationDTO) {
        customerService.activate(customerActivationDTO);
        return ResponseEntity.ok().build();
    }
}
