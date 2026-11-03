package uz.uzkassa.cts.integrations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.cts.dto.auth.AuthenticateDTO;
import uz.uzkassa.cts.dto.auth.IdTokenDTO;
import uz.uzkassa.cts.dto.factura.*;
import uz.uzkassa.cts.enums.IntegrationType;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.time.LocalTime;

import static uz.uzkassa.cts.security.SecurityUtils.getHeader;

@Component
@Slf4j
public class XfileClient extends BaseClient {

    @Override
    public String authenticate() {
        AuthenticateDTO loginDTO = new AuthenticateDTO();
        loginDTO.setUsername(applicationProperties.getXfileProperty().getUsername());
        loginDTO.setPassword(applicationProperties.getXfileProperty().getPassword());
        HttpEntity<AuthenticateDTO> entity = new HttpEntity<>(loginDTO, SecurityUtils.getHeader());
        ResponseEntity<IdTokenDTO> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getXfileProperty().getAuthenticateUrl(),
                    HttpMethod.POST,
                    entity,
                    IdTokenDTO.class
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while authenticating to X-File service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (response.getBody() != null && response.getBody().getIdToken() != null) {
            String token = response.getBody().getIdToken();
            tokenStoreService.create(getIntegrationType(), token, applicationProperties.getXfileProperty().getTokenExpiration());
            return token;
        }
        throw new IntegrationException("An unexpected error has occurred while authenticating to X-File service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
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
    public IntegrationType getIntegrationType() {
        return IntegrationType.X_FILE;
    }

    @Override
    public HttpHeaders getHeaders() {
        HttpHeaders headers = getHeader();
        headers.setBearerAuth(getToken());
        return headers;
    }

    public String sendInvoice(FacturaDTO facturaDTO) {
        log.info("Sending to xfile: {}", facturaDTO.toString());
        ResponseEntity<FacturaResponseDto> response;
        try {
            response = restTemplate.exchange(
                    applicationProperties.getXfileProperty().getInvoiceUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(facturaDTO, getHeaders()),
                    FacturaResponseDto.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while sending invoice to X-File service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (response.getBody() != null && response.getBody().getData() != null) {
            return response.getBody().getData();
        }
        throw new IntegrationException("An unexpected error has occurred while sending invoice to X-File service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
    }

    public Long getTotalDataByTin(GetIncomingInvoiceRequest request) {
        HttpEntity<?> entity = new HttpEntity<>(getHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getXfileProperty().getIncomingInvoiceUrl())
                .queryParam("customerTin", request.getSupplierTin())
                .queryParam("status", request.getStatus())
                .queryParam("from", request.getFrom().with(LocalTime.MIN))
                .queryParam("to", request.getTo().with(LocalTime.MAX))
                .queryParam("page", request.getPage())
                .queryParam("size", request.getSize());
        try {
            ResponseEntity<FacturaIncomingDto> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, FacturaIncomingDto.class);
            return response.getBody() != null ? response.getBody().getData().getTotal() : 0L;
        } catch (Exception e) {
            log.error("-------------xfile get incoming exception------------------");
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    public FacturaCheckStatusDataDto checkFacturaStatus(String facturaId) {
        log.info("Check factura status facturaId: {}", facturaId);
        String url = applicationProperties.getXfileProperty().getInvoiceByUuid() + "/" + facturaId;
        ResponseEntity<FacturaCheckStatusDto> response;
        try {
            response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders()),
                    FacturaCheckStatusDto.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while checking invoice status from X-File service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (response.getBody() != null) {
            return response.getBody().getData();
        }
        throw new IntegrationException("An unexpected error has occurred while checking invoice status from X-File service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
    }

    public FacturaIncomingDto getDataByTin(GetIncomingInvoiceRequest request) {
        HttpEntity<?> entity = new HttpEntity<>(getHeaders());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getXfileProperty().getIncomingInvoiceUrl())
                .queryParam("customerTin", request.getSupplierTin())
                .queryParam("status", request.getStatus())
                .queryParam("dateSortType", "APPROVED_DATE")
                .queryParam("from", request.getFrom().toLocalDate().atStartOfDay())
                .queryParam("to", request.getTo().toLocalDate().atTime(LocalTime.MAX))
                .queryParam("page", request.getPage() == 0 ? 1 : request.getPage())
                .queryParam("size", request.getSize());
        ResponseEntity<FacturaIncomingDto> response;
        try {
            response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, FacturaIncomingDto.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException("An unexpected error has occurred while getting incoming invoice from X-File service. Error:" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IntegrationException("An unexpected error has occurred while getting incoming invoice from X-File service. Error:" + response, null, HttpStatus.valueOf(response.getStatusCode().value()));
        }
        return response.getBody();
    }
}
