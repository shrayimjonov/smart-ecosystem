package uz.uzkassa.silen.integration.billing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.silen.config.httpclient.CustomRequest;
import uz.uzkassa.silen.config.httpclient.CustomResponse;
import uz.uzkassa.silen.dto.billingpermission.BillingErrorResponse;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.EntityNotFoundException;
import uz.uzkassa.silen.integration.BaseIntegrationClient;
import uz.uzkassa.silen.integration.billing.dto.LoginRequestDto;
import uz.uzkassa.silen.integration.billing.dto.TokenResponseDto;
import uz.uzkassa.silen.integration.billing.dto.agreement.ActivateAgreementRequestDto;
import uz.uzkassa.silen.integration.billing.dto.agreement.AgreementDTO;
import uz.uzkassa.silen.integration.billing.dto.agreement.AgreementManagerRequestDTO;
import uz.uzkassa.silen.integration.billing.dto.agreement.CreateAgreementDTO;
import uz.uzkassa.silen.integration.billing.dto.customerpublicoffer.CustomerPublicOfferCtoIdRequestDto;
import uz.uzkassa.silen.integration.billing.dto.customerpublicoffer.CustomerPublicOfferResponseDto;
import uz.uzkassa.silen.integration.billing.dto.publicoffer.PublicOfferResponseDto;
import uz.uzkassa.silen.integration.billing.dto.quote.QuoteChangeStatusRequestDto;
import uz.uzkassa.silen.integration.billing.dto.quote.QuoteCreateRequestDto;
import uz.uzkassa.silen.integration.billing.dto.quote.QuoteResponseDto;
import uz.uzkassa.silen.integration.billing.filter.*;
import uz.uzkassa.silen.integration.supplier.SupplierAuthPayload;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Component
@Slf4j
@Lazy
public class BillingClient extends BaseIntegrationClient {
    @Value("${application.billing.username}")
    String userName;

    @Value("${application.billing.password}")
    String password;

    static LocalDateTime authenticateRefreshTime;
    static String tokenStore;

    public BillingClient(CustomRequest customRequest, ObjectMapper objectMapper, @Value("${application.billing.url}") String startEndpoint) {
        super(customRequest, objectMapper, startEndpoint);
    }

