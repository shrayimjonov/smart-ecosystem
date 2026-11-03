package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.sync.customer.CustomerSyncDTO;
import uz.uzkassa.cts.enums.RelationType;
import uz.uzkassa.cts.filter.CustomerFilter;
import uz.uzkassa.cts.service.CustomerSyncService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminCustomerSyncRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCustomerSyncResource {

    CustomerSyncService customerSyncService;

    @GetMapping
    public ResponseEntity<Page<CustomerSyncDTO>> getList(@ParameterObject CustomerFilter filter) {
        return ResponseEntity.ok(customerSyncService.getList(filter));
    }

    @GetMapping("/{tin}")
    public ResponseEntity<CustomerSyncDTO> get(@PathVariable String tin) {
        return ResponseEntity.ok(customerSyncService.get(tin));
    }

    @PostMapping
    public ResponseEntity<Long> sync(@RequestBody CustomerSyncDTO customerSyncDTO) {
        return ResponseEntity.ok(customerSyncService.sync(customerSyncDTO));
    }

    @GetMapping("/{id}/{relationType}")
    public ResponseEntity<Long> syncTo(@PathVariable Long id, @PathVariable RelationType relationType) {
        return ResponseEntity.ok(customerSyncService.sync(id, relationType));
    }
}
