package uz.uzkassa.smartpossupply.integration;

import org.springframework.web.client.RestClientResponseException;
import uz.uzkassa.smartpossupply.constants.MessageConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.smartpossupply.config.httpclient.CustomResponse;
import uz.uzkassa.smartpossupply.dto.auth.TimestampSignatureDTO;
import uz.uzkassa.smartpossupply.dto.factura.*;
import uz.uzkassa.smartpossupply.dto.lgota.LgotaPrefixDTO;
import uz.uzkassa.smartpossupply.dto.lot.LotDTO;
import uz.uzkassa.smartpossupply.dto.lot.LotPrefixDTO;
import uz.uzkassa.smartpossupply.dto.soliq.*;
import uz.uzkassa.smartpossupply.dto.waybill.*;
import uz.uzkassa.smartpossupply.enums.WaybillStatus;
import uz.uzkassa.smartpossupply.repository.FacturaHostRepository;
import uz.uzkassa.smartpossupply.service.TelegramBotApplicationLogService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.FacturaErrorDTO;
import uz.uzkassa.smartpossupply.web.rest.errors.FacturaException;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Slf4j
public class FacturaRestClient extends BaseClient {

    @Qualifier("facturaRestTemplate")
    private final RestTemplate facturaRestTemplate;

    private final FacturaHostRepository facturaHostRepository;
    private final TelegramBotApplicationLogService telegramApplicationLogService;

    public FacturaRestClient(RestTemplate facturaRestTemplate, FacturaHostRepository facturaHostRepository, TelegramBotApplicationLogService telegramApplicationLogService) {
        this.facturaRestTemplate = facturaRestTemplate;
        this.facturaHostRepository = facturaHostRepository;
        this.telegramApplicationLogService = telegramApplicationLogService;
    }

    public IntegrationType getIntegrationType() {
        return IntegrationType.FACTURA;
    }

    private String getFacturaHost() {
        if (facturaHostRepository.findFirstByActiveIsTrue().isPresent()) {
            return facturaHostRepository.findFirstByActiveIsTrue().get().getHost();
        }
        return applicationProperties.getFacturaConfig().getUrl();
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = SecurityUtils.getHeader();
        headers.setBasicAuth(applicationProperties.getFacturaConfig().getUsername(), applicationProperties.getFacturaConfig().getPassword());
        return headers;
    }

