package uz.uzkassa.cts.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import static uz.uzkassa.cts.security.SecurityUtils.getHeader;

@Component
@Slf4j
public class SoliqClient extends BaseClient {

    public SoliqCompanyDTO getCustomerByTin(String tin) {
        try {
            ResponseEntity<SoliqCompanyDTO> response = restTemplate.exchange(applicationProperties.getSoliqProperty().getHost() + "np1/bytin/factura-all?" + (tin.length() > 9 ? "pinfl=" : "tin=") + tin, HttpMethod.GET, new HttpEntity<>(getHeaders()), SoliqCompanyDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error occurred while checking customer by tin " + tin + " from Soliq service. Error:" + e.getMessage(),null, HttpStatus.BAD_REQUEST);
        }
    }

    public String getCustomerByTinString(String tin) {
        try {
            ResponseEntity<String> response = restTemplate.exchange(applicationProperties.getSoliqProperty().getHost() + "np1/bytin/factura-all?" + (tin.length() > 9 ? "pinfl=" : "tin=") + tin, HttpMethod.GET, new HttpEntity<>(getHeaders()), String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error occurred while checking customer by tin " + tin + " from Soliq service. Error:" + e.getMessage(),null, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public String authenticate() {
        return null;
    }

    @Override
    public String getToken() {
        return null;
    }

    public HttpHeaders getHeaders() {
//        Integration integration = integrationRepository.findFirstByTypeAndDeletedIsFalse(getIntegrationType()).orElseThrow(validationService.entityNotFoundExceptionThrow(Integration.class.getSimpleName()));
        HttpHeaders headers = getHeader();
        headers.setBasicAuth(applicationProperties.getSoliqProperty().getUsername(), applicationProperties.getSoliqProperty().getPassword());
        return headers;
    }

    @Override
    public IntegrationType getIntegrationType() {
        return IntegrationType.SOLIQ;
    }
}