    public BigDecimal getBalanceByTin(String tin) {
        final String url = getBaseUrl() + "/services/billing/api/ledger-detail/tin/" + tin + "/balance";
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, BigDecimal.class);
    }

    public Object getGroupedXizmats(XizmatFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/xizmats/grouped";
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    public Object getSingleXizmats(XizmatFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/xizmats/single";
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    public BigDecimal showCalculationByXizmatIdAndCustomerTin(Long xizmatId, String tin, String info, Integer instance) {
        String url = getBaseUrl() + "/services/billing/api/show-calculation/xizmat/" + xizmatId + "/customer/" + tin;
        List<NameValuePair> params = new ArrayList<>();
        if (StringUtils.isNotEmpty(info)) {
            params.add(new BasicNameValuePair("info", info));
        }
        if (instance != null) {
            params.add(new BasicNameValuePair("instance", String.valueOf(instance)));
        }
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), params);
        return convertToResponse(response, BigDecimal.class);
    }

    public CustomerPublicOfferResponseDto getCustomerOfferByTin(String tin) {
        String url = getBaseUrl() + "/services/billing/api/customer-public-offer/customer/tin/" + tin;

        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, CustomerPublicOfferResponseDto.class);
    }

    public Object getAllAgreementsPageable(AgreementFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/agreements-pageable";
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    public Object getledgerDetailTransactions(String tin, IntegrationBaseFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/ledger-detail/tin/" + tin + "/transactions";
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    public Object saveAgreement(CreateAgreementDTO agreement) {
        final String url = getBaseUrl() + "/services/billing/api/agreements";
        try {
            String json = objectMapper.writeValueAsString(agreement);
            CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), json);
            return convertToResponse(response, Object.class);
        } catch (JsonProcessingException e) {
            log.debug("Billing saveAgreement convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public Object createQuote(QuoteCreateRequestDto quoteCreateRequestDto) {
        final String url = getBaseUrl() + "/services/billing/api/quote/create";
        try {
            String json = objectMapper.writeValueAsString(quoteCreateRequestDto);
            CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), json);
            return convertToResponse(response, Object.class);
        } catch (JsonProcessingException e) {
            log.debug("Billing quoteCreateRequestDto convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public Object getAgreementById(Long id) {
        final String url = getBaseUrl() + "/services/billing/api/agreements/" + id;
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, Object.class);
    }

    public QuoteResponseDto getByQuoteId(String id) {
        final String url = getBaseUrl() + "/services/billing/api/quote/" + id;
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, QuoteResponseDto.class);
    }

    public Object getByQuoteAgreementId(String id, IntegrationBaseFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/quote/agreement/" + id;
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    public String getTemplateByQuoteId(String id) {
        final String url = getBaseUrl() + "/services/billing/api/quote/" + id + "/get-template";
        CustomResponse customResponse = customRequest.sendGetRequest(url, evaluateHeaders(null));
        if (HttpStatus.OK.value() == customResponse.getCode()) {
            return customResponse.getContent();
        } else {
            log.debug("GET Company balance by TIN response: {}", customResponse);
            throw new BadRequestException(StringUtils.isNotEmpty(customResponse.getErrorMessage()) ? customResponse.getErrorMessage() : customResponse.getContent());
        }
    }

    public QuoteResponseDto changeStatus(String id, QuoteChangeStatusRequestDto dto) {
        final String url = getBaseUrl() + "/services/billing/api/quote/" + id + "/change-status";
        try {
            String json = objectMapper.writeValueAsString(dto);
            CustomResponse response = customRequest.sendPutRequest(url, evaluateHeaders(null), json);
            return convertToResponse(response, QuoteResponseDto.class);
        } catch (JsonProcessingException e) {
            log.debug("Billing changeStatus convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public AgreementDTO cancelAgreement(Long id) {
        final String url = getBaseUrl() + "/services/billing/api/agreements/" + id + "/cancel";
        CustomResponse response = customRequest.sendPutRequest(url, evaluateHeaders(null), null);
        return convertToResponse(response, AgreementDTO.class);
    }


    public AgreementDTO activateAgreement(Long id) {
        final String url = getBaseUrl() + "/services/billing/api/agreements/" + id + "/activate";
        CustomResponse response = customRequest.sendPutRequest(url, evaluateHeaders(null), null);
        return convertToResponse(response, AgreementDTO.class);
    }

    public void activateAgreement(ActivateAgreementRequestDto activateAgreementRequestDto) {
        final String url = getBaseUrl() + "/services/billing/api/agreements/activate";
        try {
            String json = objectMapper.writeValueAsString(activateAgreementRequestDto);
            customRequest.sendPostRequest(url, evaluateHeaders(null), json);
        } catch (JsonProcessingException e) {
            log.debug("Billing agreement activate convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public Object getInvoiceByQuoteId(String id, IntegrationBaseFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/invoice/get-by-quote-id/" + id;

        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    private TokenResponseDto authorize() {
        String url = getBaseUrl() + "/auth/login";
        try {
            String json = objectMapper.writeValueAsString(new LoginRequestDto(userName, password));
            CustomResponse response = customRequest.sendPostRequest(url, null, json);
            return convertToResponse(response, TokenResponseDto.class);
        } catch (JsonProcessingException e) {
            log.debug("Billing authorize convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    protected Map<String, String> evaluateHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + getToken());
        return headers;
    }

    @Override
    protected String getBaseUrl() {
        return startEndpoint;
    }

    public Object getledgerDetailGruppedTransactions(LedgerDetailAppTypeFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/ledger-detail/grouped";
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    public Object getBonusesAmount(BonusResponseFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/get-bonuses-amount";
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    public Object getBonusesList(BonusResponseFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/bonuses-pageable";
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    public Object billingInvoiceList(BillingInvoiceFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/invoice/list";
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    public Long getBillingInvoiceCustomerCount(BillingInvoiceFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/invoice/get-customer-count-by-invoice-filter";
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Long.class);
    }

    public String getInvoiceTemplateById(String id) {
        final String url = getBaseUrl() + "/services/billing/api/invoice/" + id + "/get-template";
        CustomResponse customResponse = customRequest.sendGetRequest(url, evaluateHeaders(null));
        if (HttpStatus.OK.value() == customResponse.getCode()) {
            return customResponse.getContent();
        } else {
            log.debug("GET getInvoiceTemplateById by id response: {}", customResponse);
            throw new BadRequestException(StringUtils.isNotEmpty(customResponse.getErrorMessage()) ? customResponse.getErrorMessage() : customResponse.getContent());
        }
    }

    public Object checkBillingInvoiceXfileStatus(String id) {
        final String url = getBaseUrl() + "/services/billing/api/invoice/" + id + "/check-invoice-xfile-status";
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, Object.class);
    }

    public Object getLastActivePublicOffer(String documentType) {
        final String url = getBaseUrl() + "/services/billing/api/public-offer/get-last-active?documentType=" + documentType;
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, Object.class);
    }

    public CustomerPublicOfferResponseDto createCustomerOffer(CustomerPublicOfferCtoIdRequestDto dto) {
        final String url = getBaseUrl() + "/services/billing/api/customer-public-offer/create-by-tin";
        try {
            String json = objectMapper.writeValueAsString(dto);
            CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), json);
            return convertToResponse(response, CustomerPublicOfferResponseDto.class);
        } catch (JsonProcessingException e) {
            log.debug("Billing createCustomerOffer convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public Object startAgreementWithTin(String tin) {
        final String url = getBaseUrl() + "/services/billing/api/start-agreement/" + tin;
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, Object.class);
    }

    public String updateAgreementManager(AgreementManagerRequestDTO dto) {
        final String url = getBaseUrl() + "/services/billing/api/agreements/agreements-manager-update";
        try {
            String json = objectMapper.writeValueAsString(dto);
            CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), json);
            return convertToResponse(response, String.class);
        } catch (JsonProcessingException e) {
            log.debug("Billing updateAgreementManager convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public <T> T convertToResponse(CustomResponse customResponse, Class<T> toDto) {
        if (HttpStatus.CREATED.value() == customResponse.getCode()) {
            return super.convertToResponse(customResponse, toDto);
        }

        if (HttpStatus.NOT_FOUND.value() == customResponse.getCode()) {
            throw new EntityNotFoundException(parseError(customResponse));
        }
        if (HttpStatus.OK.value() != customResponse.getCode()) {
            throw new BadRequestException(parseError(customResponse));
        }
        return super.convertToResponse(customResponse, toDto);
    }

    private String parseError(final CustomResponse response) {
        if (StringUtils.isNotBlank(response.getErrorMessage())) {
            return response.getErrorMessage();
        }
        final String content = response.getContent();
        try {
            final BillingErrorResponse responseDTO = objectMapper.readValue(content, BillingErrorResponse.class);
            return Optional.ofNullable(responseDTO).map(BillingErrorResponse::getDetail).orElse(content);
        } catch (IOException e) {
            return content;
        }
    }

    public QuoteResponseDto uploadToQuote(String id, MultipartFile file) {
        final String url = getBaseUrl() + "/services/billing/api/quote/" + id + "/upload";

        CustomResponse response = customRequest.uploadFile(url, evaluateHeaders(null), file);
        return convertToResponse(response, QuoteResponseDto.class);
    }

    public CustomerPublicOfferResponseDto uploadFileToCustomerPublicOffer(String id, MultipartFile file) {
        final String url = getBaseUrl() + "/services/billing/api/customer-public-offer/" + id + "/upload";

        CustomResponse response = customRequest.uploadFile(url, evaluateHeaders(null), file);
        return convertToResponse(response, CustomerPublicOfferResponseDto.class);
    }

    public PublicOfferResponseDto getLastActiveOffer() {
        final String url = getBaseUrl() + "/services/billing/api/public-offer/get-last-active";

        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, PublicOfferResponseDto.class);
    }

    public Object getInvoiceByAgreementId(@NotEmpty @NotNull String id, IntegrationBaseFilter filter) {
        final String url = getBaseUrl() + "/services/billing/api/invoice/get-by-agreement-id/" + id;

        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null), filter.convertToParams());
        return convertToResponse(response, Object.class);
    }

    public Object getXizmatsChildren(Long id) {
        final String url = getBaseUrl() + "/services/billing/api/xizmats/" + id + "/get-children";

        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, Object.class);
    }

    public String getToken() {
        if (tokenStore != null && authenticateRefreshTime != null && authenticateRefreshTime.isAfter(LocalDateTime.now())) {
            return tokenStore;
        }

        try {
            TokenResponseDto response = authorize();
            if (response.getAccessToken() != null) {
                tokenStore = Objects.requireNonNull(response.getAccessToken());
                authenticateRefreshTime = LocalDateTime.now().plusSeconds(86400);
                return tokenStore;
            } else {
                tokenStore = null;
                throw new BadRequestException(response.toString());
            }
        } catch (Exception e) {
            tokenStore = null;
            throw new BadRequestException(e.getMessage());
        }
    }
}
