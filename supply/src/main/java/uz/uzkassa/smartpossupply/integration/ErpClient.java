package uz.uzkassa.smartpossupply.integration;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.smartpossupply.dto.auth.AdminLoginDTO;
import uz.uzkassa.smartpossupply.dto.auth.TokenDTO;
import uz.uzkassa.smartpossupply.dto.sync.company.CompanyBranchSyncDTO;
import uz.uzkassa.smartpossupply.dto.sync.company.CompanySyncDTO;
import uz.uzkassa.smartpossupply.enums.AppType;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;


@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ErpClient extends BaseClient {

    private static String token = null;

    public IntegrationType getIntegrationType() {
        return IntegrationType.ERP;
    }

    public String getToken() {
        if (token != null) {
            return token;
        }
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            AdminLoginDTO loginDTO = new AdminLoginDTO();
            loginDTO.setUsername(applicationProperties.getErpConfig().getUsername());
            loginDTO.setPassword(applicationProperties.getErpConfig().getPassword());
            loginDTO.setRememberMe(true);

            HttpHeaders headers = SecurityUtils.getHeader();
            HttpEntity<AdminLoginDTO> entity = new HttpEntity<>(loginDTO, headers);
            ResponseEntity<TokenDTO> response = restTemplate.exchange(
                    applicationProperties.getErpConfig().getAuthUrl(),
                    HttpMethod.POST,
                    entity,
                    TokenDTO.class
            );
            token = Objects.requireNonNull(response.getBody()).getAccessToken();
            return token;
        }, throwable -> {
            token = null;
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public Long syncCompany(CompanySyncDTO companySyncDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(applicationProperties.getErpConfig().getCustomerSyncUrl());
            uriBuilder.queryParam("relationId", companySyncDTO.getId());
            uriBuilder.queryParam("relationType", AppType.SUPPLY.getCode());
            HttpEntity<?> entity = new HttpEntity<>(companySyncDTO, headers);
            try {
                ResponseEntity<Long> response = restTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.POST,
                        entity,
                        Long.class
                );
                if (response.getStatusCode().is2xxSuccessful()) {
                    return response.getBody();
                }
                log.error("Company sync response error: {}", response);
                return null;
            } catch (Exception e) {
                log.error("Company sync response error: {}", e.getMessage());
                return null;
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            return null;
        });
    }

    public Long syncBranch(CompanyBranchSyncDTO companyBranchSyncDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(applicationProperties.getErpConfig().getCustomerBranchSyncUrl());
            uriBuilder.queryParam("relationId", companyBranchSyncDTO.getId());
            uriBuilder.queryParam("relationType", AppType.SUPPLY.getCode());
            HttpEntity<?> entity = new HttpEntity<>(companyBranchSyncDTO, headers);
            try {
                ResponseEntity<Long> response = restTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.POST,
                        entity,
                        Long.class
                );
                if (response.getStatusCode().is2xxSuccessful()) {
                    return response.getBody();
                }
                log.error("Branch sync response error: {}", response);
                return null;
            } catch (Exception e) {
                log.error("Branch sync response error: {}", e.getMessage());
                return null;
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            return null;
        });
    }
}