package uz.uzkassa.silen.rest.integration.billing;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.integration.billing.BillingClient;
import uz.uzkassa.silen.integration.billing.filter.IntegrationBaseFilter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing-invoice")
public class BillingInvoice {

    private final BillingClient billingClient;

    @GetMapping("/get-by-quote-id/{id}")
    public ResponseEntity<Object> getByQuoteId(@PathVariable @NotEmpty @NotNull String id, @ModelAttribute IntegrationBaseFilter filter) {
        return ResponseEntity.ok(billingClient.getInvoiceByQuoteId(id, filter));
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<String> getInvoiceTemplateById(@PathVariable @NotEmpty @NotNull String id) {
        return ResponseEntity.ok(billingClient.getInvoiceTemplateById(id));
    }

    @GetMapping("/get-by-agreement-id/{id}")
    public ResponseEntity<Object> getInvoiceByAgreementId(@PathVariable @NotEmpty @NotNull String id, @ModelAttribute IntegrationBaseFilter filter) {
        return ResponseEntity.ok(billingClient.getInvoiceByAgreementId(id, filter));
    }
}
