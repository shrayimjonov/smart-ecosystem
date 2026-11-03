package uz.uzkassa.cts.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import uz.uzkassa.cts.dto.auth.LoginRememberMeDTO;
import uz.uzkassa.cts.dto.auth.TokenDTO;
import uz.uzkassa.cts.dto.sync.customer.CustomerSyncDTO;
import uz.uzkassa.cts.dto.sync.customerbranch.CustomerBranchSyncDTO;
import uz.uzkassa.cts.dto.sync.fiscalization.FiscalizationSyncDTO;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

@Component
@Slf4j
public class HorecaClient extends BaseClient {

    @Override
    public String authenticate() {
        LoginRememberMeDTO loginDTO = new LoginRememberMeDTO();
        loginDTO.setUsername(applicationProperties.getHorecaProperty().getUsername());
        loginDTO.setPassword(applicationProperties.getHorecaProperty().getPassword());
        loginDTO.setRememberMe(true);
        HttpEntity<LoginRememberMeDTO> entity = new HttpEntity<>(loginDTO, SecurityUtils.getHeader());
        ResponseEntity<TokenDTO> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getHorecaProperty().getAuthUrl(),
                    HttpMethod.POST,
                    entity,
                    TokenDTO.class
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while authenticating to Horeca service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (response.getBody() != null && response.getBody().getAccessToken() != null) {
            String token = response.getBody().getAccessToken();
            tokenStoreService.create(getIntegrationType(), token, applicationProperties.getHorecaProperty().getTokenExpiration());
            return token;
        }
        throw new IntegrationException("An unexpected error has occurred while authenticating to Horeca service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
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
        return IntegrationType.HORECA;
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBearerAuth(this.getToken());
        return headers;
    }

    public Long syncCompany(CustomerSyncDTO companyDTO) {
        HttpEntity<CustomerSyncDTO> entity = new HttpEntity<>(companyDTO, getHeaders());
        ResponseEntity<Long> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getHorecaProperty().getCompanySyncUrl(),
                    HttpMethod.POST,
                    entity,
                    Long.class
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while syncing company to Horeca service. Error:" + e.getMessage());
        }
        if (HttpStatus.OK != response.getStatusCode()) {
            throw new IntegrationException("An unexpected error has occurred while syncing company to Horeca service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
        }
        return response.getBody();
    }

    public String syncBranch(CustomerBranchSyncDTO customerBranchSyncDTO) {
        HttpEntity<CustomerBranchSyncDTO> entity = new HttpEntity<>(customerBranchSyncDTO, getHeaders());
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getHorecaProperty().getBranchSyncUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while syncing branch to Horeca service. Error:" + e.getMessage());
        }
        if (HttpStatus.OK != response.getStatusCode()) {
            throw new IntegrationException("An unexpected error has occurred while syncing branch to Horeca service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
        }
        return response.getBody();
    }

    public void syncFiscalization(FiscalizationSyncDTO fiscalizationSyncDTO) {
        HttpEntity<FiscalizationSyncDTO> entity = new HttpEntity<>(fiscalizationSyncDTO, getHeaders());
        ResponseEntity<Long> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getHorecaProperty().getFiscalizationSyncUrl(),
                    HttpMethod.POST,
                    entity,
                    Long.class
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while syncing branch to Horeca service. Error:" + e.getMessage());
        }
        if (HttpStatus.OK != response.getStatusCode()) {
            throw new IntegrationException("An unexpected error has occurred while syncing fiscalization to Horeca service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
        }
    }
}
