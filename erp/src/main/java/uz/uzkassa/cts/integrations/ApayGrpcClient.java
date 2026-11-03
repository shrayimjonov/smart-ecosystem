package uz.uzkassa.cts.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.cts.dto.apay.APayBranchRequestDTO;
import uz.uzkassa.cts.dto.auth.ApayGrpcTokenDTO;
import uz.uzkassa.cts.dto.auth.AuthenticateDTO;
import uz.uzkassa.cts.dto.customer.CustomerBranchResponseAPayDTO;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.util.List;

@Component
@Slf4j
public class ApayGrpcClient extends BaseClient {
    private final RestTemplate restTemplate;

    public ApayGrpcClient(@Qualifier("apayRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String authenticate() {
        AuthenticateDTO authenticateDTO = new AuthenticateDTO();
        authenticateDTO.setUsername(applicationProperties.getApayGrpcProperty().getUsername());
        authenticateDTO.setPassword(applicationProperties.getApayGrpcProperty().getPassword());
        HttpEntity<AuthenticateDTO> entity = new HttpEntity<>(authenticateDTO, SecurityUtils.getHeader());
        ResponseEntity<ApayGrpcTokenDTO> response;
        try {
            response = restTemplate.exchange(applicationProperties.getApayGrpcProperty().getLoginUrl(), HttpMethod.POST, entity, ApayGrpcTokenDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while authenticating to A-Pay GRPC service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (response.getBody() != null && response.getBody().getJwtToken() != null) {
            String token = response.getBody().getJwtToken();
            tokenStoreService.create(getIntegrationType(), token, applicationProperties.getApayGrpcProperty().getTokenExpiration());
            return token;
        }
        throw new IntegrationException("An unexpected error has occurred while authenticating to A-Pay GRPC service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
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
        return IntegrationType.APAY_GRPC;
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBearerAuth(this.getToken());
        return headers;
    }

    public void sendCustomerBranches(List<CustomerBranchResponseAPayDTO> responseAPayDTOList) {
        restTemplate.exchange(applicationProperties.getApayGrpcProperty().getCustomerBranch() + "/save", HttpMethod.POST, new HttpEntity<>(responseAPayDTOList, getHeaders()), Void.class);
    }

    public void createCustomerBranches(CustomerBranchResponseAPayDTO branchResponseAPayDTO) {
        restTemplate.exchange(applicationProperties.getApayGrpcProperty().getCustomerBranch() + "/create", HttpMethod.POST, new HttpEntity<>(branchResponseAPayDTO, getHeaders()), Void.class);
    }

    public void updateCustomerBranches(CustomerBranchResponseAPayDTO branchResponseAPayDTO) {
        restTemplate.exchange(applicationProperties.getApayGrpcProperty().getCustomerBranch() + "/update", HttpMethod.POST, new HttpEntity<>(branchResponseAPayDTO, getHeaders()), Void.class);
    }

    public void deleteCustomerBranches(APayBranchRequestDTO aPayBranchRequestDTO) {
        restTemplate.exchange(applicationProperties.getApayGrpcProperty().getCustomerBranch() + "/delete", HttpMethod.DELETE, new HttpEntity<>(aPayBranchRequestDTO, getHeaders()), Void.class);
    }
}