    private Map<String, String> getHeadersMap() {
        Map<String, String> headers = new HashMap<>();
        String credentials = Base64.getEncoder().encodeToString((applicationProperties.getFacturaConfig().getUsername() + ":" + applicationProperties.getFacturaConfig().getPassword()).getBytes());
        headers.put(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
        return headers;
    }

    private static String toCurl(Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        headers.forEach((name, value) ->
                sb.append("-H '").append(name).append(": ").append(value).append("' "));
        return sb.toString().trim();
    }

    public String getSellerFactura(String tin, String facturaId) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            final String url = StringUtils.join(getFacturaHost(), "/provider/api", "/ru/", tin, "/facturas/seller/", facturaId);
            final CustomResponse response = customRequest.sendGetRequest(url, getHeadersMap(), null);
            return response.getContent();
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public String getSellerFacturaSign(final String tin, final String facturaId, final String status) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            final String url = StringUtils.join(getFacturaHost(), "/ru/", tin, "/facturas/seller/signedfile/", facturaId, "?status=", status);
            final CustomResponse response = customRequest.sendGetRequest(url, getHeadersMap(), null);
            return response.getContent();
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public String getBuyerFactura(String tin, String facturaId) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            final String url = StringUtils.join(getFacturaHost(), "/provider/api", "/ru/", tin, "/facturas/buyer/", facturaId);
            final CustomResponse response = customRequest.sendGetRequest(url, getHeadersMap(), null);
            return response.getContent();
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public String getBuyerFacturaSign(final String tin, final String facturaId, final String status) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            final String url = StringUtils.join(getFacturaHost(), "/ru/", tin, "/facturas/buyer/signedfile/", facturaId, "?status=", status);
            final CustomResponse response = customRequest.sendGetRequest(url, getHeadersMap(), null);
            return response.getContent();
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public String getGuid() {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            url.append("/ru/utils/guid");
            try {
                ResponseEntity<String> response = facturaRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaders()),
                        String.class
                );
                if (HttpStatus.OK == response.getStatusCode()) {
                    TimestampSignatureDTO timestampSignatureDTO = objectMapper.readValue(response.getBody(), TimestampSignatureDTO.class);
                    if (timestampSignatureDTO.isSuccess()) {
                        return timestampSignatureDTO.getData();
                    } else {
                        log.error("Get GUID error response: {}", response);
                        throw new FacturaException(localizationService.localize(MessageConstants.NIC_GENERATE_FACTURA_GUID_ERROR), response.getBody(), response.getStatusCode());
                    }
                } else {
                    log.error("Get GUID error response: {}", response);
                    throw new FacturaException(localizationService.localize(MessageConstants.NIC_GENERATE_FACTURA_GUID_ERROR), response.getBody(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new FacturaException(localizationService.localize(MessageConstants.NIC_GENERATE_FACTURA_GUID_ERROR), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    //@Transactional(readOnly = true)
    public List<ProviderBindingDTO> getProviderBinding(String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            url.append("/ru/register/providerbinding/").append(tin);
            try {
                ResponseEntity<String> response = facturaRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaders()),
                        String.class
                );
                if (HttpStatus.OK == response.getStatusCode()) {
                    return objectMapper.readValue(response.getBody(), ProviderBindingResultDTO.class).getProviders();
                } else {
                    log.error("Provider binding error response: {}", response);
                    throw new FacturaException(localizationService.localize(MessageConstants.PROVIDER_BIND_ERROR_FROM_NIC), response.getBody(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new FacturaException(localizationService.localize(MessageConstants.PROVIDER_BIND_ERROR_FROM_NIC), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    //@Transactional(readOnly = true)
    public void bindingProvider(String sign) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            url.append("/ru/register/providerbinding");
            try {
                ResponseEntity<String> response = facturaRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.POST,
                        new HttpEntity<>(new SignVM(sign), getHeaders()), String.class);
                if (HttpStatus.OK != response.getStatusCode()) {
                    log.error("POST bind provider response: {}", response);
                    throw new FacturaException(localizationService.localize(MessageConstants.BINDING_PROVIDER_ERROR_FROM_NIC), response.getBody(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new FacturaException(localizationService.localize(MessageConstants.BINDING_PROVIDER_ERROR_FROM_NIC), e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    //@Transactional(readOnly = true)
    public List<LgotaProductDTO> getLgotaByWithoutVatProducts(String catalogCode, boolean throwException) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            url.append("/ru/utils/Lgota//without-vat-products?PageNumber=0&PageLimit=1000&TasnifCode=").append(catalogCode);
            try {
                ResponseEntity<String> response = facturaRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaders()),
                        String.class
                );
                if (HttpStatus.OK == response.getStatusCode()) {
                    return objectMapper.readValue(response.getBody(), FacturaLgotaProductResponseDTO.class).getRows();
                } else {
                    log.error("Lgota By Without Vat Products error response: {}", response);
                    if (throwException) {
                        throw new FacturaException(localizationService.localize(MessageConstants.LGOTA_ERROR_FROM_NIC), response.getBody(), response.getStatusCode());
                    }
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                if (throwException) {
                    throw new FacturaException(localizationService.localize(MessageConstants.LGOTA_ERROR_FROM_NIC), e.getMessage());
                }
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            if (throwException) {
                throw new IntegrationException(throwable.getMessage());
            }
            return null;
        });
    }

    //@Transactional(readOnly = true)
    public List<LgotaProductDTO> getLgotaByZeroVatProducts(String mxikCode) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            url.append("/ru/utils/Lgota/zero-vat-products?PageNumber=0&PageLimit=1000&TasnifCode=").append(mxikCode);
            try {
                ResponseEntity<String> response = facturaRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaders()),
                        String.class
                );
                if (HttpStatus.OK == response.getStatusCode()) {
                    return objectMapper.readValue(response.getBody(), FacturaLgotaProductResponseDTO.class).getRows();
                } else {
                    log.error("Lgota By Zero Vat Products error response: {}", response);
                    throw new FacturaException(localizationService.localize(MessageConstants.LGOTA_ERROR_FROM_NIC), response.getBody(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new FacturaException(localizationService.localize(MessageConstants.LGOTA_ERROR_FROM_NIC), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    //@Transactional(readOnly = true)
    public List<LgotaCompanyDTO> getLgotaByCompany(String tin, boolean isBuyer) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            if (isBuyer) {
                url.append("/ru/utils/Lgota/privileged-buyer-companies?PageNumber=0&PageLimit=1000&Tin=").append(tin);
            } else {
                url.append("/ru/utils/Lgota/privileged-seller-companies?PageNumber=0&PageLimit=1000&Tin=").append(tin);
            }
            try {
                ResponseEntity<String> response = facturaRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaders()),
                        String.class
                );
                if (HttpStatus.OK == response.getStatusCode()) {
                    return objectMapper.readValue(response.getBody(), FacturaLgotaCompanyResponseDTO.class).getRows();
                } else {
                    log.error("Lgota By Company error response: {}", response);
                    throw new FacturaException(localizationService.localize(MessageConstants.LGOTA_ERROR_FROM_NIC), response.getBody(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new FacturaException(localizationService.localize(MessageConstants.LGOTA_ERROR_FROM_NIC), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public void sendFactura(String tin, FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setNotes(null);
        facturaSendDTO.setClientIp(SecurityUtils.getCurrentUserIP());
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            long start = System.currentTimeMillis();
            StringBuilder url = new StringBuilder().append(this.getFacturaHost()).append("/provider/api").append("/ru/").append(tin).append("/facturas/seller");

            Map<String, String> headers = getHeadersMap();
            String json;
            try {
                json = objectMapper.writeValueAsString(facturaSendDTO);
                log.info(" FACTURA ID: {}, FACTURA SEND BODY: {}", facturaSendDTO.getFacturaId(), json);
            } catch (JsonProcessingException e) {
                log.error("Error convert body to JSON : {}", e.getMessage(), e);
                log.error(" FACTURA ID: {}, FACTURA SEND BODY: {}", facturaSendDTO.getFacturaId(), facturaSendDTO);
                throw new BadRequestException(e.getMessage());
            }

            log.info("FACTURA sendFactura REQUEST | facturaId={} | curl -X POST '{}' {} -d '{}'",
                    facturaSendDTO.getFacturaId(), url, toCurl(headers), json);

            CustomResponse response = null;
            try {
                response = customRequest.sendPostRequest(url.toString(), headers, json);
                log.info("FACTURA sendFactura RESPONSE | facturaId={} | TIME: {}ms | {}",
                        facturaSendDTO.getFacturaId(), (System.currentTimeMillis() - start), response);

                if (HttpStatus.OK.value() != response.getCode()) {
                    StringBuilder message = new StringBuilder("Type: ")
                            .append("FACTURA ID:").append(facturaSendDTO.getFacturaId()).append(" \n")
                            .append("SELLER TIN: ").append(tin).append(" \n")
                            .append("CLIENT IP: ").append(facturaSendDTO.getClientIp()).append(" \n")
                            .append("URL: ").append(url).append(" \n")
                            .append("HTTP CODE: ").append(response.getCode()).append(" \n")
                            .append("RESPONSE: ").append(" \n")
                            .append(response);
                    telegramApplicationLogService.send(message.toString());
                    throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), parseNicError(response));
                }
            } catch (Exception e) {
                log.error("FACTURA sendFactura FAILED | facturaId={} | TIME: {}ms | RESPONSE: {}",
                        facturaSendDTO.getFacturaId(), (System.currentTimeMillis() - start), response, e);
                throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public FacturaValidateResponseDTO validateFactura(String tin, FacturaSendDTO facturaSendDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            String url = new StringBuilder()
                    .append(getFacturaHost())
                    .append("/provider/api")
                    .append("/ru/")
                    .append(tin)
                    .append("/facturas/seller/validate")
                    .toString();

            Map<String, String> headers = getHeadersMap();

            try {
                String json = objectMapper.writeValueAsString(facturaSendDTO);
                CustomResponse response = customRequest.sendPostRequest(url, headers, json);
                if (response.getCode() == HttpStatus.OK.value()) {
                    return objectMapper.readValue(response.getContent(), FacturaValidateResponseDTO.class);
                }

                throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), parseNicError(response));
            } catch (JsonProcessingException e) {
                throw new BadRequestException(e.getMessage());
            } catch (Exception e) {
                throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), e.getMessage());
            }
        }, throwable -> {
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public FacturaShortDTO getFactura(String facturaId, String tin, boolean isBuyer) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            log.info("Get FacturaDTO by Id: {}, TIN: {}, Is Buyer: {}", facturaId, tin, isBuyer);
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            url.append("/ru/").append(tin).append("/facturas");
            if (isBuyer) {
                url.append("/buyer");
            } else {
                url.append("/seller");
            }
            url.append("/").append(facturaId.trim());

            CustomResponse response = customRequest.sendGetRequest(url.toString(), getHeadersMap());
            if (HttpStatus.OK.value() == response.getCode()) {
                try {
                    log.info("Factura content: {}", response.getContent());
                    return objectMapper.readValue(response.getContent(), FacturaShortDTO.class);
                } catch (IOException e) {
                    log.error(" Factura response convert to JSON error: {}", e.getMessage());
                }
            } else {
                log.error(" Factura response convert to JSON error: {}", parseNicError(response));
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            return null;
        });
    }

    public void cancelFactura(String tin, FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setClientIp(SecurityUtils.getCurrentUserIP());
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            url.append("/ru/").append(tin).append("/facturas/seller/cancel");
            try {
                ResponseEntity<String> response = facturaRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.POST,
                        new HttpEntity<>(facturaSendDTO, getHeaders()),
                        String.class);
                if (HttpStatus.OK != response.getStatusCode()) {
                    log.error("Factura cancel response error: {}", response);
                    throw new FacturaException(localizationService.localize(MessageConstants.FACTURA_CANCEL_ERROR), response.getBody(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Factura cancel error:" + e.getMessage());
                throw new FacturaException(localizationService.localize(MessageConstants.FACTURA_CANCEL_ERROR), e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public void acceptOrReject(String tin, FacturaSendDTO facturaSendDTO) {
        facturaSendDTO.setClientIp(SecurityUtils.getCurrentUserIP());
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            url.append("/ru/").append(tin).append("/facturas/buyer");

            StringBuilder message = new StringBuilder();
            message.append("\nURL: ").append(url);
            try {
                ResponseEntity<String> response = facturaRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.POST,
                        new HttpEntity<>(facturaSendDTO, getHeaders()),
                        String.class);
                if (HttpStatus.OK != response.getStatusCode()) {
                    log.error("Factura accept/reject response error: {}", response);
                    message.append("\nFactura accept/reject response error: ").append(response);
                    throw new FacturaException(localizationService.localize(MessageConstants.FACTURA_ERROR), response.getBody(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Factura accept/reject error:" + e.getMessage());
                message.append("\nFactura accept/reject response error: ").append(e.getMessage());
                telegramApplicationLogService.send(message.toString());
                throw new FacturaException(localizationService.localize(MessageConstants.FACTURA_ERROR), e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public String getSign(String facturaId, String tin, Status status, boolean isBuyer) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            url.append("/ru/").append(tin).append("/facturas/");
            if (isBuyer) {
                url.append("buyer/");
            } else {
                url.append("seller/");
            }
            url.append("signedfile/").append(facturaId.trim()).append("?status=").append(status.getFacturaStatus());
            CustomResponse response = customRequest.sendGetRequest(url.toString(), getHeadersMap());
            if (HttpStatus.OK.value() == response.getCode()) {
                return response.getContent();
            } else {
                log.warn("Get seller/buyer factura sign error response: {}", parseNicError(response));
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            return null;
        });
    }

    private String parseNicError(CustomResponse response) {
        if (StringUtils.isNotBlank(response.getErrorMessage())) {
            return response.getErrorMessage();
        }
        try {
            final FacturaErrorDTO responseDTO = objectMapper.readValue(response.getContent(), FacturaErrorDTO.class);
            return Optional.ofNullable(responseDTO)
                    .map(FacturaErrorDTO::getErrorMessage)
                    .orElse("Непредвиденная ошибка");
        } catch (IOException e) {
            return response.getContent();
        }
    }

    //@Transactional(readOnly = true)
    public FacturaProductListDTO getFacturaProductList(String tin, String facturaProductId) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            url.append("/ru/").append(tin).append("/facturas/productlist/").append(facturaProductId);
            try {
                ResponseEntity<String> response = facturaRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(getHeaders()),
                        String.class
                );
                if (HttpStatus.OK == response.getStatusCode()) {
                    return objectMapper.readValue(response.getBody(), FacturaProductListDTO.class);
                } else {
                    log.error("Get Factura product list error response: {}", response);
                    throw new FacturaException(localizationService.localize(MessageConstants.FACTURA_PRODUCT_ERROR), response.getBody(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new FacturaException(localizationService.localize(MessageConstants.FACTURA_PRODUCT_ERROR), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public void sendWaybill(final String lang, final String tin, final WaybillSendDTO dto, Long id) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            long start = System.currentTimeMillis();
            StringBuilder url = new StringBuilder(getFacturaHost());
            url.append("/provider/api");
            url.append("/").append(lang);
            url.append("/").append(tin);
            url.append("/waybills-v2/local/consignor");
            try {
                final String json = objectMapper.writeValueAsString(dto);
                final CustomResponse response = customRequest.sendPostRequest(url.toString(), getHeadersMap(), json);
                if (HttpStatus.OK.value() == response.getCode()) {
                    log.info("SEND WAYBILL :{} ============== TIME: {}ms", id, (System.currentTimeMillis() - start));
                } else {
                    StringBuilder message = new StringBuilder("Type: ")
                            .append("WAYBILL ID:").append(id).append(" \n")
                            .append("SELLER TIN: ").append(tin).append(" \n")
                            .append("CLIENT IP: ").append(dto.getClientIp()).append(" \n")
                            .append("URL: ").append(url).append(" \n")
                            .append("RESPONSE: ").append(" \n")
                            .append(response);
                    log.error("POST Send waybill response: {}", response);
                    log.error("Message: {}", message);
                    throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), parseNicError(response));
                }
            } catch (JsonProcessingException e) {
                log.error("Send waybill convert to JSON error: {}", e.getMessage());
                throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), e.getMessage());
            } catch (BadRequestException e) {
                throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public void cancelWaybill(final String lang, final String tin, final WaybillSendDTO waybillSendDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            StringBuilder url = new StringBuilder(getFacturaHost());
            url.append("/provider/api");
            url.append("/").append(lang);
            url.append("/").append(tin);
            url.append("/waybills-v2/local/consignor/cancel");
            try {
                final String json = objectMapper.writeValueAsString(waybillSendDTO);
                final CustomResponse response = customRequest.sendPostRequest(url.toString(), getHeadersMap(), json);
                if (HttpStatus.OK.value() != response.getCode()) {
                    log.error("POST Cancel waybill response: {}", response);
                    throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), parseNicError(response));
                }
            } catch (JsonProcessingException e) {
                log.error("Cancel waybill convert to JSON error: {}", e.getMessage());
                throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public void acceptWaybill(final String lang, final String tin, final WaybillSendDTO waybillSendDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            StringBuilder url = new StringBuilder(getFacturaHost());
            url.append("/provider/api");
            url.append("/").append(lang);
            url.append("/").append(tin);
            url.append("/waybills-v2/local/consignee");
            try {
                final String json = objectMapper.writeValueAsString(waybillSendDTO);
                final CustomResponse response = customRequest.sendPostRequest(url.toString(), getHeadersMap(), json);
                if (HttpStatus.OK.value() != response.getCode()) {
                    log.error("POST {} waybill response: {} ", waybillSendDTO.getWaybillLocalSignType(), response);
                    throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), parseNicError(response));
                }
            } catch (JsonProcessingException e) {
                log.error("{} waybill convert to JSON error: {}", waybillSendDTO.getWaybillLocalSignType(), e.getMessage());
                throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public void rejectWaybill(final String lang, final String tin, final WaybillSendDTO waybillSendDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            StringBuilder url = new StringBuilder(getFacturaHost());
            url.append("/provider/api");
            url.append("/").append(lang);
            url.append("/").append(tin);
            url.append("/waybills-v2/local/consignee/reject");
            try {
                final String json = objectMapper.writeValueAsString(waybillSendDTO);
                final CustomResponse response = customRequest.sendPostRequest(url.toString(), getHeadersMap(), json);
                if (HttpStatus.OK.value() != response.getCode()) {
                    log.error("POST {} waybill response: {} ", waybillSendDTO.getWaybillLocalSignType(), response);
                    throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), parseNicError(response));
                }
            } catch (JsonProcessingException e) {
                log.error("{} waybill convert to JSON error: {}", waybillSendDTO.getWaybillLocalSignType(), e.getMessage());
                throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public WaybillDriverTransportDTO[] getDriverTransports(String tinOrPinfl) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api/ru");
            url.append("/waybills-v2/utils/transport?");
            url.append("tinOrPinfl=").append(tinOrPinfl);
            HttpHeaders headers = getHeaders();

            log.error("NIC REQUEST >>> time={} | method=GET | url={} | headers={}", LocalDateTime.now(), url, headers);

            try {
                ResponseEntity<String> response = facturaRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class
                );
                if (HttpStatus.OK == response.getStatusCode()) {
                    return objectMapper.readValue(response.getBody(), WaybillDriverTransportDTO[].class);
                } else {
                    log.error("Get Driver transport error response: {}", response);
                    throw new FacturaException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), response.getBody(), response.getStatusCode());
                }
            } catch (RestClientResponseException e) {
                log.error("NIC RESPONSE <<< time={} | status={} | headers={} | body={}", LocalDateTime.now(), e.getStatusCode(), e.getResponseHeaders(), e.getResponseBodyAsString());
                throw new FacturaException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), e.getResponseBodyAsString());

            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new FacturaException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public TransportCheckResponseDTO[] getTransports(TransportCheckRequestDTO transportCheckRequestDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api/");
            url.append("ru");
            url.append("/utils/Transport");
            HttpHeaders headers = getHeaders();

            try {
                String json = objectMapper.writeValueAsString(transportCheckRequestDTO);
                log.error("NIC GET TRANSPORT REQUEST >>> time={} | method=POST | url={} | headers={} | body={}", LocalDateTime.now(), url, headers, json);

                ResponseEntity<String> response = facturaRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.POST,
                        new HttpEntity<>(json, headers),
                        String.class
                );
                if (HttpStatus.OK == response.getStatusCode()) {
                    return objectMapper.readValue(response.getBody(), TransportCheckResponseDTO[].class);
                } else {
                    log.error("Check transports error response: {}", response);
                    throw new FacturaException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), response.getBody(), response.getStatusCode());
                }
            } catch (RestClientResponseException e) {
                log.error("NIC GET TRANSPORT RESPONSE <<< time={} | status={} | headers={} | body={}", LocalDateTime.now(), e.getStatusCode(), e.getResponseHeaders(), e.getResponseBodyAsString());
                throw new FacturaException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), e.getResponseBodyAsString());

            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new FacturaException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public WaybillEmpowermentDTO[] getCompanyEmpowerments(String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api/ru/");
            url.append(tin);
            url.append("/empowerments/seller?stateId=15");

            CustomResponse response = customRequest.sendGetRequest(url.toString(), getHeadersMap());
            if (HttpStatus.OK.value() == response.getCode()) {
                try {
                    WaybillCompanyEmpowermentsDTO result = objectMapper.readValue(response.getContent(), WaybillCompanyEmpowermentsDTO.class);
                    if (result != null) {
                        return result.getRows();
                    } else {
                        throw new BadRequestException("Нет данных по данному ИНН");
                    }
                } catch (IOException e) {
                    log.debug("Soliq response convert to JSON error: {}", e.getMessage());
                }
            } else {
                log.debug("GET Company by TIN response: {}", response);
                throw new BadRequestException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), StringUtils.isNotEmpty(response.getErrorMessage()) ? response.getErrorMessage() : response.getContent());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            return null;
        });
    }

    public WaybillRegionDTO[] getRegions() {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            String url = StringUtils.join(getFacturaHost(), "/provider/api/ru", "/waybills-v2/utils/catalogs/regions");
            CustomResponse response = customRequest.sendGetRequest(url, getHeadersMap());
            if (HttpStatus.OK.value() == response.getCode()) {
                try {
                    return objectMapper.readValue(response.getContent(), WaybillRegionDTO[].class);
                } catch (IOException e) {
                    log.error(" Factura response convert to JSON error: {}", e.getMessage());
                }
            } else {
                log.error("GET Regions response: {}", response);
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            return null;
        });
    }

    public WaybillDistrictDTO[] getDistricts(Long regionId) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            String url = StringUtils.join(getFacturaHost(), "/provider/api/ru", "/waybills-v2/utils/catalogs/districts?regionId=", regionId);
            CustomResponse response = customRequest.sendGetRequest(url, getHeadersMap());
            if (HttpStatus.OK.value() == response.getCode()) {
                try {
                    return objectMapper.readValue(response.getContent(), WaybillDistrictDTO[].class);
                } catch (IOException e) {
                    log.error(" Factura response convert to JSON error: {}", e.getMessage());
                }
            } else {
                log.error("GET Districts response: {}", response);
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            return null;
        });
    }

    public String getWaybillSign(String waybillLocalId, String tinOrPinfl, WaybillStatus status, boolean isConsignor) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(this.getFacturaHost());
            url.append("/provider/api");
            url.append("/ru/").append(tinOrPinfl).append("/waybills-v2/local/");
            if (isConsignor) {
                url.append("consignor/");
            } else {
                url.append("consignee/");
            }
            url.append("signed-file?waybillLocalId=").append(waybillLocalId.trim()).append("&signType=").append(WaybillStatus.getSignType(status));
            CustomResponse response = customRequest.sendGetRequest(url.toString(), getHeadersMap());
            if (HttpStatus.OK.value() == response.getCode()) {
                return response.getContent();
            } else {
                log.error("Get consignor/consignee Waybill sign error response: {}", parseNicError(response));
                throw new IntegrationException(Optional.ofNullable(response.getErrorMessage()).orElse("Get consignor/consignee Waybill sign error"));
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public InvestmentObjectBuyerDTO checkIsInvestmentObjectBuyer(String tinOrPinfl) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            String url = this.getFacturaHost() + "/provider/api/ru/utils/investment-object/" + tinOrPinfl;
            CustomResponse response = customRequest.sendGetRequest(url, getHeadersMap());
            if (HttpStatus.OK.value() == response.getCode()) {
                try {
                    return objectMapper.readValue(response.getContent(), InvestmentObjectBuyerDTO.class);
                } catch (IOException e) {
                    log.error("While checking isInvestmentObjectBuyer json parse error: {}, url = {}", e.getMessage(), url);
                    throw new FacturaException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), StringUtils.isNotEmpty(response.getErrorMessage()) ? response.getErrorMessage() : response.getContent());
                }
            } else {
                log.error("Factura provider response error while getting isInvestmentObjectBuyer : {}", response);
                return new InvestmentObjectBuyerDTO(Boolean.FALSE); //In agreement with the PM, we decided to return FALSE instead of throwing error
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            return new InvestmentObjectBuyerDTO(Boolean.FALSE);
        });
    }

    public InvestmentObjectDTO getInvestmentObject(String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            String url = this.getFacturaHost() + "/provider/api/ru/utils/investment-object";
            NameValuePair[] params = new NameValuePair[1];
            params[0] = new BasicNameValuePair("buyerId", tin);
            CustomResponse response = customRequest.sendGetRequest(url, getHeadersMap(), Arrays.stream(params).toList());
            if (HttpStatus.OK.value() == response.getCode()) {
                try {
                    return objectMapper.readValue(response.getContent(), InvestmentObjectDTO.class);
                } catch (IOException e) {
                    log.error("While getting investmentObject json parse error: {}, url = {}", e.getMessage(), url);
                    throw new FacturaException(localizationService.localize(MessageConstants.ERROR_FROM_NIC), StringUtils.isNotEmpty(response.getErrorMessage()) ? response.getErrorMessage() : response.getContent());
                }
            } else {
                log.error("Factura provider response error while getting investmentObject : {}", response);
                return null; //In agreement with the PM, we decided to return null instead of throwing error
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            return null;
        });
    }

    public LgotaPrefixDTO[] getLgotaPrefix() {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            final String url = StringUtils.join(this.getFacturaHost(), "/ru/exchange/gettypes");
            final CustomResponse response = customRequest.sendGetRequest(url, getHeadersMap(), null);
            if (HttpStatus.OK.value() == response.getCode()) {
                try {
                    return objectMapper.readValue(response.getContent(), LgotaPrefixDTO[].class);
                } catch (final IOException e) {
                    log.error("Json parse error: {}", e.getMessage());
                }
            } else {
                log.error("GET Lgota prefix response: {}", response);
                throw new BadRequestException(parseNicError(response));
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public LotDTO getLot(final String lotId, final String buyerTin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            final NameValuePair[] params = new NameValuePair[2];
            params[0] = new BasicNameValuePair("lotId", lotId);
            params[1] = new BasicNameValuePair("buyerTin", buyerTin);
            final String url = StringUtils.join(this.getFacturaHost(), "/ru/exchange");
            final CustomResponse response = customRequest.sendGetRequest(url, getHeadersMap(), Arrays.stream(params).toList());
            if (HttpStatus.OK.value() == response.getCode()) {
                try {
                    return objectMapper.readValue(response.getContent(), LotDTO.class);
                } catch (final IOException e) {
                    log.error("Json parse error: {}", e.getMessage());
                }
            } else {
                log.error("GET Lot response: {}", response);
                throw new BadRequestException(parseNicError(response));
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public LotPrefixDTO[] getLotPrefix(final String lotId, final String buyerTin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            final NameValuePair[] params = new NameValuePair[2];
            params[0] = new BasicNameValuePair("lotId", lotId);
            params[1] = new BasicNameValuePair("buyerTin", buyerTin);
            final String url = StringUtils.join(this.getFacturaHost(), "/ru/exchange/getlotswithtype");
            final CustomResponse response = customRequest.sendGetRequest(url, getHeadersMap(), Arrays.stream(params).toList());
            if (response.getCode() == 200) {
                try {
                    return objectMapper.readValue(response.getContent(), LotPrefixDTO[].class);
                } catch (final IOException e) {
                    log.error("Json parse error: {}", e.getMessage());
                }
            } else {
                log.error("GET Lot with prefix response: {}", response);
                throw new BadRequestException(parseNicError(response));
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }
}
