package uz.uzkassa.smartpos.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.constants.TokenType;
import uz.uzkassa.smartpos.dto.UserDTO;
import uz.uzkassa.smartpos.dto.filter.AgreementFilter;
import uz.uzkassa.smartpos.dto.filter.BillingCategoryFilter;
import uz.uzkassa.smartpos.dto.filter.BillingPageFilter;
import uz.uzkassa.smartpos.dto.filter.OrganizationApplicationFormFilter;
import uz.uzkassa.smartpos.dto.integration.BillingCategoryTreeDto;
import uz.uzkassa.smartpos.dto.integration.agreement.AgreementDTO;
import uz.uzkassa.smartpos.dto.integration.agreement.CreateAgreementDTO;
import uz.uzkassa.smartpos.dto.integration.agreement.CreateAgreementsDTO;
import uz.uzkassa.smartpos.dto.integration.agreement.XizmatFilter;
import uz.uzkassa.smartpos.dto.integration.billingapay.ActivateAgreementRequestDto;
import uz.uzkassa.smartpos.dto.integration.billingapay.ApayPromotionSubscriberCreateRequestDto;
import uz.uzkassa.smartpos.dto.integration.billingapay.ApayTransactionRequestDto;
import uz.uzkassa.smartpos.dto.integration.customerpublicoffer.CustomerPublicOfferCtoIdRequestDto;
import uz.uzkassa.smartpos.dto.integration.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.smartpos.dto.integration.enums.BillingEntityType;
import uz.uzkassa.smartpos.dto.integration.enums.OrganizationApplicationFormStatus;
import uz.uzkassa.smartpos.dto.integration.organizationapplicationform.OrganizationApplicationFormArchiveRequestDto;
import uz.uzkassa.smartpos.dto.integration.organizationapplicationform.OrganizationApplicationFormCreateRequestDto;
import uz.uzkassa.smartpos.dto.integration.organizationapplicationform.OrganizationApplicationFormUpdateRequestDto;
import uz.uzkassa.smartpos.dto.integration.promocode.PromoCodeApplyDto;
import uz.uzkassa.smartpos.dto.integration.publicoffer.PublicOfferResponseDto;
import uz.uzkassa.smartpos.dto.integration.quote.ContractStatus;
import uz.uzkassa.smartpos.dto.integration.referrallink.ReferralLinkDTO;
import uz.uzkassa.smartpos.enums.AppType;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.integration.billing.*;
import uz.uzkassa.smartpos.integration.enums.BillingXfileStatus;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.service.*;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/billing-services")
public class BillingServiceResource {
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final UserService userService;
    private final LocalizationService localizationService;
    private final CompanyService companyService;
    private final BillingService billingService;
    private final TokenStoreService tokenStoreService;

