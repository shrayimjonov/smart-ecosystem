package uz.uzkassa.horeca.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.horeca.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.horeca.web.rest.errors.IntegrationException;

import java.io.IOException;

@Component
@Slf4j
@Lazy
@RequiredArgsConstructor
public class EimzoClient extends BaseClient {

    public Pkcs7Dto frontTimestamp(String sign) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(applicationProperties.getEimzoConfig().getHost())
                .path(applicationProperties.getEimzoConfig().getFrontendTimestampPath())
                .queryParam("X-Real-IP", applicationProperties.getEimzoConfig().getXRealIP())
                .queryParam("Host", applicationProperties.getEimzoConfig().getInternalHost());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.POST,
                    new HttpEntity<>(sign),
                    String.class
            );
            log.info("Front Timestamp :{}", response);
            if (HttpStatus.OK == response.getStatusCode()) {
                return objectMapper.readValue(response.getBody(), Pkcs7Dto.class);
            } else {
                log.error("Front timestamp sign: \n {}", sign);
                log.error("Front timestamp response: {}", response);
                throw new IntegrationException("Ошибка E-Imzo", response.getBody());
            }
        } catch (IOException e) {
            log.error("Front timestamp convert to JSON error: {}", e.getMessage());
            throw new IntegrationException("Ошибка E-Imzo", e.getMessage());
        }
    }

    public Pkcs7Dto backendVerifyAttached(String sign) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(applicationProperties.getEimzoConfig().getHost())
                .path(applicationProperties.getEimzoConfig().getBackendVerifyPath())
                .queryParam("X-Real-IP", applicationProperties.getEimzoConfig().getXRealIP())
                .queryParam("Host", applicationProperties.getEimzoConfig().getInternalHost());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.POST,
                    new HttpEntity<>(sign),
                    String.class
            );
            log.info("Backend Verify Attached :{}", response);

            if (HttpStatus.OK == response.getStatusCode()) {
                return objectMapper.readValue(response.getBody(), Pkcs7Dto.class);
            } else {
                log.error("Backend verify attached sign: \n {}", sign);
                log.error("Backend verify attached response: {}", response);
                throw new IntegrationException("Ошибка E-Imzo", response.getBody());
            }
        } catch (IOException e) {
            log.error("Backend verify attached convert to JSON error: {}", e.getMessage());
            throw new IntegrationException("Ошибка E-Imzo", e.getMessage());
        }
    }
}
