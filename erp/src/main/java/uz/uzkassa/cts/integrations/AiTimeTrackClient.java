package uz.uzkassa.cts.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.auth.AuthenticateDTO;
import uz.uzkassa.cts.dto.auth.TokenDTO;
import uz.uzkassa.cts.dto.employee.EmployeeListPhotoDTO;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

@Component
@Slf4j
public class AiTimeTrackClient extends BaseClient {

    public void syncEmployee(EmployeeListPhotoDTO employeeListPhotoDTO) {
        HttpEntity<EmployeeListPhotoDTO> entity = new HttpEntity<>(employeeListPhotoDTO, getHeaders());
        ResponseEntity<Void> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getAiTimeTrackProperty().getEmployeeSyncUrl(),
                    HttpMethod.PUT,
                    entity,
                    Void.class
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IntegrationException("An unexpected error has occurred while syncing employee to AiTimeTrack service. Error: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (HttpStatus.OK != response.getStatusCode()) {
            throw new IntegrationException("An unexpected error has occurred while syncing employee to AiTimeTrack service. Error: " + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
        }
    }

    @Override
    public String authenticate() {
        AuthenticateDTO authenticateDTO = new AuthenticateDTO();
        authenticateDTO.setUsername(applicationProperties.getAiTimeTrackProperty().getUsername());
        authenticateDTO.setPassword(applicationProperties.getAiTimeTrackProperty().getPassword());
        HttpEntity<AuthenticateDTO> entity = new HttpEntity<>(authenticateDTO, SecurityUtils.getHeader());
        ResponseEntity<TokenDTO> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getAiTimeTrackProperty().getAuthUrl(),
                    HttpMethod.POST,
                    entity,
                    TokenDTO.class
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IntegrationException("An unexpected error has occurred while authenticating to AiTimeTrack service. Error: " + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (response.getBody() != null && response.getBody().getAccessToken() != null) {
            String token = response.getBody().getAccessToken();
            tokenStoreService.create(getIntegrationType(), token, applicationProperties.getAiTimeTrackProperty().getTokenExpiration());
            return token;
        }
        throw new IntegrationException("An unexpected error has occurred while authenticating to AiTimeTrack service. Error: " + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
    }

    @Override
    public String getToken() {
        String token = tokenStoreService.getToken(getIntegrationType());
        return token != null ? token : authenticate();
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBearerAuth(getToken());
        return headers;
    }

    @Override
    public IntegrationType getIntegrationType() {
        return IntegrationType.AI_TIME_TRACK;
    }
}
