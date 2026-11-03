package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.client.CustomerDetailCabinetDTO;
import uz.uzkassa.horeca.dto.client.CustomerListCabinetDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDTO;
import uz.uzkassa.horeca.dto.reservation.CustomerDetailDTO;
import uz.uzkassa.horeca.filters.UserFilter;
import uz.uzkassa.horeca.service.CustomerService;

import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.09.2024
 */
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

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getForCabinet(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerListCabinetDTO>> getList(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(customerService.getListForCabinet(filter));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerDTO customerDetailDTO) {
        return ResponseEntity.ok(customerService.update(id, customerDetailDTO));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam(required = false) LocalDateTime lastModifiedDate) {
        customerService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject UserFilter filter) {
        return ResponseEntity.ok(customerService.lookUp(filter));
    }

    @PutMapping("/assign/{orderId}")
    public ResponseEntity<Long> assign(@PathVariable Long orderId, @RequestBody CustomerDTO customerDetailDTO) {
        return ResponseEntity.ok(customerService.assign(orderId, customerDetailDTO));
    }
}
