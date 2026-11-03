package uz.uzkassa.smartpossupply.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.smartpossupply.constants.MessageConstants;
import uz.uzkassa.smartpossupply.dto.soliq.MxikDTO;
import uz.uzkassa.smartpossupply.dto.soliq.MxikResponseDTO;
import uz.uzkassa.smartpossupply.dto.soliq.ProductPackageSyncDTO;
import uz.uzkassa.smartpossupply.dto.soliq.TasnifPackageDTO;
import uz.uzkassa.smartpossupply.dto.soliq.TasnifResponseDTO;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import java.util.List;

@Component
@Slf4j
public class TasnifClient extends BaseClient {

    @Qualifier("tasnifRestTemplate")
    private final RestTemplate tasnifRestTemplate;

    public TasnifClient(RestTemplate tasnifRestTemplate) {
        this.tasnifRestTemplate = tasnifRestTemplate;
    }

    public IntegrationType getIntegrationType() {
        return IntegrationType.TASNIF;
    }

    // Renders request headers as curl "-H" flags so a log line can be copy-pasted into a terminal.
    private static String toCurl(HttpHeaders headers) {
        StringBuilder sb = new StringBuilder();
        headers.forEach((name, values) ->
                values.forEach(value -> sb.append("-H '").append(name).append(": ").append(value).append("' ")));
        return sb.toString().trim();
    }

    public List<MxikDTO> getCompanyCatalog(String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(applicationProperties.getTasnifConfig().getHost());
            url.append("integration-mxik/company/get/basket-products?lang=ru&page=0&size=20000");
            if (tin.length() == 9) {
                url.append("&tin=").append(tin);
            } else {
                url.append("&pinfl=").append(tin);
            }
            HttpHeaders headers = SecurityUtils.getHeader();
            log.info("TASNIF getCompanyCatalog REQUEST | tin={} | curl -X GET '{}' {}",
                    tin, url, toCurl(headers));
            long start = System.currentTimeMillis();
            try {
                ResponseEntity<String> response = tasnifRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class
                );
                log.info("TASNIF getCompanyCatalog RESPONSE | tin={} | status={} | {}ms | body={}",
                        tin, response.getStatusCode(), System.currentTimeMillis() - start, response.getBody());
                if (HttpStatus.OK == response.getStatusCode()) {
                    MxikResponseDTO mxikResponseDTO = objectMapper.readValue(response.getBody(), MxikResponseDTO.class);
                    if (mxikResponseDTO != null) {
                        return mxikResponseDTO.getData();
                    }
                    throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_CATALOG_ERROR), response.getBody(), response.getStatusCode());
                } else {
                    log.error("Get tasnif company catalog error response: {}", response);
                    throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_CATALOG_ERROR), response.getBody(), response.getStatusCode());
                }
            } catch (IntegrationException e) {
                throw e;
            } catch (Exception e) {
//                log.error("Json parse error: {}", e.getMessage());
                log.error("TASNIF getCompanyCatalog FAILED | tin={} | {}ms | {}: {}",
                        tin, System.currentTimeMillis() - start, e.getClass().getName(), e.getMessage(), e);
                throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_CATALOG_ERROR), e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public List<MxikDTO> getCompanyCatalogByProduct(String tin, String mxikCode) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(applicationProperties.getTasnifConfig().getHost());
            url.append("integration-mxik/company/get/basket-products?lang=ru&page=0&size=1");
            if (tin.length() == 9) {
                url.append("&tin=").append(tin);
            } else {
                url.append("&pinfl=").append(tin);
            }
            url.append("&mxik=").append(mxikCode);
            try {
                ResponseEntity<String> response = tasnifRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(SecurityUtils.getHeader()),
                        String.class
                );
                if (HttpStatus.OK == response.getStatusCode()) {
                    MxikResponseDTO mxikResponseDTO = objectMapper.readValue(response.getBody(), MxikResponseDTO.class);
                    if (mxikResponseDTO != null) {
                        return mxikResponseDTO.getData();
                    }
                    throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_CATALOG_ERROR), response.getBody(), response.getStatusCode());
                } else {
                    log.error("Get tasnif company catalog error response: {}", response);
                    throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_CATALOG_ERROR), response.getBody(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_CATALOG_ERROR));
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public ProductPackageSyncDTO getProductPackageByCatalogCode(String catalogCode) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            // NOTE (merge): prod calls a DIFFERENT host for this endpoint (tasnif.soliq.uz/api/cls-api/),
            // not the rouming proxy used by the other 3 methods. Kept prod's host to preserve behavior.
            url.append("https://tasnif.soliq.uz/api/cls-api/");
            url.append("mxik/get/by-mxik?mxikCode=");
            url.append(catalogCode);
            url.append("&lang=ru");
            try {
                ResponseEntity<String> response = tasnifRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(SecurityUtils.getHeader()),
                        String.class
                );
                if (HttpStatus.OK == response.getStatusCode()) {
                    ProductPackageSyncDTO productPackageSyncDTO = objectMapper.readValue(response.getBody(), ProductPackageSyncDTO.class);
                    if (productPackageSyncDTO != null) {
                        return productPackageSyncDTO;
                    }
                } else {
                    log.error("Get tasnif company catalog error response: {}", response);
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            return null;
        });
    }

    public List<TasnifPackageDTO> getPackagesByCatalogCode(String catalogCode) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            StringBuilder url = new StringBuilder();
            url.append(applicationProperties.getTasnifConfig().getHost());
            url.append("integration-mxik/get/package?pageNo=0&check_user=0");
            url.append("&mxikCode=").append(catalogCode);
            try {
                ResponseEntity<String> response = tasnifRestTemplate.exchange(
                        url.toString(),
                        HttpMethod.GET,
                        new HttpEntity<>(SecurityUtils.getHeader()),
                        String.class
                );
                if (HttpStatus.OK == response.getStatusCode()) {
                    TasnifResponseDTO tasnifResponseDTO = objectMapper.readValue(response.getBody(), TasnifResponseDTO.class);
                    if (tasnifResponseDTO != null) {
                        return tasnifResponseDTO.getData();
                    }
                    throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_CATALOG_ERROR), response.getBody(), response.getStatusCode());
                } else {
                    log.error("Get tasnif company catalog error response: {}", response);
                    throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_CATALOG_ERROR), response.getBody(), response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Json parse error: {}", e.getMessage());
                throw new IntegrationException(localizationService.localize(MessageConstants.GET_COMPANY_CATALOG_ERROR));
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }
}