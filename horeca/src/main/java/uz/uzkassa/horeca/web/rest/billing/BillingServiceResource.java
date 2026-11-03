package uz.uzkassa.horeca.web.rest.billing;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.apay.APayAddTransactionResponseDto;
import uz.uzkassa.horeca.dto.apay.APayPaymentAddRequestDto;
import uz.uzkassa.horeca.dto.apay.APayPaymentPayRequestDto;
import uz.uzkassa.horeca.dto.billing.BonusResponseFilter;
import uz.uzkassa.horeca.dto.billing.agreement.ActivateAgreementRequestDto;
import uz.uzkassa.horeca.dto.billing.agreement.AgreementDTO;
import uz.uzkassa.horeca.dto.billing.agreement.CreateAgreementsDTO;
import uz.uzkassa.horeca.dto.billing.agreement.XizmatFilter;
import uz.uzkassa.horeca.dto.billing.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.horeca.dto.billing.quote.QuoteResponseDto;
import uz.uzkassa.horeca.enums.billing.AgreementStatus;
import uz.uzkassa.horeca.enums.billing.AppType;
import uz.uzkassa.horeca.enums.billing.BillingEntityType;
import uz.uzkassa.horeca.enums.billing.BillingXfileStatus;
import uz.uzkassa.horeca.filters.billing.*;
import uz.uzkassa.horeca.integration.BillingServiceClient;
import uz.uzkassa.horeca.service.BranchService;
import uz.uzkassa.horeca.utils.HeaderUtils;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetBillingServicesRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BillingServiceResource extends BillingBaseResource {

    BillingServiceClient billingServiceClient;
    BranchService branchService;

    @GetMapping("/xizmats")
    public Object getBillingXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(branchService.getActiveBranches(filter.getCompanyInn()).intValue());
        return billingServiceClient.getXizmats(filter);
    }

    @GetMapping("/xizmats/grouped")
    public Object getGroupedXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(branchService.getActiveBranches(filter.getCompanyInn()).intValue());
        filter.setDisplayInCabinet(true);
        return billingServiceClient.getGroupedXizmats(filter);
    }

    @GetMapping("/xizmats/single")
    public Object getSingleXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(branchService.getActiveBranches(filter.getCompanyInn()).intValue());
        filter.setDisplayInCabinet(true);
        return billingServiceClient.getSingleXizmats(filter);
    }

    @GetMapping("/agreements")
    public Object getBillingAgreements(@ModelAttribute AgreementFilter filter) {
        filter.setInn(HeaderUtils.getCurrentCompanyTIN());
        return billingServiceClient.getAgreements(filter);
    }

    @GetMapping("/agreements/{id}")
//    @AuthValidate(name = "id", authEnum = AuthEnum.AGREEMENT_ID)
    public Object getBillingAgreements(@PathVariable("id") Long id) {
        return billingServiceClient.getAgreementById(id);
    }

    @PatchMapping("/agreements/activate/{agreementId}")
    public Object activateBillingAgreement(@PathVariable Long agreementId) {
        return billingServiceClient.activateAgreement(agreementId);
    }

//    @PostMapping("/agreements")
//    public Object saveBillingAgreement(@RequestBody CreateAgreementDTO dto) {
//        return customerService.saveBillingAgreement(dto);
//    }

    @PostMapping("/multi-agreements")
    public ResponseEntity<Object> saveBillingAgreements(@RequestBody CreateAgreementsDTO dto) {
        try {
            dto.setCompanyInn(HeaderUtils.getCurrentCompanyTIN());
            return ResponseEntity.ok(billingServiceClient.saveAgreement(dto));
        } catch (Exception e) {
            throw getException(e);
        }
    }

    @PostMapping("/agreements/{id}/update-note")
    public ResponseEntity<AgreementDTO> updateNote(@PathVariable("id") Long id, @RequestBody String note) {
        return ResponseEntity.ok(billingServiceClient.updateAgreementNote(id, note));
    }

    @Operation(summary = "upload file")
    @PostMapping(value = "/attachment/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("relatedTo") BillingEntityType relatedTo,
            @RequestParam("relatedToId") String relatedToId
    ) {
        return billingServiceClient.upload(relatedTo, relatedToId, file);
    }

    @PutMapping("/agreements/{id}/pause")
    public ResponseEntity<AgreementDTO> pauseAgreement(@PathVariable Long id) {
        return ResponseEntity.ok().body(billingServiceClient.pauseAgreement(id));
    }

    @PutMapping("/agreements/{id}/cancel")
    public ResponseEntity<AgreementDTO> cancelAgreement(@PathVariable Long id) {
        return ResponseEntity.ok().body(billingServiceClient.cancelAgreement(id));
    }

    @PutMapping("/agreements/{id}/activate")
    public ResponseEntity<AgreementDTO> reactivateAgreement(@PathVariable Long id) {
        return ResponseEntity.ok().body(billingServiceClient.reactivateAgreement(id));
    }

