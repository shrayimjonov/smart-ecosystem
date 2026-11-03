package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.apay.APayCheckCompanyDTO;
import uz.uzkassa.cts.dto.apay.ApayCompanyEnableDTO;
import uz.uzkassa.cts.dto.auth.ActivateDTO;
import uz.uzkassa.cts.dto.customer.*;
import uz.uzkassa.cts.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerListDTO;
import uz.uzkassa.cts.enums.CustomerLegalStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.integrations.ApayClient;
import uz.uzkassa.cts.service.CustomerOldService;

/**
 * REST controller for managing {@link uz.uzkassa.cts.domain.Customer}.
 */
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer")
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class CustomerResource {

    private final CustomerOldService customerOldService;
    private final ApayClient apayClient;

    @Operation(summary = "List By Employee")
    @GetMapping("/by-employee")
    public ResponseEntity<Page<CustomerShortDTO>> listByEmployee(BaseFilter filter) {
        log.debug("REST request to get all CustomerShortDTO : {}", filter);
        return ResponseEntity.ok(customerOldService.listByEmployee(filter));
    }

    @Operation(summary = "List By Branch")
    @GetMapping("/by-branch")
    public ResponseEntity<Page<CustomerShortDTO>> listByBranch(BaseFilter filter) {
        log.debug("REST request to get all CustomerShortDTO : {}", filter);
        return ResponseEntity.ok(customerOldService.listByEmployee(filter));
    }

    @Operation(summary = "Activate")
    @PutMapping("/activate")
    public ResponseEntity<Void> activate(@RequestBody ActivateDTO activateDTO) {
        log.debug("REST request to activate customer ActivateDTO:{}", activateDTO.getLogin());
        customerOldService.activate(activateDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Count List to Sync")
    @GetMapping("/sync/count")
    public ResponseEntity<Long> getListCountToSync() {
        log.debug("REST request to get all customers count");
        return ResponseEntity.ok(customerOldService.getListCountToSync());
    }

    @Operation(summary = "Get List to Sync")
    @GetMapping("/sync/list")
    public ResponseEntity<Page<SyncCustomerListDTO>> getListToSync(BaseFilter filter) {
        log.debug("REST request to get all Customers : {}", filter);
        return ResponseEntity.ok(customerOldService.getListToSync(filter));
    }

    @Operation(summary = "Sync Apay")
    @PutMapping("/sync/apay")
    public ResponseEntity<Void> syncApay(@RequestBody ApayCompanyEnableDTO apayCompanyEnableDTO) {
        customerOldService.syncApay(apayCompanyEnableDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update App Type")
    @PatchMapping("/{id}/app-type")
    public ResponseEntity<Void> updateAppType(@PathVariable Long id, @RequestParam("appType") String appType) {
        customerOldService.updateAppType(id, appType);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update App Type")
    @PostMapping("/change-app-type")
    public ResponseEntity<Void> updateAppTypeByTin(@RequestBody ChangeAppTypeRequestDto dto) {
        customerOldService.updateAppType(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<Page<String>> getAllToSync(BaseFilter filter) {
        return ResponseEntity.ok(customerOldService.getAllToSync(filter));
    }

    @GetMapping("/check/{tin}")
    public ResponseEntity<SoliqCompanyDTO> check(@PathVariable String tin) {
        return ResponseEntity.ok(customerOldService.check(tin));
    }

    @Operation(summary = "Update Customer status")
    @PatchMapping("/{id}/customer-status")
    public ResponseEntity<Void> updateCustomerStatus(@PathVariable Long id, @RequestParam("status") String status) {
        customerOldService.updateCustomerStatus(id, CustomerLegalStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Sync customer old tin with Tax")
    @PutMapping(value = "/sync-tax-old-tin")
    public ResponseEntity<Void> syncWithTax() {
        log.debug("Sync customer old tin with tax\n");
        customerOldService.syncByCustomerTin();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/apay/tariffs")
    public ResponseEntity<Void> updateCustomerApayTariff(@RequestBody @Valid UpdateCustomerAPayTariffDTO dto) {
        customerOldService.updateAPayTariff(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/apay/check/{tin}")
    public ResponseEntity<APayCheckCompanyDTO> checkCompany(@PathVariable String tin) {
        return apayClient.checkCompanyExist(tin);
    }

    @GetMapping("/public-offer/send")
    public ResponseEntity<Void> sendPublicOffer(@RequestParam Long customerId) {
        customerOldService.sendPublicOffer(customerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/public-offer/verify")
    public ResponseEntity<Void> verifyPublicOffer(@RequestBody @Valid PublicOfferVerifyRequestDto dto) {
        customerOldService.verifyPublicOffer(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/status-tariff-rent/{tin}")
    public ResponseEntity<CustomerResponseDTO> statusTariffRent(@PathVariable String tin) {
        log.debug("REST request to get tariff and rent: {}", tin);
        return ResponseEntity.ok(customerOldService.customerStatus(tin));
    }
}
