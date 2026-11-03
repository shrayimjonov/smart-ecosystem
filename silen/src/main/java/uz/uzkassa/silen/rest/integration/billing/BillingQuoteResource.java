package uz.uzkassa.silen.rest.integration.billing;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.integration.billing.BillingClient;
import uz.uzkassa.silen.integration.billing.dto.quote.QuoteChangeStatusRequestDto;
import uz.uzkassa.silen.integration.billing.dto.quote.QuoteCreateRequestDto;
import uz.uzkassa.silen.integration.billing.dto.quote.QuoteResponseDto;
import uz.uzkassa.silen.integration.billing.filter.IntegrationBaseFilter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quote")
public class BillingQuoteResource {

    private final BillingClient billingClient;

    @GetMapping("/agreement/{id}")
    public ResponseEntity<Object> getByAgreementId(@PathVariable("id") @NotEmpty @NotNull String id, @ModelAttribute IntegrationBaseFilter filter) {
        return ResponseEntity.ok(billingClient.getByQuoteAgreementId(id, filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponseDto> getById(@PathVariable("id") @NotEmpty @NotNull String id) {
        return ResponseEntity.ok(billingClient.getByQuoteId(id));
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<String> getTemplateById(@PathVariable("id") @NotEmpty @NotNull String id) {
        return ResponseEntity.ok(billingClient.getTemplateByQuoteId(id));
    }

    @PutMapping("/{id}/change-status")
    public ResponseEntity<QuoteResponseDto> quoteChangeStats(@PathVariable("id") @NotEmpty @NotNull String id, @RequestBody QuoteChangeStatusRequestDto dto) {
        return ResponseEntity.ok(billingClient.changeStatus(id, dto));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<QuoteResponseDto> uploadFile(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(billingClient.uploadToQuote(id, file));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody QuoteCreateRequestDto dto) {
        return ResponseEntity.ok(billingClient.createQuote(dto));
    }

}
