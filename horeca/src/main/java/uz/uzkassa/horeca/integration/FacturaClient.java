package uz.uzkassa.horeca.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.factura.FacturaSendDTO;
import uz.uzkassa.horeca.dto.factura.FacturaShortDTO;
import uz.uzkassa.horeca.dto.factura.Status;
import uz.uzkassa.horeca.repository.FacturaHostRepository;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.web.rest.errors.FacturaException;

@Component
@Slf4j
public class FacturaClient extends BaseClient {

    private final FacturaHostRepository facturaHostRepository;

    public FacturaClient(FacturaHostRepository facturaHostRepository) {
        this.facturaHostRepository = facturaHostRepository;
    }

    private String getFacturaHost() {
        if (facturaHostRepository.findFirstByActiveIsTrue().isPresent()) {
            return facturaHostRepository.findFirstByActiveIsTrue().get().getHost();
        }
        return applicationProperties.getFacturaConfig().getHost();
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = HeaderUtils.getHeader();
        headers.setBasicAuth(
                applicationProperties.getFacturaConfig().getUsername(),
                applicationProperties.getFacturaConfig().getPassword()
        );
        return headers;
    }

    public void sendFactura(String tin, FacturaSendDTO facturaSendDTO) {
        long start = System.currentTimeMillis();
        facturaSendDTO.setNotes(null);
        facturaSendDTO.setClientIp(HeaderUtils.getCurrentUserIP());
        StringBuilder url = new StringBuilder();
        url.append(this.getFacturaHost());
        url.append("ru/").append(tin).append("/facturas/seller");
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url.toString(),
                    HttpMethod.POST,
                    new HttpEntity<>(facturaSendDTO, getHeaders()),
                    String.class
            );

            if (HttpStatus.OK.equals(response.getStatusCode())) {
                log.info("SEND FACTURA :{} ============== TIME: {}ms", facturaSendDTO.getFacturaId(), (System.currentTimeMillis() - start));
            } else {
                log.error("POST Send factura response: {}", response);
                throw new FacturaException(
                        localizationService.localize("factura.send.error"),
                        response.getBody(),
                        response.getStatusCode()
                );
            }
        } catch (Exception e) {
            log.error("Factura send error:" + e.getMessage());
            throw new FacturaException(localizationService.localize("factura.send.error"), e.getMessage());
        }
    }

    public FacturaShortDTO getFactura(String facturaId, String tin, boolean isBuyer) {
        log.info("Get FacturaDTO by Id: {}, TIN: {}, Is Buyer: {}", facturaId, tin, isBuyer);
        StringBuilder url = new StringBuilder();
        url.append(this.getFacturaHost()).append("ru/").append(tin).append("/facturas");
        if (isBuyer) {
            url.append("/buyer");
        } else {
            url.append("/seller");
        }
        url.append("/").append(facturaId.trim());
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url.toString(),
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders()),
                    String.class
            );

            if (HttpStatus.OK.equals(response.getStatusCode())) {
                return objectMapper.readValue(response.getBody(), FacturaShortDTO.class);
            } else {
                log.error("Get Factura error response: {}", response);
                return null;
            }
        } catch (Exception e) {
            log.error("Json parse error: {}", e.getMessage());
            return null;
        }
    }

    public void cancelFactura(String tin, FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setClientIp(HeaderUtils.getCurrentUserIP());
        StringBuilder url = new StringBuilder();
        url.append(this.getFacturaHost()).append("ru/").append(tin).append("/facturas/seller/cancel");

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url.toString(),
                    HttpMethod.POST,
                    new HttpEntity<>(facturaSendDTO, getHeaders()),
                    String.class
            );

            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                log.error("Factura cancel response error: {}", response);
                throw new FacturaException(
                        localizationService.localize("factura.cancel.error"),
                        response.getBody(),
                        response.getStatusCode()
                );
            }
        } catch (Exception e) {
            log.error("Factura cancel error:" + e.getMessage());
            throw new FacturaException(localizationService.localize("factura.cancel.error"), e.getMessage());
        }
    }

    public void acceptOrReject(String tin, FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setClientIp(HeaderUtils.getCurrentUserIP());
        StringBuilder url = new StringBuilder();
        url.append(this.getFacturaHost()).append("ru/").append(tin).append("/facturas/buyer");
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url.toString(),
                    HttpMethod.POST,
                    new HttpEntity<>(facturaSendDTO, getHeaders()),
                    String.class
            );

            if (!HttpStatus.OK.equals(response.getStatusCode())) {
                log.error("Factura accept/reject response error: {}", response);
                throw new FacturaException(localizationService.localize("factura.error"), response.getBody(), response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Factura accept/reject error:" + e.getMessage());
            throw new FacturaException(localizationService.localize("factura.error"), e.getMessage());
        }
    }

    public String getSign(String facturaId, String tin, Status status, boolean isBuyer) {
        StringBuilder url = new StringBuilder();
        url.append(this.getFacturaHost());
        url.append("ru/").append(tin).append("/facturas/");
        if (isBuyer) {
            url.append("buyer/");
        } else {
            url.append("seller/");
        }
        url.append("signedfile/").append(facturaId.trim()).append("?status=").append(status.getFacturaStatus());
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url.toString(),
                    HttpMethod.GET,
                    new HttpEntity<>(getHeaders()),
                    String.class
            );
            if (HttpStatus.OK.equals(response.getStatusCode())) {
                return objectMapper.readValue(response.getBody(), String.class);
            } else {
                log.error("Get seller/buyer factura sign error response: {}", response);
                throw new FacturaException(
                        localizationService.localize("factura.sign.error"),
                        response.getBody(),
                        response.getStatusCode()
                );
            }
        } catch (Exception e) {
            log.error("Json parse error: {}", e.getMessage());
            throw new FacturaException(localizationService.localize("factura.sign.error"), e.getMessage());
        }
    }
}
