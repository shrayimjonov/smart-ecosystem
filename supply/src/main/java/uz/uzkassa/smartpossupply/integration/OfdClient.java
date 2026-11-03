package uz.uzkassa.smartpossupply.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.ofd.OfdAuthLoginDTO;
import uz.uzkassa.smartpossupply.dto.ofd.OfdAuthLoginResultDTO;
import uz.uzkassa.smartpossupply.dto.ofd.OfdCheckTinOfdResponseResultDTO;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import java.util.Objects;

@Component
@Slf4j
public class OfdClient extends BaseClient {

    public IntegrationType getIntegrationType() {
        return IntegrationType.OFD;
    }

    //@Transactional(readOnly = true)
    public String getToken() {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            OfdAuthLoginDTO dto = new OfdAuthLoginDTO();
            dto.setPassword(applicationProperties.getOfdConfig().getPassword());
            dto.setUsername(applicationProperties.getOfdConfig().getUsername());

            HttpHeaders headers = SecurityUtils.getHeader();
            HttpEntity<OfdAuthLoginDTO> entity = new HttpEntity<>(dto, headers);
            ResponseEntity<OfdAuthLoginResultDTO> response = restTemplate.exchange(
                    applicationProperties.getOfdConfig().getAuthLoginUrl(),
                    HttpMethod.POST,
                    entity,
                    OfdAuthLoginResultDTO.class
            );
            return Objects.requireNonNull(response.getBody()).getAccess_token();
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    //@Transactional(readOnly = true)
    public OfdCheckTinOfdResponseResultDTO check(String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            try {
                String url;
                if (SecurityUtils.isTin(tin)) {
                    url = applicationProperties.getOfdConfig().getCheckTinUrl().concat(tin).concat("&lang=uz");
                } else {
                    url = applicationProperties.getOfdConfig().getCheckPinflUrl().concat(tin).concat("&lang=uz");
                }
                HttpHeaders headers = SecurityUtils.getHeader();
                headers.setBearerAuth(getToken());
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<OfdCheckTinOfdResponseResultDTO> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        OfdCheckTinOfdResponseResultDTO.class
                );
                return response.getBody();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new IntegrationException(e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }
}