package uz.uzkassa.cts.integrations;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.dto.auth.AuthenticateDTO;
import uz.uzkassa.cts.dto.auth.RefreshTokenDTO;
import uz.uzkassa.cts.dto.billing.BillingInvoicePayloadDTO;
import uz.uzkassa.cts.dto.integration.billing.*;
import uz.uzkassa.cts.dto.integration.response.BillingTransactionResponseDTO;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.enums.billing.BillingAppType;
import uz.uzkassa.cts.enums.billing.BillingDocumentType;
import uz.uzkassa.cts.enums.billing.BillingEntityType;
import uz.uzkassa.cts.enums.billing.BonusResponseFilter;
import uz.uzkassa.cts.filter.billing.*;
import uz.uzkassa.cts.interceptor.RestTemplateLoggingInterceptor;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.validator.LocalizationMessage;
import uz.uzkassa.cts.web.rest.errors.ErrorBillingDTO;
import uz.uzkassa.cts.web.rest.errors.ErrorDTO;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;
import uz.uzkassa.cts.web.rest.errors.NotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Component
public class BillingServiceFeignClient extends BaseClient implements MessageConstants {

    private final RestClient restClient;
    private final LocalizationMessage localizationMessage;
    private final ApplicationProperties applicationProperties;

    public BillingServiceFeignClient(@Qualifier("pooledIntegrationHttpClient") CloseableHttpClient httpClient,
                                     LocalizationMessage localizationMessage, ApplicationProperties applicationProperties) {
        this.localizationMessage = localizationMessage;
        this.applicationProperties = applicationProperties;

        restClient = RestClient.builder()
                .baseUrl(applicationProperties.getBillingProperty().getHost())
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .requestInterceptor(new RestTemplateLoggingInterceptor())
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    ErrorBillingDTO errorObject = new ErrorBillingDTO();
                    try {
                        errorObject = objectMapper.reader().readValue(response.getBody(), ErrorBillingDTO.class);
                    } catch (Exception ignored) {
                    }
                    ErrorDTO errorDTO = new ErrorDTO();
                    errorDTO.setTitle(errorObject.getTitle());
                    errorDTO.setStatus(errorObject.getStatus());
                    errorDTO.setDetail(errorObject.getDetail());
                    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                        throw new NotFoundException(localizationMessage.localize(ENTITY_NOT_FOUND, new Object[]{localizationMessage.localize(CUSTOMER)}));
                    }
                    throw new IntegrationException(errorObject.getDetail(), errorDTO, HttpStatus.valueOf(errorObject.getStatus()));
                })
                .build();
    }

    @Override
    public String authenticate() {
        AuthenticateDTO authenticateDTO = new AuthenticateDTO();
        authenticateDTO.setUsername(applicationProperties.getBillingProperty().getUsername());
        authenticateDTO.setPassword(applicationProperties.getBillingProperty().getPassword());
        ResponseEntity<RefreshTokenDTO> response;
        try {
            response = restClient.post().uri("/auth/login").body(authenticateDTO).retrieve().toEntity(RefreshTokenDTO.class);
        } catch (Exception e) {
            log.error("Error billing: {} =============================", e.getMessage());
            throw new IntegrationException("An unexpected error has occurred while authenticating to Smartpos billing service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (response.getBody() != null && response.getBody().getAccessToken() != null) {
            String token = response.getBody().getAccessToken();
            tokenStoreService.create(getIntegrationType(), token, applicationProperties.getBillingProperty().getTokenExpiration());
            return token;
        }
        log.error("Error auth: {}", response.getBody());
        throw new IntegrationException("An unexpected error has occurred while authenticating to Smartpos billing service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBearerAuth(this.getToken());
        return headers;
    }

    public Consumer<HttpHeaders> convertToConsumer() {
        return consumerHeaders -> this.getHeaders().forEach(consumerHeaders::addAll);
    }

    @Override
    public String getToken() {
        String token = tokenStoreService.getToken(getIntegrationType());
        if (token != null) {
            return token;
        }
        return authenticate();
    }

    @Override
    public IntegrationType getIntegrationType() {
        return IntegrationType.BILLING;
    }

    public ResponseEntity<Object> getXizmats(XizmatFilter filter) {
        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/xizmats")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<Object> getGroupedXizmats(XizmatFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/xizmats/grouped")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<Object> getSingleXizmats(XizmatFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/xizmats/single")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<Object> getAllAgreementsPageable(AgreementFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements-pageable")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve().toEntity(Object.class);
    }

    public ResponseEntity<Object> getAgreementById(Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public AgreementResponseDTO getAgreementObjectById(Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(AgreementResponseDTO.class)
                .getBody();
    }

    public AgreementResponseDTO getAgreementObjectById(Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(AgreementResponseDTO.class)
                .getBody();
    }


    public ResponseEntity<Object> updateAgreementById(Long id, List<UpdateSubAgreementDto> dtos) {
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}/update_subagreements")
                        .build(Map.of("id", id))
                )
                .body(dtos)
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> saveAgreement(CreateAgreementDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements")
                .body(dto)
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public AgreementResponseDTO createAgreement(CreateAgreementDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements")
                .body(dto)
                .headers(this.convertToConsumer())
                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
//                    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
//                        throw new NotFoundException(localizationService.localize(ERROR_CUSTOMER_NOT_FOUND_BILLING_AGREEMENT));
//                    }
//                })
                .toEntity(AgreementResponseDTO.class)
                .getBody();
    }

    public ResponseEntity<Object> updateAgreement(CreateAgreementDTO dto) {
        return restClient.put()
                .uri("/services/billing/api/agreements")
                .body(dto)
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> saveAgreement(CreateAgreementsDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements/multi")
                .body(dto)
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public AgreementDTO pauseAgreement(Long id) {
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}/pause")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .body(AgreementDTO.class);
    }

    public AgreementDTO cancelAgreement(Long id) {
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}/cancel")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .body(AgreementDTO.class);
    }

    public ResponseEntity<PublicOfferResponseDto> getLastActiveOffer() {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/public-offer/get-last-active")
                        .queryParam("documentType", BillingDocumentType.PUBLIC_OFFER.getCode())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(PublicOfferResponseDto.class);
    }

    public ResponseEntity<CustomerPublicOfferResponseDto> createCustomerOffer(CustomerPublicOfferCtoIdRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/customer-public-offer/create-by-tin")
                .body(dto)
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(CustomerPublicOfferResponseDto.class);
    }

    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByTin(String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/customer-public-offer/customer/tin/{tin}")
                        .build(Map.of("tin", tin))
                )
                .headers(this.convertToConsumer())
                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
//                    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
//                        log.error("Billing 404 error : {}", response);
//                        throw new NotFoundException(localizationService.localize(ENTITY_NOT_FOUND_WITH, new Object[]{localizationService.localize(CUSTOMER), localizationService.localize(TIN), tin}));
//                    } else
//                        throw new BadRequestException(response.getStatusText());
//                })
                .toEntity(CustomerPublicOfferResponseDto.class);
    }

    public ResponseEntity<AgreementDTO> updateAgreementNote(Long id, String note) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}/update-note")
                        .queryParamIfPresent("note", Optional.ofNullable(note))
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(AgreementDTO.class);
    }

    public ResponseEntity<Page<QuoteResponseDto>> getByCustomerTin(String tin, Integer page, Integer size) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/customer/tin/{tin}")
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .build(Map.of("tin", tin))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<QuoteResponseDto> changeStatus(String id, QuoteChangeStatusRequestDto dto) {
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/{id}/change-status")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .body(dto)
                .retrieve()
                .toEntity(QuoteResponseDto.class);
    }

    public ResponseEntity<QuoteResponseDto> getByQuoteId(String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/{id}")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(QuoteResponseDto.class);
    }

    public ResponseEntity<String> getTemplateByQuoteId(String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/{id}/get-template")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<String> uploadToQuote(String id, MultipartFile file) {
        var body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/{id}/upload")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<Page<InvoiceResponseDto>> getInvoiceByCustomerTin(String tin, Integer page, Integer size) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/customer/tin/{tin}")
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .build(Map.of("tin", tin))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<Object> getInvoiceByQuoteId(String id, Integer page, Integer size) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/get-by-quote-id/{id}")
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<String> getInvoiceTemplateById(String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/{id}/get-template")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(String.class);
    }

    public Object upload(BillingEntityType relatedTo, String relatedToId, MultipartFile file) {
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", file.getResource());
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/attachments/upload")
                        .queryParamIfPresent("relatedToId", Optional.ofNullable(relatedToId))
                        .queryParamIfPresent("relatedTo", Optional.ofNullable(relatedTo))
                        .build()
                )
                .headers(this.convertToConsumer())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }

    @Deprecated
    public ResponseEntity<BigDecimal> getBalanceByCtoId(Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/ledger-detail/cto/{id}/balance")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(BigDecimal.class);
    }

    public ResponseEntity<BigDecimal> getBalanceByTin(String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/ledger-detail/tin/{tin}/balance")
                        .build(Map.of("tin", tin))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(BigDecimal.class);
    }

    public ResponseEntity<Object> getLedgerDetailTransactions(String tin, LedgerDetailFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/ledger-detail/tin/{tin}/transactions")
                        .queryParams(filter.getMultiValueMap())
                        .build(Map.of("tin", tin))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public Object listOfCategoriesByCurrentApp(BillingCategoryFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/category/list-of-categories-by-current-app")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }


    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfCategoriesByCurrentApp(BillingCategoryFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/category/tree-of-categories-by-current-app")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfActiveCategoriesByCurrentApp(BillingCategoryFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/category/tree-of-active-categories-by-current-app")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<Void> startAgreementWithTin(String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/start-agreement/{tin}")
                        .build(Map.of("tin", tin))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Void.class);
    }

    public ResponseEntity<Object> billingInvoiceList(BillingInvoiceFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/list")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<BigDecimal> getBillingInvoiceAmount(BillingInvoiceFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/get-invoice-amount")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(BigDecimal.class);
    }

    public ResponseEntity<Long> getBillingInvoiceCustomerCount(BillingInvoiceFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/get-customer-count-by-invoice-filter")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Long.class);
    }

    public ResponseEntity<Object> checkBillingInvoiceXfileStatus(String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/{id}/check-invoice-xfile-status")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getLedgerDetailGroupedTransactions(LedgerDetailAppTypeFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/ledger-detail/grouped")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<BigDecimal> showCalculationByXizmatIdAndCustomerTin(Long xizmatId, String tin, String info, Integer instance) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/show-calculation/xizmat/{xizmatId}/customer/{tin}")
                        .queryParamIfPresent("info", Optional.ofNullable(info))
                        .queryParamIfPresent("instance", Optional.ofNullable(instance))
                        .build(Map.of("xizmatId", xizmatId, "tin", tin))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(BigDecimal.class);
    }


    public ResponseEntity<List<String>> getServiceTypes() {
        return restClient.get()
                .uri("/services/billing/api/services")
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public AgreementDTO reactivateAgreement(@RequestHeader(value = "Authorization") @PathVariable(value = "id") Long id) {
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}/activate")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .body(AgreementDTO.class);
    }

    public ResponseEntity<Object> getBonusesListByManagerId(String id, BonusResponseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/bonuses-pageable-by-manager/{id}")
                        .queryParams(filter.getMultiValueMap())
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getBonusesList(BonusResponseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/bonuses-pageable")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getBonusesAmount(BonusResponseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/get-bonuses-amount")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public Object getCustomerChildrenByTin(String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/customers/child-list-by-tin/{tin}")
                        .build(Map.of("tin", tin))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<BigDecimal> calculatePrepaid(String tin, String parentChildEnum) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/calculate-prepaid/{tin}/{parentChildEnum}")
                        .build(Map.of("tin", tin, "parentChildEnum", parentChildEnum))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(BigDecimal.class);
    }

    public ResponseEntity<CustomerPublicOfferResponseDto> uploadFileToCustomerPublicOffer(String id, MultipartFile file) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/customer-public-offer/{id}/upload")
                        .build(Map.of("id", id))
                )
                .headers(this.convertToConsumer())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(file)
                .retrieve()
                .toEntity(CustomerPublicOfferResponseDto.class);
    }

    public ResponseEntity<Object> calculatePrepaidList(ChildCalculatePrepaidRequestDto dto) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/calculate-prepaid-list")
                        .queryParams(dto.getMultiValueMap())
                        .build()
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<String> updateAgreementManager(AgreementManagerRequestDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements/agreements-manager-update")
                .headers(this.convertToConsumer())
                .body(dto)
                .retrieve()
                .toEntity(String.class);
    }

    public ResponseEntity<List<XizmatDTO>> getXizmatIdByTin(String tin, String appType) {
        return restClient
                .get()
                .uri("/services/billing/api/agreements/get-grupped-xizmat-by-tin/" + tin + "/" + appType)
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public InvoiceResponseDto createPrimeInvoice(BillingInvoicePayloadDTO dto) {
        try {
            ResponseEntity<InvoiceResponseDto> responseEntity = restClient
                    .post()
                    .uri("/services/billing/api/invoice/create")
                    .headers(this.convertToConsumer())
                    .body(dto)
                    .retrieve()
                    .toEntity(InvoiceResponseDto.class);
            if (responseEntity.getStatusCode().value() == HttpStatus.OK.value() && responseEntity.getBody() != null) {
                return responseEntity.getBody();
            }
            throw new IntegrationException("An unexpected error has occurred while create prime invoice to Smartpos billing service. Error:" + responseEntity, null, HttpStatus.valueOf(responseEntity.getStatusCode().value()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while create prime invoice to Smartpos billing service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Object> customerOffer(BillingAppType appType, String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/customer-public-offer/customer/tin/{tin}/app-type/{appType}")
                        .build(Map.of("tin", tin, "appType", appType.getCode()))
                )
                .headers(this.convertToConsumer())
                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
//                    if (response.getStatusCode() == HttpStatus.NOT_FOUND)
//                        throw new NotFoundException(response.getStatusCode().toString(), response.getStatusText());
//                    else
//                        throw new BadRequestException(response.getStatusText());
//                })
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> customerCheck(String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/customers/by-tin/{tin}")
                        .build(Map.of("tin", tin))
                )
                .headers(this.convertToConsumer())
                .retrieve()
//                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
//                    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
//                        log.error("Billing 404 error : {}", response);
//                        throw new NotFoundException(response.getStatusCode().toString(), response.getStatusText());
//                    } else
//                        throw new BadRequestException(response.getStatusText());
//                })
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> customerCheck(String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/customers/by-tin/{tin}")
                        .build(Map.of("tin", tin))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.error("Billing 404 error : {}", response);
                        throw new NotFoundException(response.getStatusCode().toString(), response.getStatusText());
                    }else
                        throw new BadRequestException(response.getStatusText());
                })
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> createCustomer(BillingCustomerRequestDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/customers/init")
                .body(dto)
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getAllBalanceByTin(String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/ledger-detail/{tin}/get-all-balances")
                        .build(Map.of("tin", tin))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getInvoiceByAgreementId(Long id, IntegrationBaseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/get-by-agreement-id/{id}")
                        .queryParams(filter.getMultiValueMap())
                        .build(Map.of("id", id)))
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getByQuoteAgreementId(Long id, IntegrationBaseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/agreement/{id}")
                        .queryParams(filter.getMultiValueMap())
                        .build(Map.of("id", id)))
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Void> activateAgreement(ActivateAgreementRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements/activate")
                .body(dto)
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Void.class);
    }

    public ResponseEntity<Object> getXizmatsChildren(Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/xizmats/{id}/get-children")
                        .build(Map.of("id", id)))
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> createQuote(QuoteCreateRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/quote/create")
                .body(dto)
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(Object.class);
    }

    public List<BillingTransactionResponseDTO> getTransactionByTin(BillingTransactionFilter transactionFilter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/ledger-detail/{tin}/transactions")
                        .queryParams(transactionFilter.getMultiValueMap())
                        .build(Map.of("tin", transactionFilter.getTin()))
                )
                .headers(this.convertToConsumer())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<BillingTransactionResponseDTO>>() {
                })
                .getBody();
    }
}
