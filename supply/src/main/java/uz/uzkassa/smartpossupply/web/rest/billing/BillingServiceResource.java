package uz.uzkassa.smartpossupply.web.rest.billing;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.aop.auth.AuthEnum;
import uz.uzkassa.smartpossupply.aop.auth.AuthValidate;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.billingpermission.BillingCompanyItem;
import uz.uzkassa.smartpossupply.dto.employee.EmployeeCountByRoleDTO;
import uz.uzkassa.smartpossupply.dto.integration.BillingCategoryTreeDto;
import uz.uzkassa.smartpossupply.dto.integration.BonusResponseFilter;
import uz.uzkassa.smartpossupply.dto.integration.agreement.AgreementDTO;
import uz.uzkassa.smartpossupply.dto.integration.agreement.CreateAgreementDTO;
import uz.uzkassa.smartpossupply.dto.integration.agreement.CreateAgreementsDTO;
import uz.uzkassa.smartpossupply.dto.integration.agreement.XizmatFilter;
import uz.uzkassa.smartpossupply.dto.integration.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.smartpossupply.dto.integration.quote.QuoteResponseDto;
import uz.uzkassa.smartpossupply.enums.*;
import uz.uzkassa.smartpossupply.filters.AgreementFilter;
import uz.uzkassa.smartpossupply.filters.BillingInvoiceFilter;
import uz.uzkassa.smartpossupply.filters.CategoryFilter;
import uz.uzkassa.smartpossupply.filters.LedgerDetailAppTypeFilter;
import uz.uzkassa.smartpossupply.integration.apay.request.APayPaymentAddRequestDto;
import uz.uzkassa.smartpossupply.integration.apay.request.APayPaymentPayRequestDto;
import uz.uzkassa.smartpossupply.integration.apay.response.APayAddTransactionResponseDto;
import uz.uzkassa.smartpossupply.integration.billing.BillingServiceFeignClient;
import uz.uzkassa.smartpossupply.integration.filter.BillingPageFilter;
import uz.uzkassa.smartpossupply.service.*;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetBillingServicesRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BillingServiceResource extends BillingBaseResource {

    BillingServiceFeignClient billingServiceFeignClient;
    ApplicationProperties applicationProperties;
    BranchService branchService;
    UserService userService;
    CompanyService companyService;
    CustomerService customerService;
    EmployeeService employeeService;

    private String getToken() {
        return billingServiceFeignClient.getToken(
                applicationProperties.getBilling().getUsername(),
                applicationProperties.getBilling().getPassword()
        );
    }

    @GetMapping("/xizmats")
    public Object getBillingXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(branchService.getCountByStatus(filter.getCompanyInn()).intValue());
        return billingServiceFeignClient.getXizmats(getToken(), filter);
    }

    @GetMapping("/xizmats/grouped")
    public Object getGroupedXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(branchService.getCountByStatus(filter.getCompanyInn()).intValue());
        filter.setDisplayInCabinet(true);
        return billingServiceFeignClient.getGroupedXizmats(getToken(), filter);
    }

    @GetMapping("/xizmats/single")
    public Object getSingleXizmats(@ModelAttribute XizmatFilter filter) {
        filter.setBranchInstance(branchService.getCountByStatus(filter.getCompanyInn()).intValue());
        filter.setDisplayInCabinet(true);
        return billingServiceFeignClient.getSingleXizmats(getToken(), filter);
    }

    @GetMapping("/agreements")
    public Object getBillingAgreements(@ModelAttribute AgreementFilter filter) {
        filter.setInn(SecurityUtils.getCurrentCompanyTin());
        return billingServiceFeignClient.getAgreements(getToken(), filter);
    }

    @GetMapping("/agreements/{id}")
    @AuthValidate(name = "id", authEnum = AuthEnum.AGREEMENT_ID)
    public Object getBillingAgreements(@PathVariable("id") Long id) {
        return billingServiceFeignClient.getAgreementById(getToken(), id);
    }

    @PatchMapping("/agreements/activate/{agreementId}")
    public Object activateBillingAgreement(@PathVariable Long agreementId) {
        return billingServiceFeignClient.activateAgreement(getToken(), agreementId);
    }

    @PostMapping("/agreements")
    public Object saveBillingAgreement(@RequestBody CreateAgreementDTO dto) {
        return customerService.saveBillingAgreement(getToken(), dto);
    }

    @PostMapping("/agreements-add-extra-terminals")
    public ResponseEntity<Object> addAgreementExtraTerminals(@Valid @RequestBody CreateAgreementDTO dto) {
        return ResponseEntity.ok(customerService.addAgreementExtraTerminals(getToken(), dto));
    }

    @PostMapping("/multi-agreements")
    public ResponseEntity<Object> saveBillingAgreements(@RequestBody CreateAgreementsDTO dto) {
        dto.setCompanyInn(SecurityUtils.getCurrentCompanyTin());
        try {
            return billingServiceFeignClient.saveAgreement(getToken(), dto);
        } catch (Exception e) {
            throw getException(e);
        }
    }

    @PostMapping("/agreements/{id}/update-note")
    public ResponseEntity<AgreementDTO> updateNote(@PathVariable("id") Long id, @RequestBody String note) {
        return billingServiceFeignClient.updateAgreementNote(getToken(), id, note);
    }

    @Operation(summary = "upload file")
    @PostMapping(value = "/attachment/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("relatedTo") BillingEntityType relatedTo,
            @RequestParam("relatedToId") String relatedToId
    ) {
        return billingServiceFeignClient.upload(getToken(), relatedTo, relatedToId, file);
    }

    @PutMapping("/agreements/{id}/pause")
    public ResponseEntity<AgreementDTO> pauseAgreement(@PathVariable Long id) {
        return ResponseEntity.ok(billingServiceFeignClient.pauseAgreement(getToken(), id));
    }

    @PutMapping("/agreements/{id}/cancel")
    public ResponseEntity<AgreementDTO> cancelAgreement(@PathVariable Long id) {
        return ResponseEntity.ok(billingServiceFeignClient.cancelAgreement(getToken(), id));
    }

    @PutMapping("/agreements/{id}/activate")
    public ResponseEntity<AgreementDTO> reactivateAgreement(@PathVariable Long id) {
        return ResponseEntity.ok(billingServiceFeignClient.reactivateAgreement(getToken(), id));
    }

    @GetMapping("/agreements/agreements-pageable")
    public ResponseEntity<Object> getAllAgreementsPageable(@ParameterObject AgreementFilter filter) {
        return companyService.getAllAgreementsPageable(getToken(), filter);
    }

    @GetMapping("/ledger-detail/tin/{companyTin}/transactions")
    public ResponseEntity<Object> getledgerDetailTransactions(@PathVariable String companyTin, BillingPageFilter filter) {
        return billingServiceFeignClient.getLedgerDetailTransactions(getToken(), companyTin, filter);
    }

    @GetMapping("/category/list-of-categories-by-current-app")
    public ResponseEntity<Object> listOfCategoriesByCurrentApp(@ParameterObject CategoryFilter filter) {
        return ResponseEntity.ok(billingServiceFeignClient.listOfCategoriesByCurrentApp(getToken(), filter));
    }

    @GetMapping("/category/tree-of-categories-by-current-app")
    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfCategoriesByCurrentApp(@ParameterObject CategoryFilter filter) {
        return billingServiceFeignClient.treeOfCategoriesByCurrentApp(getToken(), filter);
    }

    @GetMapping("/category/tree-of-active-categories-by-current-app")
    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfActiveCategoriesByCurrentApp(@ParameterObject CategoryFilter filter) {
        return billingServiceFeignClient.treeOfActiveCategoriesByCurrentApp(getToken(), filter);
    }

    @PostMapping("/a-pay-payments/create-transaction")
    public ResponseEntity<APayAddTransactionResponseDto> createTransaction(@RequestBody APayPaymentAddRequestDto dto) {
        return ResponseEntity.ok(billingServiceFeignClient.createTransaction(getToken(), dto));
    }

    @PostMapping("/a-pay-payments/confirm-transaction")
    public ResponseEntity<APayAddTransactionResponseDto> acceptPayment(@RequestBody APayPaymentPayRequestDto dto) {
        billingServiceFeignClient.acceptPayment(getToken(), dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bonuses-pageable")
    public ResponseEntity<Object> getBonusesList(@ParameterObject BonusResponseFilter filter) {
        return userService.getBonusesList(getToken(), filter);
    }

    @GetMapping("/get-bonuses-amount")
    public ResponseEntity<BigDecimal> getBonusesAmount(@ParameterObject BonusResponseFilter filter) {
        return billingServiceFeignClient.getBonusesAmount(getToken(), filter);
    }

    @GetMapping("/get-invoice-amount-by-bonus")
    public ResponseEntity<BigDecimal> getInvoiceAmountByBonus(@ParameterObject BonusResponseFilter filter) {
        return billingServiceFeignClient.getInvoiceAmountByBonus(getToken(), filter);
    }

    @GetMapping("/get-all-invoice-amount-by-bonusfilter")
    public ResponseEntity<BigDecimal> getAllInvoiceAmountByBonusFilter(@ParameterObject BonusResponseFilter filter) {
        return billingServiceFeignClient.getAllInvoiceAmountByBonusFilter(getToken(), filter);
    }

    @GetMapping("/get-all-invoice-count-by-bonusfilter")
    public ResponseEntity<Long> getAllInvoiceCountByBonusFilter(@ParameterObject BonusResponseFilter filter) {
        return billingServiceFeignClient.getAllInvoiceCountByBonusFilter(getToken(), filter);
    }

    @GetMapping("/get-all-customer-count-by-bonusfilter")
    public ResponseEntity<Long> getAllCompanyCountByBonusFilter(@ParameterObject BonusResponseFilter filter) {
        return billingServiceFeignClient.getAllCompanyCountByBonusFilter(getToken(), filter);
    }

    @GetMapping("/start-agreement/{tin}")
    public ResponseEntity<Void> startAgreementWithTin(@PathVariable String tin) {
        return billingServiceFeignClient.startAgreementWithTin(getToken(), tin);
    }

    @GetMapping("/agreements/statuses")
    public ResponseEntity<List<AgreementStatus>> getAgreementStatuses() {
        return ResponseEntity.ok(AgreementStatus.asList());
    }

    @GetMapping("/ledger-detail/grouped-transactions")
    public ResponseEntity<Object> getledgerDetailGruppedTransactions(@ParameterObject LedgerDetailAppTypeFilter filter) {
        return companyService.getledgerDetailGruppedTransactions(getToken(), filter);
    }

    @GetMapping("/billing-invoice-list")
    public ResponseEntity<Object> billingInvoiceList(@ParameterObject BillingInvoiceFilter filter) {
        filter.setAppType(AppType.SUPPLIER);
        return billingServiceFeignClient.billingInvoiceList(getToken(), filter);
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
    public ResponseEntity<BigDecimal> getBillingInvoiceAmount(@ParameterObject BillingInvoiceFilter filter) {
        filter.setAppType(AppType.SUPPLIER);
        return billingServiceFeignClient.getBillingInvoiceAmount(getToken(), filter);
    }

    @GetMapping("/billing-invoice-customer-count")
    public ResponseEntity<Long> getBillingInvoiceCustomerCount(@ParameterObject BillingInvoiceFilter filter) {
        return billingServiceFeignClient.getBillingInvoiceCustomerCount(getToken(), filter);
    }

    @GetMapping("/billing-invoice/{id}/check-invoice-xfile-status")
    public ResponseEntity<Object> checkBillingInvoiceXfileStatus(@PathVariable String id) {
        return ResponseEntity.ok(billingServiceFeignClient.checkBillingInvoiceXfileStatus(getToken(), id));
    }

    @GetMapping("/active-branches")
    public ResponseEntity<Long> getActiveBranchesCount(@RequestParam(name = "tin") String tin,
                                                       @RequestParam(name = "id", required = false) Long companyId) {
        return ResponseEntity.ok(branchService.getActiveBranchesCount(tin, companyId));
    }

    @GetMapping("/employee-count-by-role")
    public ResponseEntity<List<EmployeeCountByRoleDTO>> getEmployeeCountByRole(@RequestParam(name = "tin") String tin,
                                                                               @RequestParam(name = "id", required = false) Long companyId) {
        return ResponseEntity.ok(employeeService.getCountByRole(tin, companyId));
    }

    @GetMapping("/agreements/show-calculation/xizmat/{xizmatId}/customer/{tin}")
    public ResponseEntity<Object> showCalculationByXizmatIdAndCustomerTin(@PathVariable Long xizmatId, @PathVariable String tin,
                                                                          @RequestParam(required = false, name = "info") String info,
                                                                          @RequestParam(name = "instance", required = false) Integer instance,
                                                                          @RequestParam(name = "parentAgreementId", required = false) Long parentAgreementId,
                                                                          @RequestParam(name = "idForInstanceCheck", required = false) String idForInstanceCheck,
                                                                          @RequestParam(name = "tinForInstanceCheck", required = false) String tinForInstanceCheck
    ) {
        return billingServiceFeignClient.showCalculationByXizmatIdAndCustomerTin(getToken(), xizmatId, tin, info, instance, parentAgreementId, idForInstanceCheck, tinForInstanceCheck);
    }

    @GetMapping("/billing-services/customer-public-offer/tin/{tin}")
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByTin(@PathVariable @NotEmpty @NotNull String tin) {
        try {
            return billingServiceFeignClient.getCustomerOfferByTin(getToken(), tin);
        } catch (Exception ex) {
            throw getException(ex);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponseDto> getById(@PathVariable("id") @NotEmpty @NotNull Long id) {
        return billingServiceFeignClient.getByQuoteId(getToken(), id);
    }

    @GetMapping("/company-items")
    public ResponseEntity<List<BillingCompanyItem>> getCompanyItems(@RequestParam(name = "tin") String tin,
                                                                    @RequestParam(name = "id", required = false) Long companyId) {
        return ResponseEntity.ok(employeeService.getCompanyItems(tin, companyId));
    }
}
