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
import uz.uzkassa.smartpossupply.dto.xtrace.XtraceCheckApiKeyResponse;
import uz.uzkassa.smartpossupply.dto.xtrace.XtraceRefreshApiKeyResponse;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class XtraceClient extends BaseClient {

    public IntegrationType getIntegrationType() {
        return IntegrationType.XTRACE;
    }

    public XtraceCheckApiKeyResponse checkApiKey(String apiKey, String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(apiKey);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString(applicationProperties.getXtraceConfig().getCheckApiKeyUrl().replace("{tin}", tin));
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<XtraceCheckApiKeyResponse> response;
            try {
                response = restTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.GET,
                        entity,
                        XtraceCheckApiKeyResponse.class
                );
            } catch (Exception e) {
                throw new BadRequestException("Xtrace check API key error ", e.getMessage());
            }
            if (response.getBody() == null) {
                throw new BadRequestException("An empty response from Xtrace");
            }
            if (!Boolean.TRUE.equals(response.getBody().isTinCorrect())) {
                throw new BadRequestException("API-Ключ не принадлежит данному организацию");
            }
            return response.getBody();
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public XtraceRefreshApiKeyResponse refreshApiKey(String apiKey, String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(apiKey);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString(applicationProperties.getXtraceConfig().getRefreshApiKeyUrl().replace("{tin}", tin));
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<XtraceRefreshApiKeyResponse> response;
            try {
                response = restTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.POST,
                        entity,
                        XtraceRefreshApiKeyResponse.class
                );
            } catch (Exception e) {
                throw new BadRequestException("Xtrace refresh API key error ", e.getMessage());
            }
            if (response.getBody() == null) {
                throw new BadRequestException("An empty response from Xtrace");
            }
            return response.getBody();
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }
}
