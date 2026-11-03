package uz.uzkassa.silen.rest.integration.billing;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.billingpermission.BillingPermissionRequestDto;
import uz.uzkassa.silen.service.BillingPermissionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing-permission")
public class BillingPermissionResource {

    private final BillingPermissionService service;

    @PostMapping("/sync-with-billing")
    public ResponseEntity<Void> syncWithBilling(@RequestBody @Valid List<BillingPermissionRequestDto> dtos) {
        service.syncWithBilling(dtos);
        return ResponseEntity.ok().build();
    }
}
