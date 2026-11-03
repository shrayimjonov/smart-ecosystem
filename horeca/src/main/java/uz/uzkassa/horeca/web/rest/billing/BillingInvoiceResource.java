package uz.uzkassa.horeca.web.rest.billing;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.billing.billinginvoice.InvoiceResponseDto;
import uz.uzkassa.horeca.filters.billing.BillingBaseFilter;
import uz.uzkassa.horeca.filters.billing.BillingPageFilter;
import uz.uzkassa.horeca.integration.BillingServiceClient;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetBillingInvoiceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BillingInvoiceResource extends BillingBaseResource {

    BillingServiceClient billingServiceClient;

    @Deprecated
    @GetMapping("/customer/{id}")
    public ResponseEntity<InvoiceResponseDto> getByCustomerId(
            @PathVariable @NotEmpty @NotNull String id,
            @ModelAttribute BillingPageFilter filter
    ) {
        return ResponseEntity.ok(billingServiceClient.getInvoiceByCustomerCtoId(id, filter.getPage(), filter.getSize()));
    }

    @GetMapping("/customer-tin/{tin}")
    public ResponseEntity<InvoiceResponseDto> getByCustomerTin(
            @PathVariable @NotEmpty @NotNull String tin,
            @ModelAttribute BillingPageFilter filter
    ) {
        return ResponseEntity.ok(billingServiceClient.getInvoiceByCustomerTin(tin, filter.getPage(), filter.getSize()));
    }

    @GetMapping("/get-by-quote-id/{id}")
    public ResponseEntity<Object> getByQuoteId(
            @PathVariable @NotEmpty @NotNull String id,
            @ModelAttribute BillingPageFilter filter
    ) {
        return ResponseEntity.ok(billingServiceClient.getInvoiceByQuoteId(id, filter.getPage(), filter.getSize()));
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<String> getInvoiceTemplateById(@PathVariable @NotEmpty @NotNull String id) {
        try {
            return ResponseEntity.ok(billingServiceClient.getInvoiceTemplateById(id));
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @GetMapping("/get-by-agreement-id/{id}")
    public ResponseEntity<Object> getInvoiceByAgreementId(@PathVariable @NotEmpty @NotNull String id, @ModelAttribute BillingBaseFilter filter) {
        return ResponseEntity.ok(billingServiceClient.getInvoiceByAgreementId(id, filter));
    }
}
