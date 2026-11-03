package uz.uzkassa.smartpos.integration.apay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilderFactory;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.dto.AnonymousPaymentDTO;
import uz.uzkassa.smartpos.dto.EpsServicesDto;
import uz.uzkassa.smartpos.dto.EpsServicesLookupDto;
import uz.uzkassa.smartpos.dto.filter.RestPage;
import uz.uzkassa.smartpos.integration.apay.request.*;
import uz.uzkassa.smartpos.integration.apay.response.*;
import uz.uzkassa.smartpos.utils.LanguageUtils;
import uz.uzkassa.smartpos.web.rest.vm.TokenDTO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class ApayClient {
    private final RestTemplate restTemplate;
    private final UriBuilderFactory uriBuilderFactory;
    private final ApplicationProperties applicationProperties;

    public ApayClient(@Qualifier("apayRestTemplate") RestTemplate restTemplate, UriBuilderFactory uriBuilderFactory, ApplicationProperties applicationProperties) {
        this.restTemplate = restTemplate;
        this.uriBuilderFactory = uriBuilderFactory;
        this.applicationProperties = applicationProperties;
    }

    public ResponseEntity<TokenDTO> login(ApayCredential credential) {
        return restTemplate.postForEntity("/admin/v1/account/login",
                new HttpEntity<>(credential, evaluateHeaders(null)),
                TokenDTO.class);
    }

    public ResponseEntity<ApayCreateBillResponse> createBill(String token, ApayCreateBillRequest request) {
        return restTemplate.postForEntity("/admin/v1/a_pay/bills/Create",
                new HttpEntity<>(request, evaluateHeaders(token)),
                ApayCreateBillResponse.class);
    }

    public ResponseEntity<ApayUpdateBillResponse> updateBill(String token, ApayUpdateBillRequest request) {
        return restTemplate.exchange("/admin/v1/a_pay/bills/update",
                HttpMethod.PUT,
                new HttpEntity<>(request, evaluateHeaders(token)),
                ApayUpdateBillResponse.class);
    }

    public void attachCheck(String token, ApayAttachCheckRequest request) {
        restTemplate.exchange("/admin/v1/a_pay/bills/attach-check",
                HttpMethod.PUT,
                new HttpEntity<>(request, evaluateHeaders(token)),
                Void.class);
    }

    public ResponseEntity<ApayPayBillResponse> payBill(String token, ApayPayBillRequest request) {
        return restTemplate.exchange("/admin/v1/a_pay/bills/pay-bill",
                HttpMethod.PUT,
                new HttpEntity<>(request, evaluateHeaders(token)),
                ApayPayBillResponse.class);
    }

    public ResponseEntity<List<ApayCardSimpleResponse>> getUserCards(String token, String nfc, String pin, String billId) {
        return restTemplate.exchange(uriBuilderFactory.builder().path("/admin/v1/a_pay/bills/cards")
                        .queryParamIfPresent("nfc", Optional.ofNullable(nfc))
                        .queryParamIfPresent("pin", Optional.ofNullable(pin))
                        .queryParamIfPresent("billId", Optional.ofNullable(billId))
                        .toUriString(),
                HttpMethod.GET, new HttpEntity<>(evaluateHeaders(token)), new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<ApayBankLogoDTO> getCardById(String token, String pan) {
        return restTemplate.exchange(uriBuilderFactory.builder().path("/admin/v1/a_pay/bills/card/logo")
                        .queryParamIfPresent("pan", Optional.ofNullable(pan))
                        .toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(evaluateHeaders(token)),
                ApayBankLogoDTO.class);
    }

    public ResponseEntity<ApayVoucherResponse> getVoucher(String token, ApayVoucherRequest request) {
        return restTemplate.postForEntity("/admin/v1/a_pay/bills/voucher",
                new HttpEntity<>(request, evaluateHeaders(token)),
                ApayVoucherResponse.class);
    }

    public ResponseEntity<ApaySubmitApplicationResponseDTO> registerCompany(String token, CompanyRegisterDTO request) {
        return restTemplate.postForEntity("/business/v1/account/quick/register",
                new HttpEntity<>(request, evaluateHeaders(token)),
                ApaySubmitApplicationResponseDTO.class);
    }


    public void registerBranches(String token, CompanyRegisterDTO request) {
        restTemplate.postForEntity("/business/v1/account/quick/register/branches",
                new HttpEntity<>(request, evaluateHeaders(token)),
                Void.class);
    }

    public ResponseEntity<CheckCompanyDTO> checkCompanyExist(String token, String tin) {
        return restTemplate.exchange(uriBuilderFactory.builder().path("/business/v1/companies/check/{tin}")
                        .build(Map.of("tin", tin))
                        .toString(),
                HttpMethod.GET,
                new HttpEntity<>(evaluateHeaders(token)),
                CheckCompanyDTO.class);
    }

    public ResponseEntity<BillCheckStatusResponseDTO> checkBillStatus(String token, BillIdRequestDTO request) {
        return restTemplate.postForEntity("/admin/v1/a_pay/bills/check/status",
                new HttpEntity<>(request, evaluateHeaders(token)),
                BillCheckStatusResponseDTO.class);
    }


    public List<EpsServicesLookupDto> paymentProviders(String token, Long ctoId, String referenceId, String referenceType) {
        return restTemplate.exchange(uriBuilderFactory.builder()
                                .path("/admin/v1/eps-services/lookup")
                                .queryParamIfPresent("ctoId", Optional.ofNullable(ctoId))
                                .queryParamIfPresent("referenceId", Optional.ofNullable(referenceId))
                                .queryParamIfPresent("referenceType", Optional.ofNullable(referenceType))
                                .toUriString(),
                        HttpMethod.GET,
                        new HttpEntity<>(evaluateHeaders(token)),
                        new ParameterizedTypeReference<List<EpsServicesLookupDto>>() {
                        })
                .getBody();
    }


    public RestPage<EpsServicesDto> paymentProvidersList(String token, Integer page, Integer size) {
        return restTemplate.exchange(uriBuilderFactory.builder()
                        .path("/admin/v1/eps-services")
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(evaluateHeaders(token)),
                new ParameterizedTypeReference<RestPage<EpsServicesDto>>() {
                }).getBody();
    }

    public ResponseEntity<List<ApayTariffDTO>> tariffs(String token) {
        return restTemplate.exchange("/business/v1/tariffs",
                HttpMethod.GET,
                new HttpEntity<>(evaluateHeaders(token)),
                new ParameterizedTypeReference<>() {
                });
    }


    public ResponseEntity<ApayTariffDTO> tariffById(String token, String id) {
        return restTemplate.exchange(uriBuilderFactory.builder()
                        .path("/business/v1/tariffs/{id}")
                        .build(Map.of("id", id))
                        .toString(),
                HttpMethod.GET,
                new HttpEntity<>(evaluateHeaders(token)),
                ApayTariffDTO.class);
    }

    public ResponseEntity<AnonymousPaymentDTO> anonymousPayment(String token, String billId) {
        return restTemplate.exchange(uriBuilderFactory.builder()
                        .path("/admin/v1/bills/epos-anonymous-payment")
                        .queryParamIfPresent("billId", Optional.ofNullable(billId))
                        .toUriString(),
                HttpMethod.GET,
                new HttpEntity<>(evaluateHeaders(token)),
                AnonymousPaymentDTO.class);
    }

    public String getToken(String username, String password) {
        ResponseEntity<TokenDTO> responseEntity = login(new ApayCredential(username, password));
        return Objects.requireNonNull(responseEntity.getBody()).getAccessToken();
    }

    public HttpHeaders evaluateHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, token);
        headers.set("language", LanguageUtils.getLanguage().name());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("device-id", applicationProperties.getApay().getDeviceId());
        return headers;
    }
}