//    @GetMapping("/agreements/agreements-pageable")
//    public ResponseEntity<Object> getAllAgreementsPageable(@ParameterObject AgreementFilter filter) {
//        return companyService.getAllAgreementsPageable(filter);
//    }

    @PostMapping("/agreements/activate")
    public ResponseEntity<Void> activateAgreement(@RequestBody ActivateAgreementRequestDto activateAgreementRequestDto) {
        billingServiceClient.activateAgreement(activateAgreementRequestDto.getAgreementId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ledger-detail/tin/{companyTin}/transactions")
    public ResponseEntity<Object> getledgerDetailTransactions(@PathVariable String companyTin, BillingPageFilter filter) {
        return ResponseEntity.ok(billingServiceClient.getLedgerDetailTransactions(companyTin, filter));
    }

    @GetMapping("/category/list-of-categories-by-current-app")
    public ResponseEntity<Object> listOfCategoriesByCurrentApp(@ParameterObject CategoryFilter filter) {
        return ResponseEntity.ok(billingServiceClient.listOfCategoriesByCurrentApp(filter));
    }

    @GetMapping("/category/tree-of-categories-by-current-app")
    public ResponseEntity<Object> treeOfCategoriesByCurrentApp(@ParameterObject CategoryFilter filter) {
        return ResponseEntity.ok(billingServiceClient.treeOfCategoriesByCurrentApp(filter));
    }

    @GetMapping("/category/tree-of-active-categories-by-current-app")
    public ResponseEntity<Object> treeOfActiveCategoriesByCurrentApp(@ParameterObject CategoryFilter filter) {
        return ResponseEntity.ok(billingServiceClient.treeOfActiveCategoriesByCurrentApp(filter));
    }

    @PostMapping("/a-pay-payments/create-transaction")
    public ResponseEntity<APayAddTransactionResponseDto> createTransaction(@RequestBody APayPaymentAddRequestDto dto) {
        return ResponseEntity.ok(billingServiceClient.createTransaction(dto));
    }

    @PostMapping("/a-pay-payments/confirm-transaction")
    public ResponseEntity<APayAddTransactionResponseDto> acceptPayment(@RequestBody APayPaymentPayRequestDto dto) {
        billingServiceClient.acceptPayment(dto);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/bonuses-pageable")
//    public ResponseEntity<Object> getBonusesList(@ParameterObject BonusResponseFilter filter) {
//        return userService.getBonusesList(filter);
//    }

    @GetMapping("/get-bonuses-amount")
    public ResponseEntity<BigDecimal> getBonusesAmount(@ParameterObject BonusResponseFilter filter) {
        return ResponseEntity.ok(billingServiceClient.getBonusesAmount(filter));
    }

    @GetMapping("/get-invoice-amount-by-bonus")
    public ResponseEntity<BigDecimal> getInvoiceAmountByBonus(@ParameterObject BonusResponseFilter filter) {
        return ResponseEntity.ok(billingServiceClient.getInvoiceAmountByBonus(filter));
    }

    @GetMapping("/get-all-invoice-amount-by-bonusfilter")
    public ResponseEntity<BigDecimal> getAllInvoiceAmountByBonusFilter(@ParameterObject BonusResponseFilter filter) {
        return ResponseEntity.ok(billingServiceClient.getAllInvoiceAmountByBonusFilter(filter));
    }

    @GetMapping("/get-all-invoice-count-by-bonusfilter")
    public ResponseEntity<Long> getAllInvoiceCountByBonusFilter(@ParameterObject BonusResponseFilter filter) {
        return ResponseEntity.ok(billingServiceClient.getAllInvoiceCountByBonusFilter(filter));
    }

    @GetMapping("/get-all-customer-count-by-bonusfilter")
    public ResponseEntity<Long> getAllCompanyCountByBonusFilter(@ParameterObject BonusResponseFilter filter) {
        return ResponseEntity.ok(billingServiceClient.getAllCompanyCountByBonusFilter(filter));
    }

    @GetMapping("/start-agreement/{tin}")
    public ResponseEntity<Void> startAgreementWithTin(@PathVariable String tin) {
        billingServiceClient.startAgreementWithTin(tin);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/agreements/statuses")
    public ResponseEntity<List<AgreementStatus>> getAgreementStatuses() {
        return ResponseEntity.ok().body(AgreementStatus.asList());
    }

    @GetMapping("/billing-invoice-list")
    public ResponseEntity<Object> billingInvoiceList(@ParameterObject BillingInvoiceFilter filter) {
        filter.setAppType(AppType.SUPPLIER);
        return ResponseEntity.ok(billingServiceClient.billingInvoiceList(filter));
    }

    @GetMapping("/billing-xfile-status")
    public ResponseEntity<EnumSet<BillingXfileStatus>> getBillingXfileStatus() {
        return ResponseEntity.ok().body(EnumSet.allOf(BillingXfileStatus.class));
    }

    @GetMapping("/billing-contract-status")
    public ResponseEntity<EnumSet<BillingContractStatus>> getBillingContractStatus() {
        return ResponseEntity.ok().body(EnumSet.allOf(BillingContractStatus.class));
    }

    @GetMapping("/billing-invoice-amount")
    public ResponseEntity<BigDecimal> getBillingInvoiceAmount(@ParameterObject BillingInvoiceFilter filter) {
        filter.setAppType(AppType.SUPPLIER);
        return ResponseEntity.ok(billingServiceClient.getBillingInvoiceAmount(filter));
    }

    @GetMapping("/billing-invoice-customer-count")
    public ResponseEntity<Long> getBillingInvoiceCustomerCount(@ParameterObject BillingInvoiceFilter filter) {
        return ResponseEntity.ok(billingServiceClient.getBillingInvoiceCustomerCount(filter));
    }

    @GetMapping("/billing-invoice/{id}/check-invoice-xfile-status")
    public ResponseEntity<Object> checkBillingInvoiceXfileStatus(@PathVariable String id) {
        return ResponseEntity.ok(billingServiceClient.checkBillingInvoiceXfileStatus(id));
    }

    @GetMapping("/active-branches/{tin}")
    public ResponseEntity<Long> getActiveBranchesCount(@PathVariable String tin) {
        return ResponseEntity.ok(branchService.getActiveBranches(tin));
    }

    @GetMapping("/agreements/show-calculation/xizmat/{xizmatId}/customer/{tin}")
    public ResponseEntity<BigDecimal> showCalculationByXizmatIdAndCustomerTin(@PathVariable Long xizmatId, @PathVariable String tin, @RequestParam(required = false, name = "info") String info, @RequestParam(name = "instance", required = false) Integer instance) {
        return ResponseEntity.ok(billingServiceClient.showCalculationByXizmatIdAndCustomerTin(xizmatId, tin, info, instance));
    }

    @GetMapping("/billing-services/customer-public-offer/tin/{tin}")
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByTin(@PathVariable @NotEmpty @NotNull String tin) {
        try {
            return ResponseEntity.ok(billingServiceClient.getCustomerOfferByTin(tin));
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponseDto> getById(@PathVariable("id") @NotEmpty @NotNull String id) {
        return ResponseEntity.ok(billingServiceClient.getByQuoteId(id));
    }
}
