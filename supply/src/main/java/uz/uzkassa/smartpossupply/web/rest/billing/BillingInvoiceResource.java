package uz.uzkassa.smartpossupply.web.rest.billing;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.integration.billinginvoice.InvoiceResponseDto;
import uz.uzkassa.smartpossupply.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpossupply.integration.filter.BillingPageFilter;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetBillingInvoiceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BillingInvoiceResource extends BillingBaseResource {

    BillingServiceFeignClient billingServiceFeignClient;
    ApplicationProperties applicationProperties;

    private String getToken() {
        return billingServiceFeignClient.getToken(
                applicationProperties.getBilling().getUsername(),
                applicationProperties.getBilling().getPassword()
        );
    }

    @Deprecated
    @GetMapping("/customer/{id}")
    public ResponseEntity<InvoiceResponseDto> getByCustomerId(
            @PathVariable @NotEmpty @NotNull String id,
            @ModelAttribute BillingPageFilter filter
    ) {
        return billingServiceFeignClient.getInvoiceByCustomerCtoId(getToken(), id, filter.getPage(), filter.getSize());
    }

    @GetMapping("/customer-tin/{tin}")
    public ResponseEntity<InvoiceResponseDto> getByCustomerTin(
            @PathVariable @NotEmpty @NotNull String tin,
            @ModelAttribute BillingPageFilter filter
    ) {
        return billingServiceFeignClient.getInvoiceByCustomerTin(getToken(), tin, filter.getPage(), filter.getSize());
    }

    @GetMapping("/get-by-quote-id/{id}")
    public ResponseEntity<Object> getByQuoteId(
            @PathVariable @NotEmpty @NotNull String id,
            @ModelAttribute BillingPageFilter filter
    ) {
        return billingServiceFeignClient.getInvoiceByQuoteId(getToken(), id, filter.getPage(), filter.getSize());
    }

    @GetMapping("/get-by-agreement-id/{id}")
    public ResponseEntity<Object> getByAgreementId(
            @PathVariable Long id,
            @ModelAttribute BillingPageFilter filter
    ) {
        return billingServiceFeignClient.getInvoiceByAgreementId(getToken(), id, filter.getPage(), filter.getSize());
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<String> getInvoiceTemplateById(@PathVariable @NotEmpty @NotNull String id) {
        try {
            return billingServiceFeignClient.getInvoiceTemplateById(getToken(), id);
        } catch (Exception ex) {
            throw getException(ex);
        }
    }
}
