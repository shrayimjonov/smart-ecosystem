package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.customerbranch.CustomerBranchDTO;
import uz.uzkassa.cts.dto.customerbranch.cabinet.CustomerBranchDetailCabinetDTO;
import uz.uzkassa.cts.dto.customerbranch.cabinet.CustomerBranchListCabinetDTO;
import uz.uzkassa.cts.enums.CustomerBranchStatus;
import uz.uzkassa.cts.filter.CustomerBranchFilter;
import uz.uzkassa.cts.service.CustomerBranchService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetCustomerBranchRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetCustomerBranchResource {

    CustomerBranchService customerBranchService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CustomerBranchDTO customerBranchDTO) {
        return ResponseEntity.ok(customerBranchService.create(customerBranchDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CustomerBranchDTO customerBranchDTO) {
        return ResponseEntity.ok(customerBranchService.update(id, customerBranchDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerBranchDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerBranchService.getForCabinet(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerBranchListCabinetDTO>> getList(@ParameterObject CustomerBranchFilter filter) {
        return ResponseEntity.ok(customerBranchService.getListForCabinet(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject CustomerBranchFilter filter) {
        return ResponseEntity.ok(customerBranchService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CustomerBranchStatus status,
                                             @RequestParam(value = "reason", required = false) String reason) {
        customerBranchService.updateStatus(id, status, reason);
        return ResponseEntity.ok().build();
    }
}
