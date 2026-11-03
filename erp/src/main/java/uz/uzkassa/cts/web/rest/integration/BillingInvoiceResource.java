package uz.uzkassa.cts.web.rest.integration;


import feign.FeignException;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.integration.billing.InvoiceResponseDto;
import uz.uzkassa.cts.filter.billing.PageFilter;
import uz.uzkassa.cts.integrations.BillingServiceFeignClient;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

@RestController
@RequestMapping("/api/billing-invoice")
@AllArgsConstructor
public class BillingInvoiceResource {

    private final BillingServiceFeignClient billingServiceFeignClient;

    @GetMapping("/customer-tin/{tin}")
    public ResponseEntity<Page<InvoiceResponseDto>> getByCustomerTin(@PathVariable @NotEmpty @NotNull String tin, @ModelAttribute PageFilter filter) {
        return billingServiceFeignClient.getInvoiceByCustomerTin(tin, filter.getPage(), filter.getSize());
    }

    @GetMapping("/get-by-quote-id/{id}")
    public ResponseEntity<Object> getByQuoteId(@PathVariable @NotEmpty @NotNull String id, @ModelAttribute PageFilter filter) {
        return billingServiceFeignClient.getInvoiceByQuoteId(id, filter.getPage(), filter.getSize());
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<String> getInvoiceTemplateById(@PathVariable @NotEmpty @NotNull String id) {
        ResponseEntity<String> response;
        try {
            response = billingServiceFeignClient.getInvoiceTemplateById(id);
        } catch (FeignException ex) {
            HttpStatus statusCode = HttpStatus.valueOf(ex.status());
            if (HttpStatus.BAD_REQUEST.equals(statusCode))
                throw new BadRequestException(ex.getMessage());
            else if (HttpStatus.NOT_FOUND.equals(statusCode))
                throw new NotFoundException(ex.getMessage());
            else
                throw ex;
        }
        return response;
    }
}
