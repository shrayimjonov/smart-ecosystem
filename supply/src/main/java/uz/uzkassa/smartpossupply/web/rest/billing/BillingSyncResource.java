package uz.uzkassa.smartpossupply.web.rest.billing;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.dto.billingpermission.BillingPermissionDTO;
import uz.uzkassa.smartpossupply.service.BillingSyncService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/api/billing-permission")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BillingSyncResource {

    BillingSyncService billingSyncService;

    @PostMapping("/sync-with-billing")
    public ResponseEntity<Void> syncWithBilling(@RequestBody @Valid List<BillingPermissionDTO> billingPermissionDTOS) {
        billingSyncService.syncWithBilling(billingPermissionDTOS);
        return ResponseEntity.ok().build();
    }
}
