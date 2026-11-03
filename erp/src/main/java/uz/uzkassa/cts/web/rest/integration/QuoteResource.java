package uz.uzkassa.cts.web.rest.integration;


import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.integration.billing.QuoteChangeStatusRequestDto;
import uz.uzkassa.cts.dto.integration.billing.QuoteResponseDto;
import uz.uzkassa.cts.filter.billing.PageFilter;
import uz.uzkassa.cts.integrations.BillingServiceFeignClient;
import uz.uzkassa.cts.service.CustomerService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

@RestController
@RequestMapping("/api/quote")
@RequiredArgsConstructor
public class QuoteResource {

    private final BillingServiceFeignClient billingServiceFeignClient;
    private final CustomerService customerService;

    @GetMapping("/customer-tin/{tin}")
    public ResponseEntity<Page<QuoteResponseDto>> getByCustomerTin(@PathVariable("tin") @NotEmpty @NotNull String tin, @ModelAttribute PageFilter filter) {
        return billingServiceFeignClient.getByCustomerTin(tin, filter.getPage(), filter.getSize());
    }

    @PutMapping("/{id}/change-status")
    public ResponseEntity<QuoteResponseDto> quoteChangeStats(@PathVariable("id") @NotEmpty @NotNull String id,
                                                             @RequestBody QuoteChangeStatusRequestDto dto) {
        ResponseEntity<QuoteResponseDto> response;
        try {
            if (StringUtils.isNotEmpty(dto.getTin())) {
                customerService.attachManager(dto.getTin(), null);
            }
            response = billingServiceFeignClient.changeStatus(id, dto);
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

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponseDto> getById(@PathVariable("id") @NotEmpty @NotNull String id) {
        return billingServiceFeignClient.getByQuoteId(id);
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<String> getTemplateById(@PathVariable("id") @NotEmpty @NotNull String id) {
        return billingServiceFeignClient.getTemplateByQuoteId(id);
    }

    @Operation(summary = "upload file")
    @PostMapping("/{id}/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String id) {
        ResponseEntity<String> response;
        try {
            response = billingServiceFeignClient.uploadToQuote(id, file);
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

