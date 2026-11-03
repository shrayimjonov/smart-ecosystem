package uz.uzkassa.silen.rest.integration.billing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.integration.billing.BillingClient;
import uz.uzkassa.silen.integration.billing.dto.agreement.AgreementManagerRequestDTO;
import uz.uzkassa.silen.integration.billing.dto.customerpublicoffer.CustomerPublicOfferCtoIdRequestDto;
import uz.uzkassa.silen.integration.billing.dto.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.silen.integration.billing.dto.publicoffer.PublicOfferResponseDto;
import uz.uzkassa.silen.integration.billing.filter.XizmatFilter;
import uz.uzkassa.silen.service.OrganizationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/billing-services")
public class BillingAdminResource {

    private final BillingClient billingClient;
    private final OrganizationService organizationService;

    @GetMapping("/agreements/{id}")
    public Object getBillingAgreementsForAdmin(@PathVariable("id") Long id) {
        return billingClient.getAgreementById(id);
    }

    @PostMapping("/customer-public-offer/create-by-tin")
    public ResponseEntity<CustomerPublicOfferResponseDto> createCustomerOffer(@Valid @RequestBody CustomerPublicOfferCtoIdRequestDto dto) {
        CustomerPublicOfferResponseDto customerOffer = billingClient.createCustomerOffer(dto);
        organizationService.offered(dto.getTin(), dto.getDocumentType());
        return ResponseEntity.ok(customerOffer);
    }

    @PostMapping("/agreements/agreements-manager-update")
    public ResponseEntity<Object> updateAgreementManager(@RequestBody AgreementManagerRequestDTO dto) {
        return ResponseEntity.ok(billingClient.updateAgreementManager(dto));
    }

    @GetMapping("/balance/tin/{tin}")
    public ResponseEntity<Object> getBalanceByCtoId(@PathVariable @NotEmpty @NotNull String tin) {
        return ResponseEntity.ok(billingClient.getBalanceByTin(tin));
    }

    @GetMapping("/customer-public-offer/tin/{tin}")
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByTin(@PathVariable @NotEmpty @NotNull String tin) {
        return ResponseEntity.ok(billingClient.getCustomerOfferByTin(tin));
    }

    @GetMapping("/get-last-active-offer")
    public ResponseEntity<PublicOfferResponseDto> getLastActiveOffer() {
        return ResponseEntity.ok(billingClient.getLastActiveOffer());
    }

    @GetMapping("/xizmats/single")
    public Object getSingleXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setDisplayInCabinet(null);
        return billingClient.getSingleXizmats(filter);
    }

    @GetMapping("/xizmats/grouped")
    public Object getGroupedXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setDisplayInCabinet(null);
        return billingClient.getGroupedXizmats(filter);
    }

    @GetMapping("/xizmats/{id}")
    public Object getXizmatsChildren(@PathVariable Long id) {
        return billingClient.getXizmatsChildren(id);
    }
}
