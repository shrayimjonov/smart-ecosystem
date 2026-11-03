package uz.uzkassa.smartpos.integration.billing;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.dto.filter.AgreementFilter;
import uz.uzkassa.smartpos.dto.filter.BillingCategoryFilter;
import uz.uzkassa.smartpos.dto.filter.BillingPageFilter;
import uz.uzkassa.smartpos.dto.filter.OrganizationApplicationFormFilter;
import uz.uzkassa.smartpos.dto.integration.BillingCategoryTreeDto;
import uz.uzkassa.smartpos.dto.integration.LoginRequestDto;
import uz.uzkassa.smartpos.dto.integration.TokenResponseDto;
import uz.uzkassa.smartpos.dto.integration.agreement.*;
import uz.uzkassa.smartpos.dto.integration.billingapay.ActivateAgreementRequestDto;
import uz.uzkassa.smartpos.dto.integration.billingapay.ApayPromotionSubscriberCreateRequestDto;
import uz.uzkassa.smartpos.dto.integration.billingapay.ApayTransactionRequestDto;
import uz.uzkassa.smartpos.dto.integration.customerpublicoffer.CustomerPublicOfferCtoIdRequestDto;
import uz.uzkassa.smartpos.dto.integration.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.smartpos.dto.integration.enums.BillingEntityType;
import uz.uzkassa.smartpos.dto.integration.organizationapplicationform.OrganizationApplicationFormArchiveRequestDto;
import uz.uzkassa.smartpos.dto.integration.organizationapplicationform.OrganizationApplicationFormCreateRequestDto;
import uz.uzkassa.smartpos.dto.integration.organizationapplicationform.OrganizationApplicationFormUpdateRequestDto;
import uz.uzkassa.smartpos.dto.integration.promocode.PromoCodeApplyDto;
import uz.uzkassa.smartpos.dto.integration.publicoffer.PublicOfferResponseDto;
import uz.uzkassa.smartpos.dto.integration.quote.QuoteChangeStatusRequestDto;
import uz.uzkassa.smartpos.dto.integration.quote.QuoteCreateRequestDto;
import uz.uzkassa.smartpos.dto.integration.quote.QuoteResponseDto;
import uz.uzkassa.smartpos.dto.integration.referrallink.ReferralLinkDTO;
import uz.uzkassa.smartpos.integration.apay.request.APayPaymentAddRequestDto;
import uz.uzkassa.smartpos.integration.apay.request.APayPaymentPayRequestDto;
import uz.uzkassa.smartpos.integration.apay.request.APayPaymentQrRequestDto;
import uz.uzkassa.smartpos.integration.apay.response.APayAddTransactionResponseDto;
import uz.uzkassa.smartpos.integration.apay.response.APayQrTransactionResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class BillingServiceFeignClient {
    private final RestClient restClient;

    public BillingServiceFeignClient(ApplicationProperties applicationProperties, @Qualifier("pooledIntegrationHttpClient") CloseableHttpClient httpClient) {
        restClient = RestClient.builder()
                .baseUrl(applicationProperties.getBilling().getHost())
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    public ResponseEntity<TokenResponseDto> login(LoginRequestDto dto) {
        return restClient.post()
                .uri("/auth/login")
                .body(dto)
                .retrieve()
                .toEntity(TokenResponseDto.class);
    }


    public ResponseEntity<Object> getXizmats(String token, XizmatFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/xizmats")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }


    public ResponseEntity<Object> getXizmatsChildren(String token, Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/xizmats/{id}/get-children")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<Object> getGroupedXizmats(String token, XizmatFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/xizmats/grouped")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<Object> getSingleXizmats(String token, XizmatFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/xizmats/single")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }


    public ResponseEntity<Object> getAgreements(String token, AgreementFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<Object> getAllAgreementsPageable(String token, AgreementFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements-pageable")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }


    public ResponseEntity<Object> getAgreementById(String token, Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    public ResponseEntity<Object> updateAgreementById(String token, Long id, List<UpdateSubAgreementDto> dtos) {
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}/update_subagreements")
                        .build(Map.of("id", id))
                )
                .body(dtos)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> saveAgreement(String token, CreateAgreementDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> updateAgreement(String token, CreateAgreementDTO dto) {
        return restClient.put()
                .uri("/services/billing/api/agreements")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    public ResponseEntity<Object> saveAgreement(String token, CreateAgreementsDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements/multi")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public AgreementDTO pauseAgreement(String token, Long id) {
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}/pause")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .body(AgreementDTO.class);
    }

    public Object activateAgreement(String token, ActivateAgreementRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements/activate")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    public AgreementDTO cancelAgreement(String token, Long id) {
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}/cancel")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .body(AgreementDTO.class);
    }

    public ResponseEntity<PublicOfferResponseDto> getLastActiveOffer(String token) {
        return restClient.get()
                .uri("/services/billing/api/public-offer/get-last-active")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(PublicOfferResponseDto.class);
    }

    public ResponseEntity<CustomerPublicOfferResponseDto> createCustomerOffer(String token, CustomerPublicOfferCtoIdRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/customer-public-offer/create-by-tin")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(CustomerPublicOfferResponseDto.class);
    }


    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByCtoId(String token, Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/customer-public-offer/customer/cto/{id}")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(CustomerPublicOfferResponseDto.class);
    }

    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByTin(String token, String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/customer-public-offer/customer/tin/{tin}")
                        .build(Map.of("tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(CustomerPublicOfferResponseDto.class);
    }


    public ResponseEntity<AgreementDTO> updateAgreementNote(String token, Long id, String note) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}/update-note")
                        .queryParamIfPresent("note", Optional.ofNullable(note))
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(AgreementDTO.class);
    }


    public ResponseEntity<Object> getByCustomerCtoId(String token,
                                                     String id,
                                                     Integer page,
                                                     Integer size) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/customer/cto/{id}")
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getByCustomerTin(String token,
                                                   String tin,
                                                   Integer page,
                                                   Integer size) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/customer/tin/{tin}")
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .build(Map.of("tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<QuoteResponseDto> changeStatus(String token, String id, QuoteChangeStatusRequestDto dto) {
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/{id}/change-status")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(dto)
                .retrieve()
                .toEntity(QuoteResponseDto.class);
    }


    public ResponseEntity<QuoteResponseDto> getByQuoteId(String token, String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/{id}")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(QuoteResponseDto.class);
    }

    public ResponseEntity<String> getTemplateByQuoteId(String token, String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/{id}/get-template")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<String> uploadToQuote(String token, String id, MultipartFile file) {
        var body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/{id}/upload")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<Object> createQuote(String token, QuoteCreateRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/quote/create")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(dto)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getInvoiceByCustomerCtoId(String token,
                                                            String id,
                                                            Integer page,
                                                            Integer size) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/customer/cto/{id}")
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getInvoiceByCustomerTin(String token,
                                                          String tin,
                                                          Integer page,
                                                          Integer size) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/customer/tin/{tin}")
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .build(Map.of("tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    public ResponseEntity<Object> getInvoiceByAgreementId(String token,
                                                          String id,
                                                          Integer page,
                                                          Integer size) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/get-by-agreement-id/{id}")
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    public ResponseEntity<List<PublicAgreementDTO>> getActiveAgreements(String token,
                                                                        Long ctoId,
                                                                        String tin,
                                                                        String serviceType,
                                                                        Long smartposUserId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/is-active/")
                        .queryParamIfPresent("ctoId", Optional.ofNullable(ctoId))
                        .queryParamIfPresent("tin", Optional.ofNullable(tin))
                        .queryParamIfPresent("serviceType", Optional.ofNullable(serviceType))
                        .queryParamIfPresent("smartposUserId", Optional.ofNullable(smartposUserId))
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }


    public ResponseEntity<String> getInvoiceTemplateById(String token, String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/{id}/get-template")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(String.class);
    }

    public Object upload(String token, BillingEntityType relatedTo, String relatedToId, MultipartFile file) {
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", file.getResource());
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/attachments/upload")
                        .queryParamIfPresent("relatedToId", Optional.ofNullable(relatedToId))
                        .queryParamIfPresent("relatedTo", Optional.ofNullable(relatedTo))
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<List<Long>> getActiveAgreementIdsByServiceType(String token, String serviceType) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/active-ids")
                        .queryParamIfPresent("serviceType", Optional.ofNullable(serviceType))
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }


    @Deprecated
    public ResponseEntity<BigDecimal> getBalanceByCtoId(String token, Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/ledger-detail/cto/{id}/balance")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(BigDecimal.class);
    }


    public ResponseEntity<BigDecimal> getBalanceByTin(String token, String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/ledger-detail/tin/{tin}/balance")
                        .build(Map.of("tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(BigDecimal.class);
    }


    public APayAddTransactionResponseDto createTransaction(String token, APayPaymentAddRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/a-pay-payments/create-transaction")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(dto)
                .retrieve()
                .body(APayAddTransactionResponseDto.class);
    }


    public APayQrTransactionResponseDto createTransactionQr(String token, APayPaymentQrRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/apay-transactions/create-transaction-qr")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(dto)
                .retrieve()
                .body(APayQrTransactionResponseDto.class);
    }

    public void acceptPayment(String token, APayPaymentPayRequestDto dto) {
        restClient.post()
                .uri("/services/billing/api/a-pay-payments/confirm-transaction")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(dto)
                .retrieve()
                .body(Void.class);
    }

    public ResponseEntity<Object> getledgerDetailTransactions(String token, String tin, BillingPageFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/ledger-detail/tin/{tin}/transactions")
                        .queryParams(filter.getMultiValueMap())
                        .build(Map.of("tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    @GetMapping(value = "/services/billing/api/category/list-of-categories-by-current-app")
    public Object listOfCategoriesByCurrentApp(String token, BillingCategoryFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/category/list-of-categories-by-current-app")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfCategoriesByCurrentApp(String token, BillingCategoryFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/category/tree-of-categories-by-current-app")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfActiveCategoriesByCurrentApp(String token, BillingCategoryFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/category/tree-of-active-categories-by-current-app")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<Void> startAgreementWithTin(String token, String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/start-agreement/{tin}")
                        .build(Map.of("tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Void.class);
    }

    public ResponseEntity<Object> getQuoteByAgreement(String token, String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/agreement/{id}")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> billingInvoiceList(String token, BillingInvoiceFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/list")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    public ResponseEntity<BigDecimal> getBillingInvoiceAmount(String token, BillingInvoiceFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/get-invoice-amount")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(BigDecimal.class);
    }

    public ResponseEntity<Long> getBillingInvoiceCustomerCount(String token, BillingInvoiceFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/get-customer-count-by-invoice-filter")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Long.class);
    }

    public ResponseEntity<Object> getCustomerChildren(String token, String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/customers/child-list-by-tin/{tin}")
                        .build(Map.of("tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getParentByCustomerId(String token, String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/customers/get-parent-by-tin/{tin}")
                        .build(Map.of("tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> checkBillingInvoiceXfileStatus(String token, String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/{id}/check-invoice-xfile-status")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getledgerDetailGruppedTransactions(String token, LedgerDetailAppTypeFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/ledger-detail/grouped")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<BigDecimal> showCalculationByXizmatIdAndCustomerTin(String token,
                                                                              Long xizmatId,
                                                                              String tin,
                                                                              String info,
                                                                              Integer instance) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/show-calculation/xizmat/{xizmatId}/customer/{tin}")
                        .queryParamIfPresent("info", Optional.ofNullable(info))
                        .queryParamIfPresent("instance", Optional.ofNullable(instance))
                        .build(Map.of("xizmatId", xizmatId, "tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(BigDecimal.class);
    }

    public ResponseEntity<Object> organizationApplicationFormCreate(String token, OrganizationApplicationFormCreateRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/organization-application-form/create")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> organizationApplicationFormUpdate(String token, OrganizationApplicationFormUpdateRequestDto dto) {
        return restClient.put()
                .uri("/services/billing/api/organization-application-form/update")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> organizationApplicationFormArchive(String token, OrganizationApplicationFormArchiveRequestDto dto) {
        return restClient.put()
                .uri("/services/billing/api/organization-application-form/archive")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> organizationApplicationFormGetById(String token, String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/organization-application-form/get-by-id/{id}")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> organizationApplicationFormPageableList(String token, OrganizationApplicationFormFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/organization-application-form/pageable-list")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<String> generateReferralLink(String token, ReferralLinkDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/referral-link/generate-referral-link")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<Object> checkReferralLink(String token, String data) {
        return restClient.post()
                .uri("/services/billing/api/referral-link/check-referral-link")
                .body(data)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> showPromotionWithTin(String token, Long xizmatId, String promotionCode, BigDecimal calculatedAmount) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/show-promotion")
                        .queryParam("xizmatId", xizmatId)
                        .queryParam("promotionCode", promotionCode)
                        .queryParam("calculatedAmount", calculatedAmount)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> applyPromoCode(String token, PromoCodeApplyDto dto) {
        return restClient.post()
                .uri("/services/billing/api/promocode/apply-promo-code")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    public ResponseEntity<Object> getBillingRegionList(String token) {
        return restClient.get()
                .uri("/services/billing/api/region/get-list")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<String> getActiveReferralPromotionId(String token) {
        return restClient.get()
                .uri("/services/billing/api/referral-link/get-active-referral-promotion-id")
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<Object> getReferralsPageable(String token, ReferralsFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/referral-sender/referrals-pageable")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }


    public ResponseEntity<Object> getPromoCodesPageable(String token, PromoCodeFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/promocode/promo-codes-pageable")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<Object> getPromoCodesPageableByPromotionId(String token, PromoCodeFilter filter, String promotionId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/promocode/promo-codes-pageable/{promotionId}")
                        .queryParams(filter.getMultiValueMap())
                        .build(Map.of("promotionId", promotionId))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<Object> getPromotionPageable(String token, BillingPromotionFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/promotion/list")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<Object> getBillingPromotionById(String token, String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/promotion/{id}")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getBillingApayPromotionCheck(String token, String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/a-pay-promotion/check/{tin}")
                        .build(Map.of("tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getBillingGetApayTransactions(String token, ApayTransactionRequestDto dto) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/a-pay-promotion/get-transactions")
                        .queryParams(dto.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<Object> aPayPromotionSubscriberCreate(String token, ApayPromotionSubscriberCreateRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/a-pay-promotion-subscriber")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> aPayPromotionSubscriberGetByTin(String token, String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/a-pay-promotion-subscriber/get-by-tin/{tin}")
                        .build(Map.of("tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> aPayPromotionList(String token, APayPromotionFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/a-pay-promotion/list")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }

    public String getToken(String username, String password) {
        ResponseEntity<TokenResponseDto> responseEntity = login(new LoginRequestDto(username, password));
        return Objects.requireNonNull(responseEntity.getBody()).getAccessToken();
    }


}
