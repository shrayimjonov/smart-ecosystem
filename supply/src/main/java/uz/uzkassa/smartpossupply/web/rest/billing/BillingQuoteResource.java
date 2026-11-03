package uz.uzkassa.smartpossupply.web.rest.billing;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.integration.quote.QuoteChangeStatusRequestDto;
import uz.uzkassa.smartpossupply.dto.integration.quote.QuoteCreateRequestDto;
import uz.uzkassa.smartpossupply.dto.integration.quote.QuoteResponseDto;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.integration.billing.BillingServiceFeignClient;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetBillingQuoteRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BillingQuoteResource extends BillingBaseResource {

    BillingServiceFeignClient billingServiceFeignClient;
    ApplicationProperties applicationProperties;

    private String getToken() {
        return billingServiceFeignClient.getToken(
                applicationProperties.getBilling().getUsername(),
                applicationProperties.getBilling().getPassword()
        );
    }

    @Deprecated
    @GetMapping("/customer-cto-id/{id}")
    public ResponseEntity<QuoteResponseDto> getByCustomerId(
            @PathVariable("id") @NotEmpty @NotNull String id,
            @ModelAttribute BaseFilter filter
    ) {
        return billingServiceFeignClient.getByCustomerCtoId(getToken(), id, filter.getPage(), filter.getSize());
    }

    @GetMapping("/customer-tin/{tin}")
    public ResponseEntity<QuoteResponseDto> getByCustomerTin(
            @PathVariable("tin") @NotEmpty @NotNull String tin,
            @ModelAttribute BaseFilter filter
    ) {
        return billingServiceFeignClient.getByCustomerTin(getToken(), tin, filter.getPage(), filter.getSize());
    }

    @PutMapping("/{id}/change-status")
    public ResponseEntity<QuoteResponseDto> quoteChangeStats(
            @PathVariable("id") @NotEmpty @NotNull String id,
            @Valid @RequestBody QuoteChangeStatusRequestDto dto) {
        try {
            return billingServiceFeignClient.changeStatus(getToken(), id, dto);
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponseDto> getById(@PathVariable("id") Long id) {
        return billingServiceFeignClient.getByQuoteId(getToken(), id);
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<String> getTemplateById(@PathVariable("id") @NotEmpty @NotNull String id) {
        return billingServiceFeignClient.getTemplateByQuoteId(getToken(), id);
    }

    @Operation(summary = "upload file")
    @PostMapping("/{id}/upload")
    public ResponseEntity<QuoteResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String id) {
        try {
            return billingServiceFeignClient.uploadToQuote(getToken(), id, file);
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> crateQuote(@RequestBody QuoteCreateRequestDto dto) {
        return billingServiceFeignClient.createQuote(getToken(), dto);
    }

    @GetMapping("/agreement/{id}")
    public ResponseEntity<Object> getQuoteByAgreement(@PathVariable Long id) {
        return billingServiceFeignClient.getQuoteByAgreement(getToken(), id);
    }
}
