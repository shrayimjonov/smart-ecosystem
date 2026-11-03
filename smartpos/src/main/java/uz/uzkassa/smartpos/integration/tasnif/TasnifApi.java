package uz.uzkassa.smartpos.integration.tasnif;


import jakarta.validation.constraints.NotNull;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.integration.tasnif.dto.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public final class TasnifApi {

    private final RestClient restClient;

    public TasnifApi(@Qualifier("pooledIntegrationHttpClient") CloseableHttpClient httpClient, ApplicationProperties applicationProperties) {
        restClient = RestClient.builder()
                .baseUrl(applicationProperties.getTasnifApiUrl())
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    public ResponseEntity<TasnifResponseDTO<List<CompanyProductDTO>>> getCompanyCatalog(final String tin,
                                                                                        final String search) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/integration-mxik/company/get/basket-products")
                        .queryParam("lang", "ru")
                        .queryParamIfPresent("tin", Optional.ofNullable(tin))
                        .queryParamIfPresent("text", Optional.ofNullable(search).map(String::toLowerCase))
                        .queryParam("page", "0")
                        .queryParam("size", "1000")
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<TasnifResponseDTO<List<MxikCheckWrapperDTO>>> checkMxiksFromHazelcast(List<String> body,
                                                                                                String lang,
                                                                                                String tin) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/integration-mxik/check/hazelcast")
                        .queryParamIfPresent("lang", Optional.ofNullable(lang))
                        .queryParamIfPresent("tin", Optional.ofNullable(tin))
                        .build())
                .body(body)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<TasnifResponseDTO<List<MxikPackage>>> getPackages(final String mxik) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/integration-mxik/get/package")
                        .queryParam("pageNo", 0)
                        .queryParam("pagesize", 500)
                        .queryParam("mxikCode", mxik)
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<TasnifResponseDTO<List<MxikHistoryDTO>>> historyByTime(@NotNull Long endDate,
                                                                                 @NotNull Long startDate,
                                                                                 String lang,
                                                                                 Integer page,
                                                                                 Integer size,
                                                                                 Integer status) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/integration-mxik/get/history/time")
                        .queryParamIfPresent("endDate", Optional.of(endDate))
                        .queryParamIfPresent("startDate", Optional.of(startDate))
                        .queryParamIfPresent("lang", Optional.ofNullable(lang))
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .queryParamIfPresent("status", Optional.ofNullable(status))
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public ResponseEntity<TasnifResponseDTO<List<BasicMxikPackage>>> getBasicPackages(Set<String> mxikCodes) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/mxik/check/basic-package/hazelcast")
                        .build())
                .body(mxikCodes)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }
}