    @GetMapping("/xizmats")
    public Object getBillingXizmats(@ParameterObject XizmatFilter filter) {
        filter.setDisabled(false);
        return billingServiceFeignClient.getXizmats(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/xizmats/all")
    public Object getAllBillingXizmats() {
        return billingServiceFeignClient.getXizmats(tokenStoreService.getToken(TokenType.BILLING), new XizmatFilter());
    }

    @GetMapping("/xizmats/grouped")
    public Object getGroupedXizmats(@ParameterObject XizmatFilter filter) {
        filter.setDisplayInCabinet(true);
        return billingServiceFeignClient.getGroupedXizmats(tokenStoreService.getToken(TokenType.BILLING), filter); /**/
    }

    @GetMapping("/xizmats/{id}/get-children")
    public Object getXizmatsChildren(@PathVariable Long id) {
        return billingServiceFeignClient.getXizmatsChildren(tokenStoreService.getToken(TokenType.BILLING), id);
    }

    @GetMapping("/xizmats/single")
    public Object getSingleXizmats(@ParameterObject XizmatFilter filter) {
        filter.setDisplayInCabinet(true);
        return billingServiceFeignClient.getSingleXizmats(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/xizmats/grouped/all")
    public Object getAllGroupedXizmats() {
        return billingServiceFeignClient.getGroupedXizmats(tokenStoreService.getToken(TokenType.BILLING), new XizmatFilter());
    }

    @GetMapping("/xizmats/single/all")
    public Object getAllSingleXizmats() {
        return billingServiceFeignClient.getSingleXizmats(tokenStoreService.getToken(TokenType.BILLING), new XizmatFilter());
    }

    @Deprecated
    @GetMapping("/agreements")
    public Object getBillingAgreements(@ParameterObject AgreementFilter filter) {
        filter.setInn(userService.getCurrentUsersCompanyTin());
        return billingServiceFeignClient.getAgreements(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/agreements/agreements-pageable")
    public Object getAllAgreementsPageable(@ParameterObject AgreementFilter filter) {
        if (filter.getCompanyId() != null) {
            filter.setInn(companyService.getCompanyById(filter.getCompanyId()).getInn());
            filter.setCompanyId(null);
        }
        return billingServiceFeignClient.getAllAgreementsPageable(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/agreements/{id}")
    public Object getBillingAgreements(@PathVariable("id") Long id) {
        return billingServiceFeignClient.getAgreementById(tokenStoreService.getToken(TokenType.BILLING), id);
    }

    @PostMapping("/agreements")
    public Object saveBillingAgreement(@RequestBody CreateAgreementDTO dto) {
        if (StringUtils.isEmpty(dto.getInn())) {
            dto.setInn(userService.getCurrentUsersCompanyTin());
        }
        companyService.setManagerData(dto);
        return billingServiceFeignClient.saveAgreement(tokenStoreService.getToken(TokenType.BILLING), dto);
    }

    @PostMapping("/multi-agreements")
    public ResponseEntity<Object> saveBillingAgreements(@RequestBody CreateAgreementsDTO dto) {
        dto.setCompanyInn(userService.getCurrentUsersCompanyTin());
        try {

            UserDTO userDTO = userService.getUser(SecurityUtils.getCurrentUserId());
            if (userDTO.getFullName() != null) {
                dto.setFirstName(userDTO.getFullName().getFirstName());
                dto.setLastName(userDTO.getFullName().getLastName());
                dto.setPatronymic(userDTO.getFullName().getPatronymic());
            }
            dto.setLogin(userDTO.getLogin());
            dto.setSmartposCreatorId(userDTO.getId());
            return billingServiceFeignClient.saveAgreement(tokenStoreService.getToken(TokenType.BILLING), dto);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(localizationService.getMessage("error.undefined.occurred"));
        }
    }

    @PostMapping("/agreements/{id}/update-note")
    public ResponseEntity<AgreementDTO> updateNote(@PathVariable("id") Long id, @RequestBody String note) {
        return billingServiceFeignClient.updateAgreementNote(tokenStoreService.getToken(TokenType.BILLING), id, note);
    }

    @PostMapping(value = "/attachment/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Object uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("relatedTo") BillingEntityType relatedTo, @RequestParam("relatedToId") String relatedToId) {
        return billingServiceFeignClient.upload(tokenStoreService.getToken(TokenType.BILLING), relatedTo, relatedToId, file);
    }


    /**
     * {@code PUT  /agreements/{id}/pause} : Updates an existing agreement.
     *
     * @param id the id of the agreement to pause.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agreementDTO,
     * or with status {@code 400 (Bad Request)} if the agreementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the agreementDTO couldn't be updated.
     */
    @PutMapping("/agreements/{id}/pause")
    public ResponseEntity<AgreementDTO> pauseAgreement(@PathVariable Long id) {
        System.out.println("REST request to update Agreement.id : " + id);
        AgreementDTO result = billingServiceFeignClient.pauseAgreement(tokenStoreService.getToken(TokenType.BILLING), id);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/agreements/activate")
    public Object activatedAgreement(@RequestBody ActivateAgreementRequestDto dto) {
        System.out.println("REST request to activate Agreement.id : " + dto.getAgreementId());
        return billingServiceFeignClient.activateAgreement(tokenStoreService.getToken(TokenType.BILLING), dto);
    }


    /**
     * {@code PUT  /agreements/{id}/cancel} : Updates an existing agreement.
     *
     * @param id the id of the agreement to cancel.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agreementDTO,
     * or with status {@code 400 (Bad Request)} if the agreementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the agreementDTO couldn't be updated.
     */
    @PutMapping("/agreements/{id}/cancel")
    public ResponseEntity<AgreementDTO> cancelAgreement(@PathVariable Long id) {
        System.out.println("REST request to update Agreement.id : " + id);
        AgreementDTO result = billingServiceFeignClient.cancelAgreement(tokenStoreService.getToken(TokenType.BILLING), id);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/ledger-detail/tin/{companytin}/transactions")
    public ResponseEntity<Object> getledgerDetailTransactions(@PathVariable String companytin, @ParameterObject BillingPageFilter filter) {
        System.out.println("REST request Ledger transactions");
        return billingServiceFeignClient.getledgerDetailTransactions(tokenStoreService.getToken(TokenType.BILLING), companytin, filter);
    }

    @GetMapping("/category/list-of-categories-by-current-app")
    public ResponseEntity<Object> listOfCategoriesByCurrentApp(@ParameterObject BillingCategoryFilter filter) {
        return ResponseEntity.ok(billingServiceFeignClient.listOfCategoriesByCurrentApp(tokenStoreService.getToken(TokenType.BILLING), filter));
    }

    @GetMapping("/category/tree-of-categories-by-current-app")
    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfCategoriesByCurrentApp(@ParameterObject BillingCategoryFilter filter) {
        return billingServiceFeignClient.treeOfCategoriesByCurrentApp(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/category/tree-of-active-categories-by-current-app")
    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfActiveCategoriesByCurrentApp(@ParameterObject BillingCategoryFilter filter) {
        return billingServiceFeignClient.treeOfActiveCategoriesByCurrentApp(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/balance/tin/{tin}")
    public ResponseEntity<BigDecimal> getBalanceByCtoId(@PathVariable @NotEmpty @NotNull String tin) {
        return billingServiceFeignClient.getBalanceByTin(tokenStoreService.getToken(TokenType.BILLING), tin);
    }

    @GetMapping("/start-agreement/{tin}")
    public ResponseEntity<Void> startAgreementWithTin(@PathVariable String tin) {
        return billingServiceFeignClient.startAgreementWithTin(tokenStoreService.getToken(TokenType.BILLING), tin);
    }

    @GetMapping("/customer-public-offer/tin/{tin}")
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByTin(@PathVariable @NotEmpty @NotNull String tin) {
        return billingServiceFeignClient.getCustomerOfferByTin(tokenStoreService.getToken(TokenType.BILLING), tin);
    }


    @GetMapping("/get-last-active-offer")
    public ResponseEntity<PublicOfferResponseDto> getLastActiveOffer() {
        return billingServiceFeignClient.getLastActiveOffer(tokenStoreService.getToken(TokenType.BILLING));
    }

    @PostMapping("/customer-public-offer/create-by-tin")
    public ResponseEntity<CustomerPublicOfferResponseDto> createCustomerOffer(@Valid @RequestBody CustomerPublicOfferCtoIdRequestDto dto) {
        return ResponseEntity.ok(billingService.createCustomerOffer(dto));
    }

    @GetMapping("/billing-invoice-list")
    public ResponseEntity<Object> billingInvoiceList(@ParameterObject BillingInvoiceFilter filter) {
        filter.setAppType(AppType.SMARTPOS);
        return billingServiceFeignClient.billingInvoiceList(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/billing-xfile-status")
    public ResponseEntity<EnumSet<BillingXfileStatus>> getBillingXfileStatus() {
        return ResponseEntity.ok().body(EnumSet.allOf(BillingXfileStatus.class));
    }

    @GetMapping("/billing-contract-status")
    public ResponseEntity<EnumSet<ContractStatus>> getBillingContractStatus() {
        return ResponseEntity.ok().body(EnumSet.allOf(ContractStatus.class));
    }

    @GetMapping("/billing-invoice-amount")
    public ResponseEntity<BigDecimal> getBillingInvoiceAmount(@ParameterObject BillingInvoiceFilter filter) {
        filter.setAppType(AppType.SMARTPOS);
        return billingServiceFeignClient.getBillingInvoiceAmount(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/billing-invoice-customer-count")
    public ResponseEntity<Long> getBillingInvoiceCustomerCount(@ParameterObject BillingInvoiceFilter filter) {
        return billingServiceFeignClient.getBillingInvoiceCustomerCount(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/billing-customers/child-list/{tin}")
    public ResponseEntity<Object> getCustomerChildren(@PathVariable String tin) {
        return billingServiceFeignClient.getCustomerChildren(tokenStoreService.getToken(TokenType.BILLING), tin);
    }

    @GetMapping("/billing-customers/get-parent/{tin}")
    public ResponseEntity<Object> getParentByCustomerId(@PathVariable String tin) {
        return billingServiceFeignClient.getParentByCustomerId(tokenStoreService.getToken(TokenType.BILLING), tin);
    }

    @GetMapping("/billing-invoice/{id}/check-invoice-xfile-status")
    public ResponseEntity<Object> checkBillingInvoiceXfileStatus(@PathVariable String id) {
        return ResponseEntity.ok(billingServiceFeignClient.checkBillingInvoiceXfileStatus(tokenStoreService.getToken(TokenType.BILLING), id));
    }

    @GetMapping("/ledger-detail/grouped-transactions")
    public ResponseEntity<Object> getledgerDetailGruppedTransactions(@ParameterObject LedgerDetailAppTypeFilter filter) {
        if (filter.getOrderBy().equals("id")) {
            filter.setOrderBy(null);
        }
        System.out.println("REST request ledger-detail grouped-transactions");
        return billingServiceFeignClient.getledgerDetailGruppedTransactions(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/agreements/show-calculation/xizmat/{xizmatId}/customer/{tin}")
    public ResponseEntity<BigDecimal> showCalculationByXizmatIdAndCustomerTin(@PathVariable Long xizmatId,
                                                                              @PathVariable String tin,
                                                                              @RequestParam(required = false, name = "info") String info,
                                                                              @RequestParam(name = "instance", required = false) Integer instance) {
        return billingServiceFeignClient.showCalculationByXizmatIdAndCustomerTin(tokenStoreService.getToken(TokenType.BILLING), xizmatId, tin, info, instance);
    }

    @PostMapping("/organization-application-form/create")
    public ResponseEntity<Object> organizationApplicationFormCreate(@Valid @RequestBody OrganizationApplicationFormCreateRequestDto dto) {
        return billingServiceFeignClient.organizationApplicationFormCreate(tokenStoreService.getToken(TokenType.BILLING), dto);
    }

    @PutMapping("/organization-application-form/update")
    public ResponseEntity<Object> organizationApplicationFormUpdate(@Valid @RequestBody OrganizationApplicationFormUpdateRequestDto dto) {
        return billingServiceFeignClient.organizationApplicationFormUpdate(tokenStoreService.getToken(TokenType.BILLING), dto);
    }

    @PutMapping("/organization-application-form/archive")
    public ResponseEntity<Object> organizationApplicationFormArchive(@Valid @RequestBody OrganizationApplicationFormArchiveRequestDto dto) {
        return billingServiceFeignClient.organizationApplicationFormArchive(tokenStoreService.getToken(TokenType.BILLING), dto);
    }

    @GetMapping("/organization-application-form/get-by-id/{id}")
    public ResponseEntity<Object> organizationApplicationFormGetById(@PathVariable String id) {
        return billingServiceFeignClient.organizationApplicationFormGetById(tokenStoreService.getToken(TokenType.BILLING), id);
    }

    @GetMapping("/organization-application-form/pageable-list")
    public Object organizationApplicationFormPageableList(@ParameterObject OrganizationApplicationFormFilter filter) {
        return billingServiceFeignClient.organizationApplicationFormPageableList(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @PostMapping("/referral-link/generate-referral-link")
    public ResponseEntity<String> generateReferralLink(@Valid @RequestBody ReferralLinkDTO dto) {
        return billingServiceFeignClient.generateReferralLink(tokenStoreService.getToken(TokenType.BILLING), dto);
    }

    @PostMapping("/referral-link/check-referral-link")
    public ResponseEntity<Object> checkReferralLink(@Valid @RequestBody String data) {
        return billingServiceFeignClient.checkReferralLink(tokenStoreService.getToken(TokenType.BILLING), data);
    }

    @GetMapping("/promotion/show-promotion")
    public ResponseEntity<Object> showPromotionWithTin(@RequestParam(name = "xizmatId") Long xizmatId,
                                                       @RequestParam(name = "promotionCode") String promotionCode,
                                                       @RequestParam(name = "calculatedAmount") BigDecimal calculatedAmount) {
        return billingServiceFeignClient.showPromotionWithTin(tokenStoreService.getToken(TokenType.BILLING), xizmatId, promotionCode, calculatedAmount);
    }

    @PostMapping("/promocode/apply-promo-code")
    public ResponseEntity<Object> applyPromoCode(@Valid @RequestBody PromoCodeApplyDto dto) {
        return billingServiceFeignClient.applyPromoCode(tokenStoreService.getToken(TokenType.BILLING), dto);
    }

    @GetMapping("/billing-region-list")
    public Object getBillingRegionList() {
        return billingServiceFeignClient.getBillingRegionList(tokenStoreService.getToken(TokenType.BILLING));
    }

    @GetMapping("/organization-application-form-status")
    public ResponseEntity<EnumSet<OrganizationApplicationFormStatus>> getBillingOrganizationApplicationFormStatus() {
        return ResponseEntity.ok().body(EnumSet.allOf(OrganizationApplicationFormStatus.class));
    }

    @GetMapping("/referral-link/get-active-referral-promotion-id")
    public ResponseEntity<String> getActiveReferralPromotionId() {
        return billingServiceFeignClient.getActiveReferralPromotionId(tokenStoreService.getToken(TokenType.BILLING));
    }

    @GetMapping("/promocode/promo-codes-pageable")
    public Object getPromoCodesPageable(@ParameterObject PromoCodeFilter filter) {
        return billingServiceFeignClient.getPromoCodesPageable(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/promocode/promo-codes-pageable/{promotionId}")
    public Object getPromoCodesPageableByPromotionId(@ParameterObject PromoCodeFilter filter, @PathVariable String promotionId) {
        return billingServiceFeignClient.getPromoCodesPageableByPromotionId(tokenStoreService.getToken(TokenType.BILLING), filter, promotionId);
    }

    @GetMapping("/referral-sender/referrals-pageable")
    public Object getReferralsPageable(@ParameterObject ReferralsFilter filter) {
        return billingServiceFeignClient.getReferralsPageable(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/promotion/promotion-pageable")
    public Object getPromotionPageable(@ParameterObject BillingPromotionFilter filter) {
        return billingServiceFeignClient.getPromotionPageable(tokenStoreService.getToken(TokenType.BILLING), filter);
    }

    @GetMapping("/promotion/{id}")
    public Object getBillingPromotionById(@PathVariable("id") String id) {
        return billingServiceFeignClient.getBillingPromotionById(tokenStoreService.getToken(TokenType.BILLING), id);
    }

    @GetMapping("/a-pay-promotion/check/{tin}")
    public Object apayPromotionCheck(@PathVariable(value = "tin") String tin) {
        return billingServiceFeignClient.getBillingApayPromotionCheck(tokenStoreService.getToken(TokenType.BILLING), tin);
    }

    @GetMapping("/a-pay-promotion/get-transactions")
    public Object getTransactions(@ParameterObject ApayTransactionRequestDto dto) {
        return billingServiceFeignClient.getBillingGetApayTransactions(tokenStoreService.getToken(TokenType.BILLING), dto);
    }

    @PostMapping("/a-pay-promotion-subscriber/create")
    public ResponseEntity<Object> aPayPromotionSubscriberCreate(@Valid @RequestBody ApayPromotionSubscriberCreateRequestDto dto) {
        companyService.checkApplicationStatus(dto.getTin());
        return billingServiceFeignClient.aPayPromotionSubscriberCreate(tokenStoreService.getToken(TokenType.BILLING), dto);
    }

    @GetMapping("/a-pay-promotion-subscriber/get-by-tin/{tin}")
    public Object aPayPromotionSubscriberGetByTin(@PathVariable(value = "tin") String tin) {
        return billingServiceFeignClient.aPayPromotionSubscriberGetByTin(tokenStoreService.getToken(TokenType.BILLING), tin);
    }

    @GetMapping("/a-pay-promotion/list")
    public Object aPayPromotionList(@ParameterObject APayPromotionFilter filter) {
        filter.setActive(true);
        filter.setAppType(AppType.SMARTPOS.getCode());
        return billingServiceFeignClient.aPayPromotionList(tokenStoreService.getToken(TokenType.BILLING), filter);
    }
}
