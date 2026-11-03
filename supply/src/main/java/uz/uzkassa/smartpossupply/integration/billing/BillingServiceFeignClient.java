package uz.uzkassa.smartpossupply.integration.billing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.dto.auth.TokenDTO;
import uz.uzkassa.smartpossupply.dto.integration.BillingCategoryTreeDto;
import uz.uzkassa.smartpossupply.dto.integration.BonusResponseFilter;
import uz.uzkassa.smartpossupply.dto.integration.LoginRequestDto;
import uz.uzkassa.smartpossupply.dto.integration.XizmatResponseDTO;
import uz.uzkassa.smartpossupply.dto.integration.agreement.*;
import uz.uzkassa.smartpossupply.dto.integration.billinginvoice.InvoiceResponseDto;
import uz.uzkassa.smartpossupply.dto.integration.customerpublicoffer.CustomerPublicOfferCtoIdRequestDto;
import uz.uzkassa.smartpossupply.dto.integration.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.smartpossupply.dto.integration.publicoffer.PublicOfferResponseDto;
import uz.uzkassa.smartpossupply.dto.integration.quote.QuoteChangeStatusRequestDto;
import uz.uzkassa.smartpossupply.dto.integration.quote.QuoteCreateRequestDto;
import uz.uzkassa.smartpossupply.dto.integration.quote.QuoteResponseDto;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.enums.BillingEntityType;
import uz.uzkassa.smartpossupply.filters.AgreementFilter;
import uz.uzkassa.smartpossupply.filters.BillingInvoiceFilter;
import uz.uzkassa.smartpossupply.filters.CategoryFilter;
import uz.uzkassa.smartpossupply.filters.LedgerDetailAppTypeFilter;
import uz.uzkassa.smartpossupply.integration.apay.request.APayPaymentAddRequestDto;
import uz.uzkassa.smartpossupply.integration.apay.request.APayPaymentPayRequestDto;
import uz.uzkassa.smartpossupply.integration.apay.response.APayAddTransactionResponseDto;
import uz.uzkassa.smartpossupply.integration.filter.BillingPageFilter;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class BillingServiceFeignClient {


    RestClient restClient;
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper objectMapper;


    public BillingServiceFeignClient(ApplicationProperties applicationProperties, @Qualifier("pooledIntegrationHttpClient") CloseableHttpClient httpClient, ObjectMapper objectMapper) {
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
        restClient = RestClient.builder()
                .baseUrl(applicationProperties.getBilling().getHost())
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    /*@PostMapping(value = "/auth/login")
    ResponseEntity<TokenDTO> login(@RequestBody LoginRequestDto dto);
     */

    public ResponseEntity<TokenDTO> login(LoginRequestDto dto) {
        return restClient.post()
                .uri("/auth/login")
                .body(dto)
                .retrieve()
                .toEntity(TokenDTO.class);
    }

    /*@GetMapping(value = "/services/billing/api/xizmats", produces = "application/json")
    Object getXizmats(@RequestHeader(value = "Authorization") String token, @SpringQueryMap XizmatFilter filter);
     */

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

    /*@GetMapping(value = "/services/billing/api/xizmats/grouped", produces = "application/json")
    Object getGroupedXizmats(@RequestHeader(value = "Authorization") String token, @SpringQueryMap XizmatFilter filter);
     */

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

    public ResponseEntity<Object> getXizmatChildren(String token, Long xizmatId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/xizmats/").path(xizmatId.toString()).path("/get-children")
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve().toEntity(Object.class);
    }

   /* @GetMapping(value = "/services/billing/api/xizmats/single", produces = "application/json")
    Object getSingleXizmats(@RequestHeader(value = "Authorization") String token, @SpringQueryMap XizmatFilter filter);
    */

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


    /*@GetMapping(value = "/services/billing/api/agreements", produces = "application/json")
    Object getAgreements(@RequestHeader(value = "Authorization") String token, @SpringQueryMap AgreementFilter agreementFilter);
     */
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

    public List<XizmatResponseDTO> getTariff(String tin) {
        ResponseEntity<String> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/get-grupped-xizmat-by-tin/".concat(tin).concat("/").concat(AppType.SMARTPOS.name()))
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, getToken())
                .retrieve().toEntity(String.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                return List.of(objectMapper.readValue(response.getBody(), XizmatResponseDTO[].class));
            } catch (JsonProcessingException e) {
                log.warn("Converting object error: {}", e.getMessage());
            }
        }
        throw new BadRequestException("Billing error");
    }

    /*@GetMapping(value = "/services/billing/api/agreements/{id}", produces = "application/json")
    Object getAgreementById(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);
     */

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

    /*@GetMapping(value = "/services/billing/api/agreements/{id}", produces = "application/json")
    PublicAgreementDTO getAgreementDtoById(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);
     */

    public PublicAgreementDTO getAgreementDtoById(String token, Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .body(PublicAgreementDTO.class);
    }

    /*@PutMapping(value = "/services/billing/api/agreements/{id}/update_subagreements", produces = "application/json")
    ResponseEntity<Object> updateAgreementById(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "id") Long id,
            @RequestBody List<UpdateSubAgreementDto> dtos
    );
     */
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

    /*@PostMapping(value = "/services/billing/api/agreements")
    ResponseEntity<Object> saveAgreement(@RequestHeader(value = "Authorization") String token, @RequestBody CreateAgreementDTO agreement);

     */

    public ResponseEntity<Object> saveAgreement(String token, CreateAgreementDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> saveAgreement(String token, CreateAgreementRequestDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxBillingError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxBillingError)
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> addExtraTerminalsAgreement(String token, CreateAgreementRequestDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/create-recalculation-supply")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxBillingError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxBillingError)
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> activateAgreement(String token, Long agreementId) {
        return restClient.post()
                .uri("/services/billing/api/agreements/activate")
                .body(new ActivateAgreementRequestDto(agreementId))
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    /* @PutMapping(value = "/services/billing/api/agreements")
     ResponseEntity<Object> updateAgreement(@RequestHeader(value = "Authorization") String token, @RequestBody CreateAgreementDTO agreement);

     */
    @PutMapping(value = "/services/billing/api/agreements")
    public ResponseEntity<Object> updateAgreement(String token, CreateAgreementDTO dto) {
        return restClient.put()
                .uri("/services/billing/api/agreements")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    /*@PostMapping(value = "/services/billing/api/agreements/multi")
    ResponseEntity<Object> saveAgreement(@RequestHeader(value = "Authorization") String token, @RequestBody CreateAgreementsDTO agreement);

     */

    public ResponseEntity<Object> saveAgreement(String token, CreateAgreementsDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements/multi")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    /*@PutMapping(value = "/services/billing/api/agreements/{id}/pause")
    AgreementDTO pauseAgreement(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

     */

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

    /*@PutMapping(value = "/services/billing/api/agreements/{id}/cancel")
    AgreementDTO cancelAgreement(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);

     */

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

    /*@PutMapping(value = "/services/billing/api/agreements/{id}/activate")
    AgreementDTO reactivateAgreement(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);
     */

    public AgreementDTO reactivateAgreement(String token, Long id) {
        return restClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/agreements/{id}/activate")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .body(AgreementDTO.class);
    }

    /// PublicOffer
    /*@GetMapping(value = "/services/billing/api/public-offer/get-last-active")
    ResponseEntity<PublicOfferResponseDto> getLastActiveOffer(@RequestHeader(value = "Authorization") String token);
*/
    public ResponseEntity<PublicOfferResponseDto> getLastActiveOffer(String token, String appType) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/public-offer/get-last-active-by-app-type")
                        .queryParam("documentType", "PUBLIC_OFFER")
                        .queryParam("appType", appType)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(PublicOfferResponseDto.class);
    }

    /// CustomerPublicOffer
    /*@GetMapping(value = "/services/billing/api/customer-public-offer/{id}")
    ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferById(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "id") String id
    );
     */
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferById(String token, String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/customer-public-offer/{id}")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(CustomerPublicOfferResponseDto.class);
    }

   /* @PostMapping(value = "/services/billing/api/customer-public-offer/create-by-tin")
    ResponseEntity<CustomerPublicOfferResponseDto> createCustomerOffer(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody CustomerPublicOfferCtoIdRequestDto dto
    );
    */

    public ResponseEntity<CustomerPublicOfferResponseDto> createCustomerOffer(String token, CustomerPublicOfferCtoIdRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/customer-public-offer/create-by-tin")
                .body(dto)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(CustomerPublicOfferResponseDto.class);
    }

    /*@GetMapping(value = "/services/billing/api/customer-public-offer/customer/cto/{id}")
    ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByCtoId(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "id") @NotNull Long id
    );
     */

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

    /*@GetMapping(value = "/services/billing/api/customer-public-offer/customer/tin/{tin}")
    ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByTin(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "tin") @NotNull String tin
    );
     */

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

    /*@GetMapping(value = "/services/billing/api/agreements/{id}/update-note")
    ResponseEntity<AgreementDTO> updateAgreementNote(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "id") @NotNull Long id,
            @RequestBody String note
    );
     */

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

    /// Quote
    /*@GetMapping(value = "/services/billing/api/quote/customer/cto/{id}")
    ResponseEntity<Page<QuoteResponseDto>> getByCustomerCtoId(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "id") String id,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size
    );
     */
    public ResponseEntity<QuoteResponseDto> getByCustomerCtoId(String token,
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
                .toEntity(QuoteResponseDto.class);
    }

    /*@GetMapping(value = "/services/billing/api/quote/customer/tin/{tin}")
    ResponseEntity<Page<QuoteResponseDto>> getByCustomerTin(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "tin") String tin,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size
    );
     */

    public ResponseEntity<QuoteResponseDto> getByCustomerTin(String token,
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
                .toEntity(QuoteResponseDto.class);
    }

    /*@PutMapping(value = "/services/billing/api/quote/{id}/change-status")
    ResponseEntity<QuoteResponseDto> changeStatus(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "id") String id,
            @RequestBody QuoteChangeStatusRequestDto dto
    );
    */

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

    /*@GetMapping(value = "/services/billing/api/quote/{id}")
    ResponseEntity<QuoteResponseDto> getByQuoteId(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "id") String id
    );
*/
    public ResponseEntity<QuoteResponseDto> getByQuoteId(String token, Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/{id}")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(QuoteResponseDto.class);
    }

    public ResponseEntity<Object> getQuoteByAgreement(String token, Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/quote/agreement/{id}")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    /* @GetMapping(value = "/services/billing/api/quote/{id}/get-template")
     ResponseEntity<String> getTemplateByQuoteId(
             @RequestHeader(value = "Authorization") String token,
             @PathVariable(value = "id") String id
     );
 */
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

    /* @PostMapping(value = "/services/billing/api/quote/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     ResponseEntity<QuoteResponseDto> uploadToQuote(
             @RequestHeader(value = "Authorization") String token,
             @PathVariable(value = "id") String id,
             @RequestPart(value = "file") MultipartFile file
     );
 */
    public ResponseEntity<QuoteResponseDto> uploadToQuote(String token, String id, MultipartFile file) {
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
                .toEntity(QuoteResponseDto.class);
    }

    /// INVOICE
    /*@GetMapping(value = "/services/billing/api/invoice/customer/cto/{id}")
    ResponseEntity<Page<InvoiceResponseDto>> getInvoiceByCustomerCtoId(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "id") String id,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size
    );
*/
    public ResponseEntity<InvoiceResponseDto> getInvoiceByCustomerCtoId(String token,
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
                .toEntity(InvoiceResponseDto.class);
    }

    /*@GetMapping(value = "/services/billing/api/invoice/customer/tin/{tin}")
    ResponseEntity<Page<InvoiceResponseDto>> getInvoiceByCustomerTin(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "tin") String tin,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size
    );
*/
    public ResponseEntity<InvoiceResponseDto> getInvoiceByCustomerTin(String token,
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
                .toEntity(InvoiceResponseDto.class);
    }

    /*  @GetMapping(value = "/services/billing/api/invoice/get-by-quote-id/{id}")
      ResponseEntity<Page<InvoiceResponseDto>> getInvoiceByQuoteId(
              @RequestHeader(value = "Authorization") String token,
              @PathVariable(value = "id") String id,
              @RequestParam(value = "page") Integer page,
              @RequestParam(value = "size") Integer size
      );
  */
    public ResponseEntity<Object> getInvoiceByQuoteId(String token,
                                                      String id,
                                                      Integer page,
                                                      Integer size) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/invoice/get-by-quote-id/{id}")
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> getInvoiceByAgreementId(String token,
                                                          Long id,
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

    /*@GetMapping(value = "/services/billing/api/customer-public-offer/customer/{id}")
    ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByCustomerId(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "id") @NotNull Long id
    );
*/
    public ResponseEntity<CustomerPublicOfferResponseDto> getCustomerOfferByCustomerId(String token, Long id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/customer-public-offer/customer/{id}")
                        .build(Map.of("id", id))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(CustomerPublicOfferResponseDto.class);
    }

    /*@GetMapping(value = "/services/billing/api/agreements/is-active/")
    ResponseEntity<List<PublicAgreementDTO>> getActiveAgreements(
            @RequestHeader(value = "Authorization") String token,
            @RequestParam(value = "ctoId", required = false) Long ctoId,
            @RequestParam("tin") String tin,
            @RequestParam("serviceType") String serviceType,
            @RequestParam("operatorId") Long smartposUserId
    );
*/
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
                        .queryParamIfPresent("operatorId", Optional.ofNullable(smartposUserId))
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public String getToken(String username, String password) {
        ResponseEntity<TokenDTO> responseEntity = login(new LoginRequestDto(username, password));
        if (responseEntity != null) {
            if (responseEntity.getBody() != null && StringUtils.isNotEmpty(responseEntity.getBody().getAccessToken())) {
                return "Bearer " + responseEntity.getBody().getAccessToken();
            }
            if (
                    !CollectionUtils.isEmpty(responseEntity.getHeaders()) &&
                            !CollectionUtils.isEmpty(responseEntity.getHeaders().get("authorization"))
            ) {
                return responseEntity.getHeaders().get("authorization").get(0);
            }
        }
        return null;
    }

    /* @GetMapping(value = "/services/billing/api/invoice/{id}/get-template")
     ResponseEntity<String> getInvoiceTemplateById(
             @RequestHeader(value = "Authorization") String token,
             @PathVariable(value = "id") String id
     );
 */
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

    /*@PostMapping(value = "/services/billing/api/attachments/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Object upload(
            @RequestHeader(value = "Authorization") String token,
            @RequestParam(value = "relatedTo") BillingEntityType relatedTo,
            @RequestParam(value = "relatedToId") String relatedToId,
            @RequestPart(value = "file") MultipartFile file
    );
*/
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

    /* @GetMapping(value = "/services/billing/api/agreements/active-ids")
     ResponseEntity<List<Long>> getActiveAgreementIdsByServiceType(
             @RequestHeader(value = "Authorization") String token,
             @RequestParam(value = "serviceType") String serviceType
     );
 */
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

    /* @Deprecated
     @GetMapping(value = "/services/billing/api/ledger-detail/cto/{id}/balance")
     ResponseEntity<BigDecimal> getBalanceByCtoId(@RequestHeader(value = "Authorization") String token, @PathVariable(value = "id") Long id);
 */
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

    /* @GetMapping(value = "/services/billing/api/ledger-detail/tin/{tin}/balance")
     ResponseEntity<BigDecimal> getBalanceByTin(
             @RequestHeader(value = "Authorization") String token,
             @PathVariable(value = "tin") String tin
     );
 */
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


    public ResponseEntity<Object> getBalancesByTin(String token, String tin) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/ledger-detail/{tin}/get-balances-by-company-type")
                        .build(Map.of("tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxBillingError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxBillingError)
                .toEntity(Object.class);
    }

    /*
     @PostMapping("/services/billing/api/a-pay-payments/create-transaction") ResponseEntity<APayAddTransactionResponseDto> createTransaction(
     @RequestHeader(value = "Authorization") String token,
     @RequestBody APayPaymentAddRequestDto dto
     );
     */

    public APayAddTransactionResponseDto createTransaction(String token, APayPaymentAddRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/a-pay-payments/create-transaction")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(dto)
                .retrieve()
                .body(APayAddTransactionResponseDto.class);
    }

    /*
     @PostMapping("/services/billing/api/a-pay-payments/confirm-transaction") void acceptPayment(@RequestHeader(value = "Authorization") String token, @RequestBody APayPaymentPayRequestDto dto);
     */
    public void acceptPayment(String token, APayPaymentPayRequestDto dto) {
        restClient.post()
                .uri("/services/billing/api/a-pay-payments/confirm-transaction")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(dto)
                .retrieve()
                .body(Void.class);
    }

    /*
     @PostMapping("/services/billing/api/agreements/agreements-manager-update") ResponseEntity<String> updateAgreementManager(
     @RequestHeader(value = "Authorization") String token,
     @RequestBody AgreementManagerRequestDTO dto
     );
     */
    public String updateAgreementManager(String token, AgreementManagerRequestDTO dto) {
        return restClient.post()
                .uri("/services/billing/api/agreements/agreements-manager-update")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(dto)
                .retrieve()
                .body(String.class);
    }

    /*
     @GetMapping(value = "/services/billing/api/agreements-pageable")
     ResponseEntity<Page<Object>> getAllAgreementsPageable(
     @RequestHeader(value = "Authorization") String token,
     @SpringQueryMap AgreementFilter filter
     );
     */
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

    /*
     @GetMapping(value = "/services/billing/api/ledger-detail/tin/{tin}/transactions")
     ResponseEntity<Object> getLedgerDetailTransactions(
     @RequestHeader(value = "Authorization") String token,
     @PathVariable(value = "tin") String tin,
     @SpringQueryMap BillingPageFilter filter
     );
     */
    public ResponseEntity<Object> getLedgerDetailTransactions(String token, String tin, BillingPageFilter filter) {
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

    /*
     @GetMapping(value = "/services/billing/api/category/list-of-categories-by-current-app")
     Object listOfCategoriesByCurrentApp(@RequestHeader(value = "Authorization") String token, @SpringQueryMap CategoryFilter filter);
     */
    public Object listOfCategoriesByCurrentApp(String token, CategoryFilter filter) {
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

    /*
     @GetMapping(value = "/services/billing/api/category/tree-of-categories-by-current-app")
     ResponseEntity<List<BillingCategoryTreeDto>> treeOfCategoriesByCurrentApp(
     @RequestHeader(value = "Authorization") String token,
     @SpringQueryMap CategoryFilter filter
     );
     */

    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfCategoriesByCurrentApp(String token, CategoryFilter filter) {
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

    /*
     @GetMapping(value = "/services/billing/api/category/tree-of-active-categories-by-current-app")
     ResponseEntity<List<BillingCategoryTreeDto>> treeOfActiveCategoriesByCurrentApp(
     @RequestHeader(value = "Authorization") String token,
     @SpringQueryMap CategoryFilter filter
     );
     */

    public ResponseEntity<List<BillingCategoryTreeDto>> treeOfActiveCategoriesByCurrentApp(String token, CategoryFilter filter) {
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

    /*
     @GetMapping(value = "/services/billing/api/bonuses-pageable")
     ResponseEntity<Page<Object>> getBonusesList(
     @RequestHeader(value = "Authorization") String token,
     @SpringQueryMap BonusResponseFilter filter);
     */

    public ResponseEntity<Object> getBonusesList(String token, BonusResponseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/bonuses-pageable")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Object.class);
    }


    /*
     @GetMapping(value = "/services/billing/api/get-bonuses-amount")
     ResponseEntity<BigDecimal> getBonusesAmount(
     @RequestHeader(value = "Authorization") String token,
     @SpringQueryMap BonusResponseFilter filter);
     */
    public ResponseEntity<BigDecimal> getBonusesAmount(String token, BonusResponseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/get-bonuses-amount")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(BigDecimal.class);
    }

    /*
     @GetMapping(value = "/services/billing/api/get-invoice-amount-by-bonus")
     ResponseEntity<BigDecimal> getInvoiceAmountByBonus(
     @RequestHeader(value = "Authorization") String token,
     @SpringQueryMap BonusResponseFilter filter);
     */
    public ResponseEntity<BigDecimal> getInvoiceAmountByBonus(String token, BonusResponseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/get-invoice-amount-by-bonus")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(BigDecimal.class);
    }

    /*
     @GetMapping(value = "/services/billing/api/get-all-invoice-amount-by-bonusfilter")
     ResponseEntity<BigDecimal> getAllInvoiceAmountByBonusFilter(
     @RequestHeader(value = "Authorization") String token,
     @SpringQueryMap BonusResponseFilter filter);
     */

    public ResponseEntity<BigDecimal> getAllInvoiceAmountByBonusFilter(String token, BonusResponseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/get-all-invoice-amount-by-bonusfilter")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(BigDecimal.class);
    }


    public ResponseEntity<Long> getAllInvoiceCountByBonusFilter(String token, BonusResponseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/get-all-invoice-count-by-bonusfilter")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Long.class);
    }


    public ResponseEntity<Long> getAllCompanyCountByBonusFilter(String token, BonusResponseFilter filter) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/get-all-customer-count-by-bonusfilter")
                        .queryParams(filter.getMultiValueMap())
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .toEntity(Long.class);
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

    public ResponseEntity<Object> showCalculationByXizmatIdAndCustomerTin(String token, Long xizmatId, String tin, String info, Integer instance, Long parentAgreementId, String idForInstanceCheck, String tinForInstanceCheck) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/billing/api/show-calculation/xizmat/{xizmatId}/customer/{tin}")
                        .queryParamIfPresent("info", Optional.ofNullable(info))
                        .queryParamIfPresent("instance", Optional.ofNullable(instance))
                        .queryParamIfPresent("parentAgreementId", Optional.ofNullable(parentAgreementId))
                        .queryParamIfPresent("idForInstanceCheck", Optional.ofNullable(idForInstanceCheck))
                        .queryParamIfPresent("tinForInstanceCheck", Optional.ofNullable(tinForInstanceCheck))
                        .build(Map.of("xizmatId", xizmatId, "tin", tin))
                )
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(HttpHeaders.ACCEPT_LANGUAGE, SecurityUtils.getLocale().getLanguage())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxBillingError)
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxBillingError)
                .toEntity(Object.class);
    }

    public ResponseEntity<Object> createQuote(String token, QuoteCreateRequestDto dto) {
        return restClient.post()
                .uri("/services/billing/api/quote/create")
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(dto)
                .retrieve()
                .toEntity(Object.class);
    }

    public String getToken() {
        return this.getToken(
                applicationProperties.getBilling().getUsername(),
                applicationProperties.getBilling().getPassword()
        );
    }

    private void handle4xxBillingError(HttpRequest req, ClientHttpResponse res) throws IOException {
        byte[] body = res.getBody().readAllBytes();
        log.warn("BILLING SERVICE RESPONSE WITH 4XX ERROR CODE: {}", new String(body));
        BillingExceptionDTO billingExceptionDTO = objectMapper.readValue(body, BillingExceptionDTO.class);
        billingExceptionDTO.setTitle(billingExceptionDTO.getTitle() != null ? billingExceptionDTO.getTitle() : "billing.validation");
        throw new BillingIntegrationException(billingExceptionDTO);
    }

    private void handle5xxBillingError(HttpRequest req, ClientHttpResponse res) throws IOException {
        byte[] body = res.getBody().readAllBytes();
        log.error("BILLING SERVICE RESPONSE WITH 5XX ERROR CODE: {}", new String(body));
        BillingExceptionDTO billingExceptionDTO = new BillingExceptionDTO();
        billingExceptionDTO.setTitle("billing.error");
        billingExceptionDTO.setMessage(res.getStatusCode().toString());
        throw new BillingIntegrationException(billingExceptionDTO);
    }
}
