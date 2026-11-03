package uz.uzkassa.smartpos.web.rest;

import feign.FeignException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.dto.filter.PageFilter;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpos.service.TokenStoreService;

@RestController
@RequestMapping("/api/billing-invoice")
@RequiredArgsConstructor
public class BillingInvoice {
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final TokenStoreService tokenStoreService;

    @Deprecated
    @GetMapping("/customer/{id}")
    public ResponseEntity<Object> getByCustomerId(@PathVariable @NotEmpty @NotNull String id, @ParameterObject PageFilter filter) {
        return billingServiceFeignClient.getInvoiceByCustomerCtoId(tokenStoreService.getToken(TokenType.BILLING), id, filter.getPage(), filter.getSize());
    }

    @GetMapping("/customer-tin/{tin}")
    public ResponseEntity<Object> getByCustomerTin(@PathVariable @NotEmpty @NotNull String tin, @ParameterObject PageFilter filter) {
        return billingServiceFeignClient.getInvoiceByCustomerTin(tokenStoreService.getToken(TokenType.BILLING), tin, filter.getPage(), filter.getSize());
    }

    @GetMapping("/get-by-agreement-id/{id}")
    public ResponseEntity<Object> getByQuoteId(@PathVariable @NotEmpty @NotNull String id, @ParameterObject PageFilter filter) {
        return billingServiceFeignClient.getInvoiceByAgreementId(tokenStoreService.getToken(TokenType.BILLING), id, filter.getPage(), filter.getSize());
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<String> getInvoiceTemplateById(@PathVariable @NotEmpty @NotNull String id) {
        ResponseEntity<String> response;
        try {
            response = billingServiceFeignClient.getInvoiceTemplateById(tokenStoreService.getToken(TokenType.BILLING), id);
        } catch (FeignException ex) {
            HttpStatus statusCode = HttpStatus.valueOf(ex.status());
            if (HttpStatus.BAD_REQUEST.equals(statusCode))
                throw new BadRequestException(ex.getMessage());
            else if (HttpStatus.NOT_FOUND.equals(statusCode))
                throw new EntityNotFound(ex.getMessage());
            else
                throw ex;
        }
        return response;
    }
}
