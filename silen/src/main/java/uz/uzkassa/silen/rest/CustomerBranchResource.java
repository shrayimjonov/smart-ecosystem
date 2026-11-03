package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.CustomerBranchDTO;
import uz.uzkassa.silen.dto.filter.CustomerFilter;
import uz.uzkassa.silen.service.CustomerBranchService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Customer}.
 */
@RestController
@RequestMapping("/api/customer/branch")
@RequiredArgsConstructor
@Validated
public class CustomerBranchResource {
    private final CustomerBranchService customerBranchService;

    @PostMapping("/sync/{customerId}")
    public ResponseEntity<Long> syncFromNIC(@PathVariable @Valid Long customerId) {
        return ResponseEntity.ok(customerBranchService.syncBranches(customerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerBranchDTO> update(@PathVariable Long id, @RequestBody @Valid CustomerBranchDTO dto) {
        return ResponseEntity.ok(customerBranchService.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<Page<CustomerBranchDTO>> findAllByFilter(@ModelAttribute @Valid CustomerFilter filter) {
        return ResponseEntity.ok(customerBranchService.findAllByFilter(filter));
    }

    @GetMapping("/items")
    public ResponseEntity<List<CustomerBranchDTO>> getItems(@ModelAttribute @Valid CustomerFilter filter) {
        return ResponseEntity.ok(customerBranchService.getItems(filter));
    }

    @GetMapping("/by-tin/{tin}")
    public ResponseEntity<List<CustomerBranchDTO>> getCustomerBranchesByTin(@PathVariable String tin) {
        return ResponseEntity.ok(customerBranchService.getCustomerBranchesByTin(tin));
    }

}
