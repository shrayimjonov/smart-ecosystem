package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.sync.customerbranch.CustomerBranchSyncDTO;
import uz.uzkassa.cts.enums.RelationType;
import uz.uzkassa.cts.filter.CustomerBranchFilter;
import uz.uzkassa.cts.service.CustomerBranchSyncService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminCustomerBranchSyncRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCustomerBranchSyncResource {

    CustomerBranchSyncService customerBranchSyncService;

    @GetMapping
    public ResponseEntity<Page<CustomerBranchSyncDTO>> getList(@ParameterObject CustomerBranchFilter filter) {
        return ResponseEntity.ok(customerBranchSyncService.getList(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CustomerBranchSyncDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerBranchSyncService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> sync(@RequestBody CustomerBranchSyncDTO customerBranchSyncDTO) {
        return ResponseEntity.ok(customerBranchSyncService.sync(customerBranchSyncDTO));
    }

    @GetMapping("/{id}/{relationType}")
    public ResponseEntity<Long> syncTo(@PathVariable Long id, @PathVariable RelationType relationType) {
        return ResponseEntity.ok(customerBranchSyncService.sync(id, relationType));
    }
}
