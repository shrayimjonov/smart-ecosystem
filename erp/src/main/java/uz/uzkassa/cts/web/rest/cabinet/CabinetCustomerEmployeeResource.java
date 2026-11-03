package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.customeremployee.CustomerEmployeeDTO;
import uz.uzkassa.cts.dto.customeremployee.CustomerEmployeeListDTO;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.service.CustomerEmployeeService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetCustomerEmployeeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCustomerEmployeeResource {

    CustomerEmployeeService customerEmployeeService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerEmployeeDTO customerEmployeeDTO) {
        return ResponseEntity.ok(customerEmployeeService.create(customerEmployeeDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerEmployeeDTO customerEmployeeDTO) {
        return ResponseEntity.ok(customerEmployeeService.update(id, customerEmployeeDTO));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<CustomerEmployeeListDTO>> getList(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerEmployeeService.getList(customerId));
    }

    @GetMapping("/roles")
    public ResponseEntity<EnumSet<Role>> getRoles() {
        return ResponseEntity.ok(EnumSet.of(Role.DIRECTOR, Role.ACCOUNTANT));
    }
}
