package uz.uzkassa.cts.integrations;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.auth.AuthenticateDTO;
import uz.uzkassa.cts.dto.auth.IdTokenDTO;
import uz.uzkassa.cts.dto.launcher.LauncherTerminalRequestDto;
import uz.uzkassa.cts.dto.launcher.LauncherTerminalResponseDto;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.TokenUtils;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

@Component
@Slf4j
public class LauncherClient extends BaseClient {

    @Override
    public String authenticate() {
        AuthenticateDTO loginDTO = new AuthenticateDTO();
        loginDTO.setUsername(applicationProperties.getLauncherProperty().getUsername());
        loginDTO.setPassword(applicationProperties.getLauncherProperty().getPassword());
        HttpEntity<AuthenticateDTO> entity = new HttpEntity<>(loginDTO, SecurityUtils.getHeader());
        ResponseEntity<IdTokenDTO> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getLauncherProperty().getAuthenticateUrl(),
                    HttpMethod.POST,
                    entity,
                    IdTokenDTO.class
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while authenticating to Launcher service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (response.getBody() != null && response.getBody().getIdToken() != null) {
            String token = response.getBody().getIdToken();
            tokenStoreService.create(getIntegrationType(), token, TokenUtils.getExpirationInSeconds(token));
            return token;
        }
        throw new IntegrationException("An unexpected error has occurred while authenticating to Launcher service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
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
    public HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBearerAuth(this.getToken());
        return headers;
    }

    @Override
    public IntegrationType getIntegrationType() {
        return IntegrationType.LAUNCHER;
    }

    public void send(LauncherTerminalRequestDto dto) {
        log.info("List of {}: {}", dto.getAction(), String.join(", ", dto.getTerminalNumbers()));

        ResponseEntity<LauncherTerminalResponseDto> response = restTemplate.exchange(
                applicationProperties.getLauncherProperty().getActionUrl(),
                HttpMethod.POST,
                new HttpEntity<>(dto, getHeaders()),
                LauncherTerminalResponseDto.class);

        if (HttpStatus.OK != response.getStatusCode()){
            throw new AmqpRejectAndDontRequeueException("Launcher exception: " + response.getStatusCode());
        }
    }

    public void updateBankType(LauncherTerminalRequestDto dto) {
        try {
            log.info("Object launcher: {}", objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ResponseEntity<LauncherTerminalResponseDto> response = restTemplate.exchange(
                applicationProperties.getLauncherProperty().getUpdateTerminal(),
                HttpMethod.PUT,
                new HttpEntity<>(dto, getHeaders()),
                LauncherTerminalResponseDto.class);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new AmqpRejectAndDontRequeueException("Launcher exception: " + response.getStatusCode());
    }
}
