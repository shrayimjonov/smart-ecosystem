package uz.uzkassa.horeca.web.rest.billing;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.billing.quote.QuoteChangeStatusRequestDto;
import uz.uzkassa.horeca.dto.billing.quote.QuoteCreateRequestDto;
import uz.uzkassa.horeca.dto.billing.quote.QuoteResponseDto;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.integration.BillingServiceClient;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetBillingQuoteRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BillingQuoteResource extends BillingBaseResource {

    BillingServiceClient billingServiceClient;

    @Deprecated
    @GetMapping("/customer-cto-id/{id}")
    public ResponseEntity<QuoteResponseDto> getByCustomerId(
            @PathVariable("id") @NotEmpty @NotNull String id,
            @ModelAttribute BaseFilter filter
    ) {
        return ResponseEntity.ok(billingServiceClient.getByCustomerCtoId(id, filter.getPage(), filter.getSize()));
    }

    @GetMapping("/customer-tin/{tin}")
    public ResponseEntity<QuoteResponseDto> getByCustomerTin(
            @PathVariable("tin") @NotEmpty @NotNull String tin,
            @ModelAttribute BaseFilter filter
    ) {
        return ResponseEntity.ok(billingServiceClient.getByCustomerTin(tin, filter.getPage(), filter.getSize()));
    }

    @PutMapping("/{id}/change-status")
    public ResponseEntity<QuoteResponseDto> quoteChangeStats(
            @PathVariable("id") @NotEmpty @NotNull String id,
            @Valid @RequestBody QuoteChangeStatusRequestDto dto) {
        try {
            return ResponseEntity.ok(billingServiceClient.changeStatus(id, dto));
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponseDto> getById(@PathVariable("id") @NotEmpty @NotNull String id) {
        return ResponseEntity.ok(billingServiceClient.getByQuoteId(id));
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<String> getTemplateById(@PathVariable("id") @NotEmpty @NotNull String id) {
        return ResponseEntity.ok(billingServiceClient.getTemplateByQuoteId(id));
    }

    @Operation(summary = "upload file")
    @PostMapping("/{id}/upload")
    public ResponseEntity<QuoteResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String id) {
        try {
            return ResponseEntity.ok(billingServiceClient.uploadToQuote(id, file));
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create(@RequestBody QuoteCreateRequestDto dto) {
        return ResponseEntity.ok(billingServiceClient.createQuote(dto));
    }
}
