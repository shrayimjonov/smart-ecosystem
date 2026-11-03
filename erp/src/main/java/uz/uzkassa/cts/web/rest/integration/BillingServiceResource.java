package uz.uzkassa.cts.web.rest.integration;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.config.FeignBadResponseWrapper;
import uz.uzkassa.cts.dto.integration.billing.*;
import uz.uzkassa.cts.enums.billing.*;
import uz.uzkassa.cts.filter.billing.*;
import uz.uzkassa.cts.integrations.BillingServiceFeignClient;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/billing-services")
@RequiredArgsConstructor
public class BillingServiceResource {

    private final BillingServiceFeignClient billingServiceFeignClient;

    @GetMapping("/xizmats")
    public Object getBillingXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(1);
        return billingServiceFeignClient.getXizmats(filter);
    }

    @GetMapping("/xizmats/grouped")
    public Object getGroupedXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(1);
        return billingServiceFeignClient.getGroupedXizmats(filter);
    }

    @GetMapping("/xizmats/single")
    public Object getSingleXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(1);
        return billingServiceFeignClient.getSingleXizmats(filter);
    }

    @GetMapping("/agreements/{id}")
    public Object getBillingAgreements(@PathVariable("id") Long id) {
        return billingServiceFeignClient.getAgreementById(id);
    }

    @PostMapping("/agreements")
    public Object saveBillingAgreement(@RequestBody @Valid CreateAgreementDTO dto) {
        return billingServiceFeignClient.saveAgreement(dto);
    }

    @PostMapping("/multi-agreements")
    public ResponseEntity<Object> saveBillingAgreements(@RequestBody CreateAgreementsDTO dto) {
        try {
            return billingServiceFeignClient.saveAgreement(dto);
        } catch (HystrixBadRequestException e) {
            if (e instanceof FeignBadResponseWrapper) {
                throw new BadRequestException(((FeignBadResponseWrapper) e).getBody());
            } else {
                throw new BadRequestException(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @PutMapping("/agreements/{id}/pause")
    public ResponseEntity<AgreementDTO> pauseAgreement(@PathVariable Long id) {
        System.out.println("REST request to update Agreement.id : " + id);
        AgreementDTO result = billingServiceFeignClient.pauseAgreement(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/agreements/{id}/cancel")
    public ResponseEntity<AgreementDTO> cancelAgreement(@PathVariable Long id) {
        System.out.println("REST request to update Agreement.id : " + id);
        AgreementDTO result = billingServiceFeignClient.cancelAgreement(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/agreements/{id}/activate")
    public ResponseEntity<AgreementDTO> reactivateAgreement(@PathVariable Long id) {
        System.out.println("REST request to update Agreement.id : " + id);
        AgreementDTO result = billingServiceFeignClient.reactivateAgreement(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/agreements/agreements-pageable")
    public Object getAllAgreementsPageable(AgreementFilter filter) {
        return billingServiceFeignClient.getAllAgreementsPageable(filter);
    }

    @GetMapping("/ledger-detail/tin/{companytin}/transactions")
    public ResponseEntity<Object> getLedgerDetailTransactions(@PathVariable String companytin, LedgerDetailFilter filter) {
        System.out.println("REST request Ledger transactions");
        return billingServiceFeignClient.getLedgerDetailTransactions(companytin, filter);
    }

    @GetMapping("/category/list-of-categories-by-current-app")
    public ResponseEntity<Object> listOfCategoriesByCurrentApp(BillingCategoryFilter filter) {
        return ResponseEntity.ok(billingServiceFeignClient.listOfCategoriesByCurrentApp(filter));
    }

    @GetMapping("/category/tree-of-categories-by-current-app")
    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfCategoriesByCurrentApp(BillingCategoryFilter filter) {
        return billingServiceFeignClient.treeOfCategoriesByCurrentApp(filter);
    }

    @GetMapping("/category/tree-of-active-categories-by-current-app")
    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfActiveCategoriesByCurrentApp(BillingCategoryFilter filter) {
        return billingServiceFeignClient.treeOfActiveCategoriesByCurrentApp(filter);
    }

    @GetMapping("/bonuses-pageable-by-manager/{id}")
    public ResponseEntity<Object> getBonusesListByManagerId(@PathVariable String id, BonusResponseFilter filter) {
        return billingServiceFeignClient.getBonusesListByManagerId(id, filter);
    }

    @GetMapping("/bonuses-pageable")
    public ResponseEntity<Object> getBonusesList(BonusResponseFilter filter) {
        return billingServiceFeignClient.getBonusesList(filter);
    }

    @GetMapping("/get-bonuses-amount")
    public ResponseEntity<Object> getBonusesAmount(BonusResponseFilter filter) {
        return billingServiceFeignClient.getBonusesAmount(filter);
    }

    @GetMapping("/start-agreement/{tin}")
    public ResponseEntity<Void> startAgreementWithTin(@PathVariable String tin) {
        return billingServiceFeignClient.startAgreementWithTin(tin);
    }

    @GetMapping("/billing-invoice-list")
    public ResponseEntity<Object> billingInvoiceList(BillingInvoiceFilter filter) {
        filter.setAppType(BillingAppType.SMARTPOS);
        return billingServiceFeignClient.billingInvoiceList(filter);
    }

    @GetMapping("/billing-xfile-status")
    public ResponseEntity<EnumSet<BillingXfileStatus>> getBillingXfileStatus() {
        return ResponseEntity.ok(EnumSet.allOf(BillingXfileStatus.class));
    }

    @GetMapping("/billing-contract-status")
    public ResponseEntity<EnumSet<BillingContractStatus>> getBillingContractStatus() {
        return ResponseEntity.ok(EnumSet.allOf(BillingContractStatus.class));
    }

    @GetMapping("/billing-invoice-amount")
    public ResponseEntity<BigDecimal> getBillingInvoiceAmount(BillingInvoiceFilter filter) {
        filter.setAppType(BillingAppType.SMARTPOS);
        return billingServiceFeignClient.getBillingInvoiceAmount(filter);
    }

    @GetMapping("/billing-invoice-customer-count")
    public ResponseEntity<Long> getBillingInvoiceCustomerCount(BillingInvoiceFilter filter) {
        return billingServiceFeignClient.getBillingInvoiceCustomerCount(filter);
    }

    @GetMapping("/billing-invoice/{id}/check-invoice-xfile-status")
    public ResponseEntity<Object> checkBillingInvoiceXfileStatus(@PathVariable String id) {
        return ResponseEntity.ok(billingServiceFeignClient.checkBillingInvoiceXfileStatus(id));
    }

    @GetMapping("/ledger-detail/grouped-transactions")
    public ResponseEntity<Object> getLedgerDetailGroupedTransactions(final LedgerDetailAppTypeFilter filter) {
        return billingServiceFeignClient.getLedgerDetailGroupedTransactions(filter);
    }

    @GetMapping("/agreements/show-calculation/xizmat/{xizmatId}/customer/{tin}")
    public ResponseEntity<BigDecimal> showCalculationByXizmatIdAndCustomerTin(@PathVariable Long xizmatId, @PathVariable String tin,
                                                                              @RequestParam(required = false, name = "info") String info,
                                                                              @RequestParam(name = "instance", required = false) Integer instance) {
        return billingServiceFeignClient.showCalculationByXizmatIdAndCustomerTin(xizmatId, tin, info, instance);
    }

    @GetMapping("/billing-customer/child-list-by-tin/{tin}")
    public Object getCustomerChildrenByTin(@PathVariable String tin) {
        return billingServiceFeignClient.getCustomerChildrenByTin(tin);
    }

    @GetMapping("/agreements/calculate-prepaid/{tin}/{parentChildEnum}")
    public ResponseEntity<BigDecimal> calculatePrepaid(@PathVariable String tin, @PathVariable String parentChildEnum) {
        return billingServiceFeignClient.calculatePrepaid(tin, parentChildEnum);
    }

    @PostMapping("/customer-public-offer/{id}/upload")
    public ResponseEntity<CustomerPublicOfferResponseDto> uploadFileToCustomerPublicOffer(@RequestParam("file") MultipartFile file, @PathVariable String id) {
        ResponseEntity<CustomerPublicOfferResponseDto> response;
        try {
            response = billingServiceFeignClient.uploadFileToCustomerPublicOffer(id, file);
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

    @GetMapping("/public-offer/get-last-active/document-type/{documentType}")
    public Object getLastActivePublicOffer(@PathVariable @NotNull BillingDocumentType documentType) {
        return ResponseEntity.ok(billingServiceFeignClient.getLastActiveOffer());
    }

    @GetMapping("/agreements/calculate-prepaid-list")
    public ResponseEntity<Object> calculatePrepaidList(@ModelAttribute ChildCalculatePrepaidRequestDto dto) {
        return billingServiceFeignClient.calculatePrepaidList(dto);
    }

    @GetMapping("/billing-invoice/get-by-agreement-id/{id}")
    public ResponseEntity<Object> getInvoiceByAgreementId(@PathVariable Long id, @ModelAttribute IntegrationBaseFilter filter) {
        return billingServiceFeignClient.getInvoiceByAgreementId(id, filter);
    }

    @GetMapping("/quote/agreement/{id}")
    public ResponseEntity<Object> getByAgreementId(@PathVariable Long id, @ModelAttribute IntegrationBaseFilter filter) {
        return billingServiceFeignClient.getByQuoteAgreementId(id, filter);
    }

    @PostMapping("/agreements/activate")
    public ResponseEntity<Void> activateAgreement(@RequestBody ActivateAgreementRequestDto activateAgreementRequestDto) {
        return billingServiceFeignClient.activateAgreement(activateAgreementRequestDto);
    }

    @GetMapping("/xizmats/{id}/get-children")
    public ResponseEntity<Object> getXizmatsChildren(@PathVariable Long id) {
        return billingServiceFeignClient.getXizmatsChildren(id);
    }

    @PostMapping("/quote/create")
    public ResponseEntity<Object> createQuote(@RequestBody QuoteCreateRequestDto quoteCreateRequestDto) {
        return billingServiceFeignClient.createQuote(quoteCreateRequestDto);
    }
}
