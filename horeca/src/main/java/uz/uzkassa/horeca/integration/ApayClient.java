package uz.uzkassa.horeca.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.horeca.dto.apay.ApayLoginDTO;
import uz.uzkassa.horeca.dto.apay.ApayTokenDTO;
import uz.uzkassa.horeca.dto.base.RestPage;
import uz.uzkassa.horeca.dto.eps.EpsServiceDTO;
import uz.uzkassa.horeca.dto.terminal.*;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.web.rest.errors.IntegrationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class ApayClient extends BaseClient {

    static LocalDateTime authenticateRefreshTime;
    static String tokenStore;
    final UriBuilderFactory uriBuilderFactory;

    public ApayClient(final UriBuilderFactory uriBuilderFactory) {
        this.uriBuilderFactory = uriBuilderFactory;
    }

    public String getToken() {
        if (tokenStore != null && authenticateRefreshTime != null && authenticateRefreshTime.isAfter(LocalDateTime.now())) {
            return tokenStore;
        }

        ApayLoginDTO loginDTO = new ApayLoginDTO();
        loginDTO.setLogin(applicationProperties.getApayConfig().getUsername());
        loginDTO.setPassword(applicationProperties.getApayConfig().getPassword());
        MultiValueMap<String, String> headers = HeaderUtils.getHeader();
        headers.add("device-id", applicationProperties.getApayConfig().getDeviceId());
        HttpEntity<ApayLoginDTO> entity = new HttpEntity<>(loginDTO, headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(applicationProperties.getApayConfig().getHost())
                .path(applicationProperties.getApayConfig().getAuthPath());

        try {
            ResponseEntity<ApayTokenDTO> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.POST,
                    entity,
                    ApayTokenDTO.class
            );
            if (HttpStatus.OK == response.getStatusCode()) {
                tokenStore = Objects.requireNonNull(response.getBody()).getAccessToken();
                authenticateRefreshTime = LocalDateTime.now().plusDays(1);
                return tokenStore;
            } else {
                tokenStore = null;
                throw new IntegrationException(response.toString());
            }
        } catch (Exception e) {
            tokenStore = null;
            throw new IntegrationException(e.getMessage());
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = HeaderUtils.getHeader();
        String token = getToken();
        headers.setBearerAuth(token);
        headers.add("device-id", applicationProperties.getApayConfig().getDeviceId());
        log.info("Token: {}", token);
        return headers;
    }

    private String getBaseUrl() {
        return applicationProperties.getApayConfig().getHost() + "/api";
    }

    public List<EpsServiceDTO> getPaymentProviders(Integer page, Integer size) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(applicationProperties.getApayConfig().getHost())
                .path(applicationProperties.getApayConfig().getEpsServicesPath())
                .queryParam("page", page)
                .queryParam("size", size);

        try {
            ResponseEntity<RestPage<EpsServiceDTO>> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders()),
                    new ParameterizedTypeReference<>() {
                    }
            );
            if (HttpStatus.OK == response.getStatusCode()) {
                return response.getBody().getContent();
            } else {
                throw new IntegrationException(response.toString());
            }
        } catch (Exception e) {
            throw new IntegrationException("Get payment providers from Apay error", e.getMessage());
        }
    }

    public ResponseEntity<ApayCreateBillResponse> createBill(ApayCreateBillRequest request) {
        return restTemplate.postForEntity(getBaseUrl() + "/admin/v1/a_pay/bills/Create",
                new HttpEntity<>(request, getHeaders()),
                ApayCreateBillResponse.class);
    }

    public ResponseEntity<ApayUpdateBillResponse> updateBill(ApayUpdateBillRequest request) {
        return restTemplate.exchange(getBaseUrl() + "/admin/v1/a_pay/bills/update",
                HttpMethod.PUT,
                new HttpEntity<>(request, getHeaders()),
                ApayUpdateBillResponse.class);
    }

    public ResponseEntity<List<ApayCardSimpleResponse>> getUserCards(String nfc, String pin, String billId) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(getBaseUrl())
                .path("/admin/v1/a_pay/bills/cards")
                .queryParamIfPresent("nfc", Optional.ofNullable(nfc))
                .queryParamIfPresent("pin", Optional.ofNullable(pin))
                .queryParamIfPresent("billId", Optional.ofNullable(billId));
        return restTemplate.exchange(uriBuilder
                        .toUriString(),
                HttpMethod.GET, new HttpEntity<>(getHeaders()), new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<ApayPayBillResponse> payBill(ApayPayBillRequest request) {
        return restTemplate.exchange(getBaseUrl() + "/admin/v1/a_pay/bills/pay-bill",
                HttpMethod.PUT,
                new HttpEntity<>(request, getHeaders()),
                ApayPayBillResponse.class);
    }

    public ResponseEntity<BillCheckStatusResponseDTO> checkBillStatus(BillIdRequestDTO billIdRequestDTO) {
        return restTemplate.postForEntity(getBaseUrl() + "/admin/v1/a_pay/bills/check/status",
                new HttpEntity<>(billIdRequestDTO, getHeaders()),
                BillCheckStatusResponseDTO.class);
    }

    public void cancelBill(String billId) {
        restTemplate.put(getBaseUrl() + "/admin/v1/a_pay/bills/cancel-bill/".concat(billId),
                new HttpEntity<>(getHeaders()), Void.class);
    }

    public ResponseEntity<ApayBankLogoDTO> getCardById(String pan) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(getBaseUrl())
                .path("/admin/v1/a_pay/bills/card/logo")
                .queryParamIfPresent("pan", Optional.ofNullable(pan));
        return restTemplate.exchange(uriBuilder
                        .toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(getHeaders()),
                ApayBankLogoDTO.class);
    }

    public void attachReceipt(TerminalApayAttachCheckRequest request) {
        restTemplate.exchange(getBaseUrl() + "/admin/v1/a_pay/bills/attach-check",
                HttpMethod.PUT,
                new HttpEntity<>(request, getHeaders()),
                Void.class);
    }

    public ResponseEntity<AnonymousPaymentDTO> anonymousPayment(String billId) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(getBaseUrl())
                .path("/admin/v1/bills/epos-anonymous-payment")
                .queryParamIfPresent("billId", Optional.ofNullable(billId));
        log.error("URL: {}", uriBuilder.toUriString());
        ResponseEntity<AnonymousPaymentDTO> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(getHeaders()),
                AnonymousPaymentDTO.class);
        try {
            log.info("Apay response: {}", objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            log.error("Writing object error: {}", e.getMessage());
        }
        return response;
    }

    public ResponseEntity<CheckApayCompanyDTO> checkCompanyExist(String tin) {
        return restTemplate.exchange(uriBuilderFactory.builder().path("/business/v1/companies/check/{tin}")
                        .build(Map.of("tin", tin))
                        .toString(),
                HttpMethod.GET,
                new HttpEntity<>(getHeaders()),
                CheckApayCompanyDTO.class);
    }

    public void registerBranches(ApayCompanyRegisterDTO request) {
        restTemplate.postForEntity("/business/v1/account/quick/register/branches",
                new HttpEntity<>(request, getHeaders()),
                Void.class);
    }

    public ResponseEntity<ApaySubmitApplicationResponseDTO> registerCompany(ApayCompanyRegisterDTO request) {
        return restTemplate.postForEntity("/business/v1/account/quick/register",
                new HttpEntity<>(request, getHeaders()),
                ApaySubmitApplicationResponseDTO.class);
    }
}
