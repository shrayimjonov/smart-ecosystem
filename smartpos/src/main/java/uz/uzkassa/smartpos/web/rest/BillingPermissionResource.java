package uz.uzkassa.smartpos.web.rest;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.BillingPermissionRequestDto;
import uz.uzkassa.smartpos.service.BillingPermissionService;

import java.util.List;

@RestController
@RequestMapping("/api/billing-permission")
@Slf4j
public class BillingPermissionResource {

    private final BillingPermissionService service;

    public BillingPermissionResource(BillingPermissionService service) {
        this.service = service;
    }

    @PostMapping("/sync-with-billing")
    public ResponseEntity<Void> syncWithBilling(@RequestBody @Valid List<BillingPermissionRequestDto> dtos) {
        service.syncWithBilling(dtos);
        return ResponseEntity.ok().build();
    }
}
