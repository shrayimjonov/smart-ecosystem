package uz.uzkassa.smartpossupply.integration;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.auth.JWTToken;
import uz.uzkassa.smartpossupply.dto.integration.LoginRequestDto;
import uz.uzkassa.smartpossupply.dto.launcher.LauncherTerminalRequestDto;
import uz.uzkassa.smartpossupply.dto.launcher.LauncherTerminalResponseDto;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.01.2025
 */
@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LauncherClient extends BaseClient {

    public IntegrationType getIntegrationType() {
        return IntegrationType.LAUNCHER;
    }

    public String getToken() {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpEntity<LoginRequestDto> entity = new HttpEntity<>(new LoginRequestDto(applicationProperties.getLauncher().getUsername(), applicationProperties.getLauncher().getPassword()), SecurityUtils.getHeader());
            ResponseEntity<JWTToken> response = restTemplate.exchange(
                    applicationProperties.getLauncher().getHost(),
                    HttpMethod.POST,
                    entity,
                    JWTToken.class
            );
            if (response.getBody() != null && response.getBody().getIdToken() != null) {
                return response.getBody().getIdToken();
            }
            throw new IntegrationException("An unexpected error has occurred while authenticating to Launcher service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException("An unexpected error has occurred while authenticating to Launcher service. Error:" + throwable.getMessage(), null, HttpStatus.BAD_REQUEST);
        });
    }

    public HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBearerAuth(this.getToken());
        return headers;
    }

    public void send(LauncherTerminalRequestDto dto) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            log.info("Launcher {} terminals: {}", dto.getAction(), String.join(", ", dto.getTerminalNumbers()));
            ResponseEntity<LauncherTerminalResponseDto> response = restTemplate.exchange(
                    applicationProperties.getLauncher().getHost().concat("/api/v1/admin/terminals/action"),
                    HttpMethod.POST,
                    new HttpEntity<>(dto, getHeaders()),
                    LauncherTerminalResponseDto.class);
            if (HttpStatus.OK != response.getStatusCode()) {
                throw new IntegrationException("Launcher exception: " + response.getStatusCode());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }
}
