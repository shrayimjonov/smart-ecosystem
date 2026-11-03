package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.branch.BranchAddressLookupDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchDetailDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchListDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.service.CustomerBranchService;

import java.util.ArrayList;
import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.01.2023 13:43
 */
@RestController
@RequestMapping(ApiConstants.cabinetCustomerBranchRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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
    public ResponseEntity<CustomerBranchDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerBranchService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerBranchListDTO>> getList(@ParameterObject CustomerBranchFilter filter) {
        if (filter.getCustomerId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(customerBranchService.getList(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") String status) {
        customerBranchService.updateStatus(id, CommonStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.UpdateNumber)
    @PatchMapping(ApiConstants.updateNumber)
    public ResponseEntity<Void> updateNumber(@PathVariable Long id, @RequestParam("number") String number) {
        customerBranchService.updateNumber(id, number.trim());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sync/{customerId}")
    public ResponseEntity<Void> sync(@PathVariable Long customerId) {
        customerBranchService.sync(customerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/trade/sync")
    public ResponseEntity<Void> syncTradeBranches() {
        customerBranchService.syncTradeBranches();
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<BranchAddressLookupDTO>> lookUpWithAddress(@ParameterObject CustomerBranchFilter filter) {
        if (filter.getCustomerId() == null && StringUtils.isEmpty(filter.getTin())) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(customerBranchService.lookUpWithAddress(filter));
    }
}
