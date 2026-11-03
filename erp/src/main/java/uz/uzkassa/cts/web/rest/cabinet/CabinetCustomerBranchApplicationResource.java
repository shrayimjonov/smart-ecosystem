package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.customerbranchapplication.base.CustomerBranchApplicationDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.cabinet.CustomerBranchApplicationDetailCabinetDTO;
import uz.uzkassa.cts.dto.customerbranchapplication.cabinet.CustomerBranchApplicationListCabinetDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CustomerBranchApplicationService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetCustomerBranchApplicationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCustomerBranchApplicationResource {

    CustomerBranchApplicationService customerBranchApplicationService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerBranchApplicationDTO customerBranchApplicationDTO) {
        return ResponseEntity.ok(customerBranchApplicationService.create(customerBranchApplicationDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerBranchApplicationDTO customerBranchApplicationDTO) {
        return ResponseEntity.ok(customerBranchApplicationService.update(id, customerBranchApplicationDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerBranchApplicationDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerBranchApplicationService.getForCabinet(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerBranchApplicationListCabinetDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(customerBranchApplicationService.getListForCabinet(filter));
    }
}
