package uz.uzkassa.cts.web.rest.integration;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.config.FeignBadResponseWrapper;
import uz.uzkassa.cts.dto.integration.billing.*;
import uz.uzkassa.cts.dto.integration.response.BillingTransactionResponseDTO;
import uz.uzkassa.cts.enums.billing.BillingEntityType;
import uz.uzkassa.cts.filter.billing.AgreementFilter;
import uz.uzkassa.cts.filter.billing.BillingTransactionFilter;
import uz.uzkassa.cts.filter.billing.XizmatFilter;
import uz.uzkassa.cts.integrations.BillingServiceFeignClient;
import uz.uzkassa.cts.service.BillingClientService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/billing-services")
@RequiredArgsConstructor
public class AdminBillingServiceResource {

    private final BillingServiceFeignClient billingServiceFeignClient;
    private final BillingClientService billingClientService;

    @GetMapping("/xizmats")
    public Object getBillingXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(1);
        return billingServiceFeignClient.getXizmats(filter);
    }

    @GetMapping("/services")
    public Object getBillingXizmats() {
        return billingServiceFeignClient.getServiceTypes();
    }

    @GetMapping("/agreements/{id}")
    public Object getBillingAgreements(@PathVariable("id") Long id) {
        return billingServiceFeignClient.getAgreementById(id);
    }

    @PostMapping("/agreements")
    public Object saveBillingAgreement(@RequestBody @Valid CreateAgreementDTO dto) {
        return billingServiceFeignClient.saveAgreement(dto);
    }

    @PutMapping("/agreements")
    public Object updateBillingAgreement(@RequestBody @Valid CreateAgreementDTO dto) {
        return billingServiceFeignClient.updateAgreement(dto);
    }

    @PostMapping("/multi-agreements")
    public ResponseEntity<Object> saveBillingAgreements(@RequestBody CreateAgreementsDTO dto) {
        return billingServiceFeignClient.saveAgreement(dto);
    }

    @PostMapping("/{id}/update-agreements")
    public ResponseEntity<Object> updateAgreements(@PathVariable Long id, @RequestBody List<UpdateSubAgreementDto> dtos) {
        try {
            return billingServiceFeignClient.updateAgreementById(id, dtos);
        } catch (HystrixBadRequestException e) {
            if (e instanceof FeignBadResponseWrapper) {
                throw new BadRequestException(((FeignBadResponseWrapper) e).getBody());
            } else {
                throw new BadRequestException(e.getMessage());
            }
        } catch (Exception e) {
            log.error("Error occurred while updating agreements", e);
            throw new BadRequestException("Undefined error occurred");
        }
    }

    @PostMapping("/agreements/{id}/update-note")
    public ResponseEntity<AgreementDTO> updateNote(@PathVariable("id") Long id, @RequestBody String note) {
        return billingServiceFeignClient.updateAgreementNote(id, note);
    }

    @Operation(summary = "upload file")
    @PostMapping(value = "/attachment/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("relatedTo") BillingEntityType relatedTo, @RequestParam("relatedToId") String relatedToId) {
        return billingServiceFeignClient.upload(relatedTo, relatedToId, file);
    }

    @GetMapping("/get-last-active-offer")
    public ResponseEntity<PublicOfferResponseDto> getLastActiveOffer() {
        return billingServiceFeignClient.getLastActiveOffer();
    }

    @GetMapping("/customer-public-offer/tin/{tin}")
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByTin(@PathVariable @NotEmpty @NotNull String tin) {
        return billingClientService.customerOffer(tin);
    }

    @GetMapping("/balance/tin/{tin}")
    public ResponseEntity<BigDecimal> getBalanceByCtoId(@PathVariable @NotEmpty @NotNull String tin) {
        return billingServiceFeignClient.getBalanceByTin(tin);
    }

    @GetMapping("/all-balance/tin/{tin}")
    public ResponseEntity<Object> getAllBalanceByTin(@PathVariable String tin) {
        return billingServiceFeignClient.getAllBalanceByTin(tin);
    }

    @PostMapping("/customer-public-offer/create-by-tin")
    public ResponseEntity<CustomerPublicOfferResponseDto> createCustomerOffer(@Valid @RequestBody CustomerPublicOfferCtoIdRequestDto dto) {
        try {
            return billingServiceFeignClient.createCustomerOffer(dto);
        } catch (FeignException ex) {
            HttpStatus statusCode = HttpStatus.valueOf(ex.status());
            if (HttpStatus.BAD_REQUEST.equals(statusCode))
                throw new BadRequestException("Bad request");
            else if (HttpStatus.NOT_FOUND.equals(statusCode))
                throw new NotFoundException("entity not found");
            else
                throw ex;
        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("400"))
                throw new BadRequestException("Bad request");
            else if (ex.getMessage().contains("404"))
                throw new NotFoundException("entity not found");
            else
                throw ex;
        }
    }

    @PutMapping("/agreements/{id}/activate")
    public ResponseEntity<AgreementDTO> reactivateAgreement(@PathVariable Long id) {
        AgreementDTO result = billingServiceFeignClient.reactivateAgreement(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/agreements/agreements-manager-update")
    public Object updateAgreementManager(@RequestBody AgreementManagerRequestDTO dto) {
        return billingServiceFeignClient.updateAgreementManager(dto);
    }

    @GetMapping("/agreements/agreements-pageable")
    public Object getAllAgreementsPageable(AgreementFilter filter) {
        return billingServiceFeignClient.getAllAgreementsPageable(filter);
    }

    @GetMapping("/agreements/show-calculation/xizmat/{xizmatId}/customer/{tin}")
    public ResponseEntity<BigDecimal> startAgreementWithTin(@PathVariable Long xizmatId, @PathVariable String tin,
                                                            @RequestParam(required = false, name = "info") String info,
                                                            @RequestParam(name = "instance", required = false) Integer instance) {
        return billingServiceFeignClient.showCalculationByXizmatIdAndCustomerTin(xizmatId, tin, info, instance);
    }

    @GetMapping(value = "/transactions")
    public ResponseEntity<List<BillingTransactionResponseDTO>> getTransaction(@ParameterObject BillingTransactionFilter filter) {
        return ResponseEntity.ok(billingServiceFeignClient.getTransactionByTin(filter));
    }
}
