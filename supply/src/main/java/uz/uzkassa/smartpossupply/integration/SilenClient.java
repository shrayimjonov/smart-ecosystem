package uz.uzkassa.smartpossupply.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActUpdateStatusDTO;
import uz.uzkassa.smartpossupply.dto.auth.AdminLoginDTO;
import uz.uzkassa.smartpossupply.dto.auth.JWTToken;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
public class SilenClient extends BaseClient {

    static LocalDateTime authenticateRefreshTime;
    static String tokenStore;

    public IntegrationType getIntegrationType() {
        return IntegrationType.SILEN;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBearerAuth(getToken());
        return headers;
    }

    private String getToken() {
        if (tokenStore != null && authenticateRefreshTime != null && authenticateRefreshTime.isAfter(LocalDateTime.now())) {
            return tokenStore;
        }
        try {
            AdminLoginDTO dto = new AdminLoginDTO();
            dto.setPassword(applicationProperties.getSilenConfig().getPassword());
            dto.setUsername(applicationProperties.getSilenConfig().getUsername());
            dto.setRememberMe(true);

            HttpHeaders headers = SecurityUtils.getHeader();
            HttpEntity<AdminLoginDTO> entity = new HttpEntity<>(dto, headers);
            ResponseEntity<JWTToken> response = restTemplate.exchange(
                    applicationProperties.getSilenConfig().getHost().concat("/authenticate"),
                    HttpMethod.POST,
                    entity,
                    JWTToken.class
            );
            tokenStore = Objects.requireNonNull(response.getBody()).getIdToken();
            authenticateRefreshTime = LocalDateTime.now().plusSeconds(28800);
            return tokenStore;
        } catch (Exception e) {
            tokenStore = null;
            throw new IntegrationException("Authentication to Silen error", e.getMessage());
        }
    }

    public void updateAcceptanceTransferActStatus(AcceptanceTransferActUpdateStatusDTO updateStatusDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            String token = this.getToken();
            headers.setBearerAuth(token);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString(applicationProperties.getSilenConfig().getHost().concat("/acceptance-transfer-act/webhook"));
            HttpEntity<?> entity = new HttpEntity<>(updateStatusDTO, headers);
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.POST,
                        entity,
                        String.class
                );
                response.getBody();
            } catch (Exception e) {
                log.error("Updating acceptance transfer act status from Silen error :{}", e.getMessage());
                throw new IntegrationException("Updating acceptance transfer act status from Silen error", e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }
}
