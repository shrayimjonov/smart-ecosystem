package uz.uzkassa.horeca.integration;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.horeca.dto.soliq.MxikDTO;
import uz.uzkassa.horeca.dto.soliq.MxikResponseDTO;
import uz.uzkassa.horeca.dto.soliq.ProductSearchDTO;
import uz.uzkassa.horeca.dto.tasnif.TasnifBasketProductResponseListDTO;
import uz.uzkassa.horeca.dto.tasnif.TasnifMxikHistoryResponseListDTO;
import uz.uzkassa.horeca.dto.tasnif.TasnifPackageDTO;
import uz.uzkassa.horeca.dto.tasnif.TasnifPackageResponseDTO;
import uz.uzkassa.horeca.utils.HeaderUtils;
import uz.uzkassa.horeca.web.rest.errors.IntegrationException;

import java.util.List;

@Component
@Slf4j
public class TasnifClient extends BaseClient {

    public TasnifBasketProductResponseListDTO getCatalogByTin(String tin) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(applicationProperties.getTasnifConfig().getHost())
                .path(applicationProperties.getTasnifConfig().getBasketProductsPath())
                .queryParam("page", 0)
                .queryParam("size", 5000)
                .queryParam("lang", "ru")
                .queryParam(tin.length() == 9 ? "tin" : "pinfl", tin);

        try {
            ResponseEntity<TasnifBasketProductResponseListDTO> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(HeaderUtils.getHeader()),
                    TasnifBasketProductResponseListDTO.class
            );

            if (HttpStatus.OK == response.getStatusCode()) {
                return response.getBody();
            } else {
                throw new IntegrationException(response.toString());
            }
        } catch (Exception e) {
            throw new IntegrationException("Get Tasnif basket product error", e.getMessage());
        }
    }

    public TasnifMxikHistoryResponseListDTO getMxikHistory(String mxik) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(applicationProperties.getTasnifConfig().getHost())
                .path(applicationProperties.getTasnifConfig().getMxikHistoryPath())
                .path("/")
                .path(mxik);
        try {
            ResponseEntity<TasnifMxikHistoryResponseListDTO> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(HeaderUtils.getHeader()),
                    TasnifMxikHistoryResponseListDTO.class
            );

            if (HttpStatus.OK == response.getStatusCode()) {
                return response.getBody();
            } else {
                throw new IntegrationException(response.toString());
            }
        } catch (Exception e) {
            throw new IntegrationException("Get Tasnif Mxik history error", e.getMessage());
        }
    }

    public List<MxikDTO> getCompanyCatalogByProduct(String tin, String mxikCode) {
        StringBuilder url = new StringBuilder();
        url.append("https://tasnif.soliq.uz/api/cl-api/integration-mxik/company/get/basket-products?lang=ru&page=0&size=1");
        if (tin.length() == 9) {
            url.append("&tin=").append(tin);
        } else {
            url.append("&pinfl=").append(tin);
        }
        url.append("&mxik=").append(mxikCode);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url.toString(),
                    HttpMethod.GET,
                    new HttpEntity<>(HeaderUtils.getHeader()),
                    String.class
            );
            if (HttpStatus.OK == response.getStatusCode()) {
                MxikResponseDTO mxikResponseDTO = objectMapper.readValue(response.getBody(), MxikResponseDTO.class);
                if (mxikResponseDTO != null) {
                    return mxikResponseDTO.getData();
                }
                throw new IntegrationException(localizationService.localize("get.company.catalog.error"), response.getBody(), response.getStatusCode());
            } else {
                log.error("Get tasnif company catalog error response: {}", response);
                throw new IntegrationException(localizationService.localize("get.company.catalog.error"), response.getBody(), response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Json parse error: {}", e.getMessage());
            throw new IntegrationException(localizationService.localize("get.company.catalog.error"));
        }
    }

    public List<TasnifPackageDTO> getPackagesByCatalogCode(String catalogCode) {
        StringBuilder url = new StringBuilder();
        url.append("https://tasnif.soliq.uz/api/cl-api/integration-mxik/get/package?pageNo=0&check_user=0");
        url.append("&mxikCode=").append(catalogCode);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url.toString(),
                    HttpMethod.GET,
                    new HttpEntity<>(HeaderUtils.getHeader()),
                    String.class
            );
            if (HttpStatus.OK == response.getStatusCode()) {
                TasnifPackageResponseDTO tasnifResponseDTO = objectMapper.readValue(response.getBody(), TasnifPackageResponseDTO.class);
                if (tasnifResponseDTO != null) {
                    return tasnifResponseDTO.getData();
                }
                throw new IntegrationException(localizationService.localize("get.company.catalog.error"), response.getBody(), response.getStatusCode());
            } else {
                log.error("Get tasnif company catalog error response: {}", response);
                throw new IntegrationException(localizationService.localize("get.company.catalog.error"), response.getBody(), response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Json parse error: {}", e.getMessage());
            throw new IntegrationException(localizationService.localize("get.company.catalog.error"));
        }
    }

    public List<MxikDTO> searchByParams(ProductSearchDTO productSearchDTO) {
        if (StringUtils.isEmpty(productSearchDTO.getLang())) {
            productSearchDTO.setLang(HeaderUtils.getCurrentRequestLanguageEnum().getCode());
        }
        StringBuilder url = new StringBuilder();
        url.append("https://tasnif.soliq.uz/api/cl-api/integration-mxik/search/by-params");
        url.append("?lang=").append(productSearchDTO.getLang());
        if (StringUtils.isNotEmpty(productSearchDTO.getText())) {
            url.append("&text=").append(productSearchDTO.getText());
        }
        if (StringUtils.isNotEmpty(productSearchDTO.getMxikCode())) {
            url.append("&mxikCode=").append(productSearchDTO.getMxikCode());
        }
        if (StringUtils.isNotEmpty(productSearchDTO.getGtin())) {
            url.append("&gtin=").append(productSearchDTO.getGtin());
        }
        if (StringUtils.isNotEmpty(productSearchDTO.getBrandName())) {
            url.append("&brandName=").append(productSearchDTO.getBrandName());
        }
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url.toString(),
                    HttpMethod.GET,
                    new HttpEntity<>(HeaderUtils.getHeader()),
                    String.class
            );
            if (HttpStatus.OK == response.getStatusCode()) {
                MxikResponseDTO mxikResponseDTO = objectMapper.readValue(response.getBody(), MxikResponseDTO.class);
                if (mxikResponseDTO != null) {
                    return mxikResponseDTO.getData();
                }
                throw new IntegrationException("Search product by params error", response.getBody(), response.getStatusCode());
            } else {
                log.error("Search product by params error response: {}", response);
                throw new IntegrationException("Search product by params error", response.getBody(), response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Json parse error: {}", e.getMessage());
            throw new IntegrationException("Search product by params error");
        }
    }
}
