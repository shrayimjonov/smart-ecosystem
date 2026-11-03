package uz.uzkassa.horeca.integration;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.horeca.dto.apay.APayAddTransactionResponseDto;
import uz.uzkassa.horeca.dto.apay.APayPaymentAddRequestDto;
import uz.uzkassa.horeca.dto.apay.APayPaymentPayRequestDto;
import uz.uzkassa.horeca.dto.auth.TokenDTO;
import uz.uzkassa.horeca.dto.billing.BonusResponseFilter;
import uz.uzkassa.horeca.dto.billing.LoginRequestDto;
import uz.uzkassa.horeca.dto.billing.agreement.*;
import uz.uzkassa.horeca.dto.billing.billinginvoice.InvoiceResponseDto;
import uz.uzkassa.horeca.dto.billing.customerpublicoffer.CustomerPublicOfferCtoIdRequestDto;
import uz.uzkassa.horeca.dto.billing.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.horeca.dto.billing.publicoffer.PublicOfferResponseDto;
import uz.uzkassa.horeca.dto.billing.quote.QuoteChangeStatusRequestDto;
import uz.uzkassa.horeca.dto.billing.quote.QuoteCreateRequestDto;
import uz.uzkassa.horeca.dto.billing.quote.QuoteResponseDto;
import uz.uzkassa.horeca.enums.billing.AppType;
import uz.uzkassa.horeca.enums.billing.BillingEntityType;
import uz.uzkassa.horeca.filters.billing.*;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.web.rest.errors.BadRequestException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class BillingServiceClient extends BaseClient {
    public static final int TOKEN_LIVE_IN_SECONDS = 86400;
    static LocalDateTime authenticateRefreshTime;
    static String tokenStore;

    public Object getXizmats(XizmatFilter filter) {
        return get(getHost().path("/services/billing/api/xizmats").queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public Object getGroupedXizmats(XizmatFilter filter) {
        return get(getHost().path("/services/billing/api/xizmats/grouped")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()),
                Object.class);
    }

    public Object getXizmatChildren(Long xizmatId) {
        return get(getHost().path("/services/billing/api/xizmats/")
                        .path(xizmatId.toString()).path("/get-children"),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public Object getSingleXizmats(XizmatFilter filter) {
        return get(getHost().path("/services/billing/api/xizmats/single")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public Object getAgreements(AgreementFilter filter) {
        return get(getHost().path("/services/billing/api/agreements")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public Object getTariff(String tin) {
        return get(getHost().path("/services/billing/api/agreements/get-grupped-xizmat-by-tin/".concat(tin).concat("/").concat(AppType.HORECA.name())),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public Object getAgreementById(Long id) {
        return get(getHost().path("/services/billing/api/agreements/")
                        .path("" + id),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public PublicAgreementDTO getAgreementDtoById(Long id) {
        return get(getHost().path("/services/billing/api/agreements/").path("" + id),
                new HttpEntity<>(getAuthHeaders()), PublicAgreementDTO.class);
    }

    public Object updateAgreementById(Long id, List<UpdateSubAgreementDto> dtos) {
        return put(getHost().path("/services/billing/api/agreements/" + id + "/update_subagreements"),
                new HttpEntity<>(dtos, getAuthHeaders()), Object.class);
    }

    public Object saveAgreement(CreateAgreementDTO dto) {
        return post(getHost().path("/services/billing/api/agreements"),
                new HttpEntity<>(dto, getAuthHeaders()), Object.class);

    }

    public Object activateAgreement(Long agreementId) {
        return post(getHost().path("/services/billing/api/agreements/activate"),
                new HttpEntity<>(new ActivateAgreementRequestDto(agreementId), getAuthHeaders()), Object.class);
    }

    public Object updateAgreement(CreateAgreementDTO dto) {
        return put(getHost().path("/services/billing/api/agreements"),
                new HttpEntity<>(dto, getAuthHeaders()), Object.class);
    }

    public Object saveAgreement(CreateAgreementsDTO dto) {
        return post(getHost().path("/services/billing/api/agreements/multi"),
                new HttpEntity<>(dto, getAuthHeaders()), Object.class);
    }

    public AgreementDTO pauseAgreement(Long id) {
        return put(getHost().path("/services/billing/api/agreements/" + id + "/pause"),
                new HttpEntity<>(getAuthHeaders()), AgreementDTO.class);
    }

    public AgreementDTO cancelAgreement(Long id) {
        return put(getHost().path("/services/billing/api/agreements/" + id + "/cancel"),
                new HttpEntity<>(getAuthHeaders()), AgreementDTO.class);
    }

    public AgreementDTO reactivateAgreement(Long id) {
        return put(getHost().path("/services/billing/api/agreements/" + id + "/activate"),
                new HttpEntity<>(getAuthHeaders()), AgreementDTO.class);
    }

    public PublicOfferResponseDto getLastActiveOffer() {
        return get(getHost().path("/services/billing/api/public-offer/get-last-active"),
                new HttpEntity<>(getAuthHeaders()), PublicOfferResponseDto.class);
    }

    public CustomerPublicOfferResponseDto getCustomerOfferById(String id) {
        return get(getHost().path("/services/billing/api/customer-public-offer/").path(id),
                new HttpEntity<>(getAuthHeaders()), CustomerPublicOfferResponseDto.class);
    }

    public CustomerPublicOfferResponseDto createCustomerOffer(CustomerPublicOfferCtoIdRequestDto dto) {
        return post(getHost().path("/services/billing/api/customer-public-offer/create-by-tin"),
                new HttpEntity<>(dto, getAuthHeaders()), CustomerPublicOfferResponseDto.class);
    }

    public CustomerPublicOfferResponseDto getCustomerOfferByCtoId(Long id) {
        return get(getHost().path("/services/billing/api/customer-public-offer/customer/cto/").path(String.valueOf(id)),
                new HttpEntity<>(getAuthHeaders()), CustomerPublicOfferResponseDto.class);
    }

    public CustomerPublicOfferResponseDto getCustomerOfferByTin(String tin) {
        return get(getHost().path("/services/billing/api/customer-public-offer/customer/tin/").path(tin),
                new HttpEntity<>(getAuthHeaders()), CustomerPublicOfferResponseDto.class);
    }

    public AgreementDTO updateAgreementNote(Long id, String note) {
        return get(getHost().path("/services/billing/api/agreements/" + id + "/update-note").queryParamIfPresent("note", Optional.ofNullable(note)),
                new HttpEntity<>(getAuthHeaders()), AgreementDTO.class);
    }

    public QuoteResponseDto getByCustomerCtoId(String id, Integer page, Integer size) {
        return get(getHost().path("/services/billing/api/quote/customer/cto/" + id).queryParamIfPresent("page", Optional.ofNullable(page)).queryParamIfPresent("size", Optional.ofNullable(size)),
                new HttpEntity<>(getAuthHeaders()), QuoteResponseDto.class);
    }

    public QuoteResponseDto getByCustomerTin(String tin, Integer page, Integer size) {
        return get(getHost().path("/services/billing/api/quote/customer/tin/" + tin).queryParamIfPresent("page", Optional.ofNullable(page)).queryParamIfPresent("size", Optional.ofNullable(size)),
                new HttpEntity<>(getAuthHeaders()), QuoteResponseDto.class);
    }

    public QuoteResponseDto changeStatus(String id, QuoteChangeStatusRequestDto dto) {
        return put(getHost().path("/services/billing/api/quote/" + id + "/change-status"),
                new HttpEntity<>(dto, getAuthHeaders()), QuoteResponseDto.class);
    }

    public QuoteResponseDto getByQuoteId(String id) {
        return get(getHost().path("/services/billing/api/quote/agreement/" + id),
                new HttpEntity<>(getAuthHeaders()), QuoteResponseDto.class);
    }

    public String getTemplateByQuoteId(String id) {
        return get(getHost().path("/services/billing/api/quote/" + id + "/get-template"),
                new HttpEntity<>(getAuthHeaders()), String.class);
    }

    public QuoteResponseDto uploadToQuote(String id, MultipartFile file) {
        var body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        HttpHeaders authHeaders = getAuthHeaders();
        authHeaders.add(MediaType.MULTIPART_FORM_DATA.getType(), MediaType.MULTIPART_FORM_DATA_VALUE);
        return post(getHost().path("/services/billing/api/quote/" + id + "/upload"),
                new HttpEntity<>(body, authHeaders), QuoteResponseDto.class);
    }

    public InvoiceResponseDto getInvoiceByCustomerCtoId(String id, Integer page, Integer size) {
        return get(getHost().path("/services/billing/api/invoice/customer/cto/" + id)
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size)),
                new HttpEntity<>(getAuthHeaders()), InvoiceResponseDto.class);
    }

    public InvoiceResponseDto getInvoiceByCustomerTin(String tin, Integer page, Integer size) {

        return get(getHost().path("/services/billing/api/invoice/customer/tin/" + tin)
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size)),
                new HttpEntity<>(getAuthHeaders()), InvoiceResponseDto.class);
    }

    public Object getInvoiceByQuoteId(String id, Integer page, Integer size) {
        return get(getHost().path("/services/billing/api/invoice/get-by-quote-id/" + id)
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size)),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public CustomerPublicOfferResponseDto getCustomerOfferByCustomerId(Long id) {
        return get(getHost().path("/services/billing/api/customer-public-offer/customer/" + id),
                new HttpEntity<>(getAuthHeaders()), CustomerPublicOfferResponseDto.class);
    }

    public Object getActiveAgreements(Long ctoId, String tin, String serviceType, Long smartposUserId) {
        return get(getHost().path("/services/billing/api/agreements/is-active/")
                        .queryParamIfPresent("ctoId", Optional.ofNullable(ctoId))
                        .queryParamIfPresent("tin", Optional.ofNullable(tin))
                        .queryParamIfPresent("serviceType", Optional.ofNullable(serviceType))
                        .queryParamIfPresent("operatorId", Optional.ofNullable(smartposUserId)),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public String getInvoiceTemplateById(String id) {
        return get(getHost().path("/services/billing/api/invoice/" + id + "/get-template"),
                new HttpEntity<>(getAuthHeaders()), String.class);
    }


    public Object upload(BillingEntityType relatedTo, String relatedToId, MultipartFile file) {
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", file.getResource());
        HttpHeaders authHeaders = getAuthHeaders();
        authHeaders.add(MediaType.MULTIPART_FORM_DATA.getType(), MediaType.MULTIPART_FORM_DATA_VALUE);
        return post(getHost().path("/services/billing/api/attachments/upload")
                        .queryParamIfPresent("relatedToId", Optional.ofNullable(relatedToId))
                        .queryParamIfPresent("relatedTo", Optional.ofNullable(relatedTo)),
                new HttpEntity<>(body, authHeaders), Object.class);
    }

    public Object getActiveAgreementIdsByServiceType(String serviceType) {
        return get(getHost().path("/services/billing/api/agreements/active-ids")
                        .queryParamIfPresent("serviceType", Optional.ofNullable(serviceType)),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public BigDecimal getBalanceByCtoId(Long id) {
        return get(getHost().path("/services/billing/api/ledger-detail/cto/" + id + "/balance"),
                new HttpEntity<>(getAuthHeaders()), BigDecimal.class);
    }

    public BigDecimal getBalanceByTin(String tin) {
        return get(getHost().path("/services/billing/api/ledger-detail/tin/" + tin + "/balance"),
                new HttpEntity<>(getAuthHeaders()), BigDecimal.class);
    }

    public APayAddTransactionResponseDto createTransaction(APayPaymentAddRequestDto dto) {
        return post(getHost().path("/services/billing/api/a-pay-payments/create-transaction"),
                new HttpEntity<>(dto, getAuthHeaders()), APayAddTransactionResponseDto.class);
    }

    public void acceptPayment(APayPaymentPayRequestDto dto) {
        post(getHost().path("/services/billing/api/a-pay-payments/confirm-transaction"),
                new HttpEntity<>(dto, getAuthHeaders()), Void.class);
    }

    public String updateAgreementManager(AgreementManagerRequestDTO dto) {
        return post(getHost().path("/services/billing/api/agreements/agreements-manager-update"),
                new HttpEntity<>(getAuthHeaders()), String.class);
    }

    public String getAllAgreementsPageable(AgreementFilter filter) {
        return get(getHost().path("/services/billing/api/agreements-pageable")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), String.class);
    }

    public Object getLedgerDetailTransactions(String tin, BillingPageFilter filter) {
        return get(getHost().path("/services/billing/api/ledger-detail/tin/" + tin + "/transactions")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public Object listOfCategoriesByCurrentApp(CategoryFilter filter) {
        return get(getHost().path("/services/billing/api/category/list-of-categories-by-current-app")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public Object treeOfCategoriesByCurrentApp(CategoryFilter filter) {
        return get(getHost().path("/services/billing/api/category/tree-of-categories-by-current-app")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public Object treeOfActiveCategoriesByCurrentApp(CategoryFilter filter) {
        return get(getHost().path("/services/billing/api/category/tree-of-active-categories-by-current-app")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public Object getBonusesList(BonusResponseFilter filter) {
        return get(getHost().path("/services/billing/api/bonuses-pageable")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public BigDecimal getBonusesAmount(BonusResponseFilter filter) {
        return get(getHost().path("/services/billing/api/get-bonuses-amount")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), BigDecimal.class);
    }

    public BigDecimal getInvoiceAmountByBonus(BonusResponseFilter filter) {
        return get(getHost().path("/services/billing/api/get-invoice-amount-by-bonus")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), BigDecimal.class);
    }

    public BigDecimal getAllInvoiceAmountByBonusFilter(BonusResponseFilter filter) {
        return get(getHost().path("/services/billing/api/get-all-invoice-amount-by-bonusfilter")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), BigDecimal.class);
    }


    public Long getAllInvoiceCountByBonusFilter(BonusResponseFilter filter) {
        return get(getHost().path("/services/billing/api/get-all-invoice-count-by-bonusfilter")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Long.class);
    }


    public Long getAllCompanyCountByBonusFilter(BonusResponseFilter filter) {
        return get(getHost().path("/services/billing/api/get-all-customer-count-by-bonusfilter")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Long.class);
    }


    public void startAgreementWithTin(String tin) {
        get(getHost().path("/services/billing/api/start-agreement/" + tin),
                new HttpEntity<>(getAuthHeaders()), Void.class);
    }


    public Object getledgerDetailGruppedTransactions(LedgerDetailAppTypeFilter filter) {
        return get(getHost().path("/services/billing/api/ledger-detail/grouped")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public Object billingInvoiceList(BillingInvoiceFilter filter) {
        return get(getHost().path("/services/billing/api/invoice/list")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }


    public BigDecimal getBillingInvoiceAmount(BillingInvoiceFilter filter) {
        return get(getHost().path("/services/billing/api/invoice/get-invoice-amount")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), BigDecimal.class);
    }

    public Long getBillingInvoiceCustomerCount(BillingInvoiceFilter filter) {
        return get(getHost().path("/services/billing/api/invoice/get-customer-count-by-invoice-filter")
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Long.class);
    }


    public Object checkBillingInvoiceXfileStatus(String id) {
        return get(getHost().path("/services/billing/api/invoice/" + id + "/check-invoice-xfile-status"),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public BigDecimal showCalculationByXizmatIdAndCustomerTin(Long xizmatId, String tin, String info, Integer instance) {
        return get(getHost().path("/services/billing/api/show-calculation/xizmat/" + xizmatId + "/customer/" + tin)
                        .queryParamIfPresent("info", Optional.ofNullable(info))
                        .queryParamIfPresent("instance", Optional.ofNullable(instance)),
                new HttpEntity<>(getAuthHeaders()), BigDecimal.class);
    }

    public TokenDTO login(LoginRequestDto dto) {
        return post(getHost().path("/auth/login"),
                new HttpEntity<>(dto), TokenDTO.class);
    }

    private UriComponentsBuilder getHost() {
        return applicationProperties.getBilling().baseUri();
    }

    @NotNull
    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = HeaderUtils.getHeader();
        headers.setBearerAuth(this.getToken());
        return headers;
    }

    public String getToken() {
        if (tokenStore != null && authenticateRefreshTime != null && authenticateRefreshTime.isAfter(LocalDateTime.now())) {
            return tokenStore;
        }

        try {
            LoginRequestDto loginDTO = new LoginRequestDto(
                    applicationProperties.getBilling().getUsername(),
                    applicationProperties.getBilling().getPassword()
            );
            TokenDTO response = login(loginDTO);
            tokenStore = Objects.requireNonNull(response).getAccessToken();
            authenticateRefreshTime = LocalDateTime.now().plusSeconds(TOKEN_LIVE_IN_SECONDS);
            return tokenStore;
        } catch (Exception e) {
            tokenStore = null;
            throw new BadRequestException(e.getMessage());
        }
    }

    public Object checkAuthenticated() {
        return get(getHost().path("services/uaa/api/account"),
                new HttpEntity<>(getAuthHeaders()),
                Object.class);
    }

    public Object createQuote(QuoteCreateRequestDto dto) {
        return post(getHost().path("/services/billing/api/quote/create"),
                new HttpEntity<>(dto), Object.class);
    }

    public Object getInvoiceByAgreementId(String id, BillingPageFilter filter) {
        return get(getHost().path("/services/billing/api/invoice/get-by-agreement-id/" + id)
                        .queryParams(filter.getMultiValueMap()),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }

    public Object getXizmatsChildren(Long id) {
        return get(getHost().path("/services/billing/api/xizmats/" + id + "/get-children"),
                new HttpEntity<>(getAuthHeaders()), Object.class);
    }
}
