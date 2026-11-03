package uz.uzkassa.cts.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.cts.dto.apay.*;
import uz.uzkassa.cts.dto.apay.category.APayCategoryListDTO;
import uz.uzkassa.cts.dto.apay.category.APayCategoryListResponseDTO;
import uz.uzkassa.cts.dto.auth.LoginDTO;
import uz.uzkassa.cts.dto.auth.RefreshTokenDTO;
import uz.uzkassa.cts.dto.base.PagedResponseDTO;
import uz.uzkassa.cts.dto.sync.customer.CustomerSyncDTO;
import uz.uzkassa.cts.dto.sync.customerbranch.CustomerBranchSyncDTO;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.util.List;

@Component
@Slf4j
public class ApayClient extends BaseClient {
    private final RestTemplate restTemplate;

    public ApayClient(@Qualifier("apayRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String authenticate() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setLogin(applicationProperties.getApayProperty().getUsername());
        loginDTO.setPassword(applicationProperties.getApayProperty().getPassword());
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.set("device-id", applicationProperties.getApayProperty().getDeviceId());
        HttpEntity<LoginDTO> entity = new HttpEntity<>(loginDTO, headers);
        ResponseEntity<RefreshTokenDTO> response;
        try {
            response = restTemplate.exchange(applicationProperties.getApayProperty().getLoginUrl(), HttpMethod.POST, entity, RefreshTokenDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while authenticating to A-Pay service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (response.getBody() != null && response.getBody().getAccessToken() != null) {
            String token = response.getBody().getAccessToken();
            tokenStoreService.create(getIntegrationType(), token, applicationProperties.getApayProperty().getTokenExpiration());
            return token;
        }
        throw new IntegrationException("An unexpected error has occurred while authenticating to A-Pay service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
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
        return IntegrationType.APAY;
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBearerAuth(this.getToken());
        headers.set("device-id", applicationProperties.getApayProperty().getDeviceId());
        return headers;
    }

    public APayAddTransactionResponseDto add(APayAddTransactionRequestDto dto) {
        ResponseEntity<APayAddTransactionResponseDto> response = restTemplate.exchange(applicationProperties.getApayProperty().getAddUrl(), HttpMethod.POST, new HttpEntity<>(dto, getHeaders()), APayAddTransactionResponseDto.class);
        return response.getBody();
    }

    public APayAddTransactionResponseDto createBillTransaction(APayBillCreateRequestDTO dto) {
        ResponseEntity<APayAddTransactionResponseDto> response = restTemplate.exchange(applicationProperties.getApayProperty().getKkmUrlCreate(), HttpMethod.POST, new HttpEntity<>(dto, getHeaders()), APayAddTransactionResponseDto.class);
        return response.getBody();
    }

    public void updateBillTransaction(APayBillUpdateRequestDTO dto) {
        restTemplate.exchange(applicationProperties.getApayProperty().getKkmUrlUpdate(), HttpMethod.PUT, new HttpEntity<>(dto, getHeaders()), APayUpdateBillResponseDTO.class);
    }

    public APayPayResponseDto pay(APayPayRequestDto dto) {
        ResponseEntity<APayPayResponseDto> response = restTemplate.exchange(applicationProperties.getApayProperty().getPayUrl(), HttpMethod.PUT, new HttpEntity<>(dto, getHeaders()), APayPayResponseDto.class);
        return response.getBody();
    }

    public APayPayResponseDto confirm(APayPayRequestDto dto) {
        ResponseEntity<APayPayResponseDto> response = restTemplate.exchange(applicationProperties.getApayProperty().getKkmUrlConfirm(), HttpMethod.PUT, new HttpEntity<>(dto, getHeaders()), APayPayResponseDto.class);
        return response.getBody();
    }

    public APayPayResponseDto confirm(APayPayRequestDto dto) {
        ResponseEntity<APayPayResponseDto> response = restTemplate.exchange(
                applicationProperties.getApayProperty().getKkmUrlConfirm(),
                HttpMethod.PUT,
                new HttpEntity<>(dto, getHeaders()),
                APayPayResponseDto.class
        );
        return response.getBody();
    }

    public APayCheckResponseDto check(APayCheckRequestDto dto) {
        ResponseEntity<APayCheckResponseDto> response = restTemplate.exchange(applicationProperties.getApayProperty().getCheckUrl(), HttpMethod.POST, new HttpEntity<>(dto, getHeaders()), APayCheckResponseDto.class);
        return response.getBody();
    }

    public APayResponseDTO getByBillId(String billId) {
        log.info("A-PAY request: {}", applicationProperties.getApayProperty().getGetByBillId() + "/" + billId);
        ResponseEntity<APayResponseDTO> response = restTemplate.exchange(applicationProperties.getApayProperty().getGetByBillId() + "/" + billId, HttpMethod.GET, new HttpEntity<>(getHeaders()), APayResponseDTO.class);
        log.info("A_PAY response: {}", response);
        return response.getBody();
    }

    public OfdPaymentInfoResponseDTO fiscal(OfdBillFiscalRequestDTO requestDTO) {
        ResponseEntity<OfdPaymentInfoResponseDTO> response = restTemplate.exchange(applicationProperties.getApayProperty().getFiscalUrl(), HttpMethod.POST, new HttpEntity<>(requestDTO, getHeaders()), OfdPaymentInfoResponseDTO.class);
        return response.getBody();
    }

    public ResponseEntity<APayCheckCompanyDTO> checkCompanyExist(String tin) {
        return restTemplate.exchange(applicationProperties.getApayProperty().getCheckCompany() + tin, HttpMethod.GET, new HttpEntity<>(getHeaders()), APayCheckCompanyDTO.class);
    }

    public ResponseEntity<ApayQuickRegisterResponseDTO> quickRegister(APayCompanyRegisterDTO registerDTO) {
        return restTemplate.exchange(applicationProperties.getApayProperty().getQuickRegister(), HttpMethod.POST, new HttpEntity<>(registerDTO, getHeaders()), ApayQuickRegisterResponseDTO.class);
    }

    public void quickRegisterBranches(APayCompanyRegisterDTO registerDTO) {
        restTemplate.exchange(applicationProperties.getApayProperty().getQuickRegister() + "/branches", HttpMethod.POST, new HttpEntity<>(registerDTO, getHeaders()), Void.class);
    }

    public ResponseEntity<List<ApayTariffDTO>> getTariffs() {
        return restTemplate.exchange(applicationProperties.getApayProperty().getTariffList(), HttpMethod.GET, new HttpEntity<>(getHeaders()), new ParameterizedTypeReference<>() {
        });
    }

    public ResponseEntity<ApayTariffDTO> tariffById(String id) {
        return restTemplate.exchange(applicationProperties.getApayProperty().getTariff() + id, HttpMethod.GET, new HttpEntity<>(getHeaders()), ApayTariffDTO.class);
    }

    public PagedResponseDTO<APayCategoryListDTO> getCategories(Integer page, Integer size) {
        String url = applicationProperties.getApayProperty().getCategoryListUrl() + "?page=" + page + "&size=" + size;
        ResponseEntity<APayCategoryListResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(getHeaders()), APayCategoryListResponseDTO.class);
        return response.getBody();
    }

    public String syncCompany(CustomerSyncDTO companyDTO) {
        HttpEntity<CustomerSyncDTO> entity = new HttpEntity<>(companyDTO, getHeaders());
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(applicationProperties.getApayProperty().getCompanySyncUrl(), HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while syncing company to A-Pay service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (HttpStatus.OK != response.getStatusCode()) {
            throw new IntegrationException("An unexpected error has occurred while syncing company to A-Pay service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
        }
        return response.getBody();
    }

    public String syncBranch(CustomerBranchSyncDTO customerBranchSyncDTO) {
        HttpEntity<CustomerBranchSyncDTO> entity = new HttpEntity<>(customerBranchSyncDTO, getHeaders());
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(applicationProperties.getApayProperty().getMerchantSyncUrl(), HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while syncing branch to A-Pay service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (HttpStatus.OK != response.getStatusCode()) {
            throw new IntegrationException("An unexpected error has occurred while syncing branch to A-Pay service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
        }
        return response.getBody();
    }
}
