package uz.uzkassa.horeca.integration;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.horeca.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.web.rest.errors.IntegrationException;

@Component
public class SoliqClient extends BaseClient {

    public SoliqCompanyDTO getCompany(String tin) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(applicationProperties.getSoliqConfig().getHost())
                .path(applicationProperties.getSoliqConfig().getCompanyDetailPath())
                .queryParam(tin.length() == 9 ? "pinfl" : "tin", tin);

        try {
            ResponseEntity<SoliqCompanyDTO> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders()),
                    SoliqCompanyDTO.class
            );
            if (HttpStatus.OK == response.getStatusCode()) {
                return response.getBody();
            } else {
                throw new IntegrationException(response.toString());
            }
        } catch (Exception e) {
            throw new IntegrationException(e.getMessage());
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = HeaderUtils.getHeader();
        headers.setBasicAuth(applicationProperties.getSoliqConfig().getUsername(), applicationProperties.getSoliqConfig().getPassword());
        return headers;
    }
}
