package uz.uzkassa.smartpos.web.rest;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.dto.filter.PageFilter;
import uz.uzkassa.smartpos.dto.integration.quote.QuoteChangeStatusRequestDto;
import uz.uzkassa.smartpos.dto.integration.quote.QuoteCreateRequestDto;
import uz.uzkassa.smartpos.dto.integration.quote.QuoteResponseDto;
import uz.uzkassa.smartpos.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpos.service.TelegramService;
import uz.uzkassa.smartpos.service.TokenStoreService;

@RestController
@RequestMapping("/api/quote")
@RequiredArgsConstructor
public class QuoteResource {
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final TelegramService telegramService;
    private final TokenStoreService tokenStoreService;

    @Deprecated
    @GetMapping("/customer-cto-id/{id}")
    public ResponseEntity<Object> getByCustomerId(@PathVariable("id") @NotEmpty @NotNull String id, @ParameterObject PageFilter filter) {
        return billingServiceFeignClient.getByCustomerCtoId(tokenStoreService.getToken(TokenType.BILLING), id, filter.getPage(), filter.getSize());
    }

    //    @AuthValidate(name = "tin", authEnum = AuthEnum.COMPANY_TIN)
    @GetMapping("/customer-tin/{tin}")
    public ResponseEntity<Object> getByCustomerTin(@PathVariable("tin") @NotEmpty @NotNull String tin, @ParameterObject PageFilter filter) {
        return billingServiceFeignClient.getByCustomerTin(tokenStoreService.getToken(TokenType.BILLING), tin, filter.getPage(), filter.getSize());
    }

    @PutMapping("/{id}/change-status")
    public ResponseEntity<QuoteResponseDto> quoteChangeStats(@PathVariable("id") @NotEmpty @NotNull String id, @RequestBody QuoteChangeStatusRequestDto dto) {
        return billingServiceFeignClient.changeStatus(tokenStoreService.getToken(TokenType.BILLING), id, dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponseDto> getById(@PathVariable("id") @NotEmpty @NotNull String id) {
        return billingServiceFeignClient.getByQuoteId(tokenStoreService.getToken(TokenType.BILLING), id);
    }

    @PostMapping("/telegram/{id}")
    public ResponseEntity<Void> sendToTelegram(@PathVariable("id") String id) {
        ResponseEntity<QuoteResponseDto> response = billingServiceFeignClient.getByQuoteId(tokenStoreService.getToken(TokenType.BILLING), id);
        QuoteResponseDto dto = response.getBody();
        if (dto != null) {
            telegramService.sendQuote(dto, 0);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<String> getTemplateById(@PathVariable("id") @NotEmpty @NotNull String id) {
        return billingServiceFeignClient.getTemplateByQuoteId(tokenStoreService.getToken(TokenType.BILLING), id);
    }


    @GetMapping("/agreement/{id}")
    public ResponseEntity<Object> getQuoteByAgreement(@PathVariable String id) {
        return billingServiceFeignClient.getQuoteByAgreement(tokenStoreService.getToken(TokenType.BILLING), id);
    }

    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String id) {
        return billingServiceFeignClient.uploadToQuote(tokenStoreService.getToken(TokenType.BILLING), id, file);
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Object> crateQuote(@RequestBody QuoteCreateRequestDto dto) {
        return billingServiceFeignClient.createQuote(tokenStoreService.getToken(TokenType.BILLING), dto);
    }
}
