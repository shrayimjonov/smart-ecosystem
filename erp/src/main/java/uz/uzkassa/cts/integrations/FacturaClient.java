package uz.uzkassa.cts.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.factura.FacturaBankDTO;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.util.List;

@Slf4j
@Component
public class FacturaClient extends BaseClient {

    public List<FacturaBankDTO> getBanks(String lang) {
        ResponseEntity<List<FacturaBankDTO>> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getFacturaProperty().getHost().concat(lang).concat("/catalogs/bank"),
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders()),
                    new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while getting banks from Factura service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (HttpStatus.OK == response.getStatusCode()) {
            return response.getBody();
        }
        throw new IntegrationException("An unexpected error has occurred while getting banks from Factura service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBasicAuth(applicationProperties.getFacturaProperty().getUsername(), applicationProperties.getFacturaProperty().getPassword());
        return headers;
    }

    @Override
    public String authenticate() {
        return null;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public IntegrationType getIntegrationType() {
        return IntegrationType.FACTURA;
    }
}
