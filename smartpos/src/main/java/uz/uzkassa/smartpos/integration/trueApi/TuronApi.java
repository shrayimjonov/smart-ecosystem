package uz.uzkassa.smartpos.integration.trueApi;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilderFactory;
import uz.uzkassa.smartpos.dto.turon.CisInfoWrapper;
import uz.uzkassa.smartpos.dto.turon.PublicAggregatedList;
import uz.uzkassa.smartpos.dto.turon.PublicCisInfo;

import java.util.*;

@Slf4j
@Component
public class TuronApi {
    private final RestTemplate restTemplate;
    private final UriBuilderFactory uriBuilderFactory;

    public TuronApi(@Qualifier("turonRestTemplate") RestTemplate restTemplate, UriBuilderFactory uriBuilderFactory) {
        this.restTemplate = restTemplate;
        this.uriBuilderFactory = uriBuilderFactory;
    }

    public AuthKey authKey() {
        return restTemplate.exchange("/auth/key", HttpMethod.GET, new HttpEntity<>(evaluateHeaders(null)), AuthKey.class).getBody();
    }

    public String simpleSignIn(AuthKey authKey) {
        AuthToken authToken = restTemplate.postForObject("/auth/simpleSignIn", new HttpEntity<>(authKey, evaluateHeaders(null)), AuthToken.class);
        return authToken != null ? authToken.getToken() : null;
    }

    public String refresh(String token) {
        AuthToken authToken = restTemplate.postForObject("/auth/refresh", new HttpEntity<>(evaluateHeaders(token)), AuthToken.class);
        return authToken != null ? authToken.getToken() : null;
    }

    public ResponseEntity<List<CisInfoWrapper<PublicCisInfo>>> cisesPublicInfo(String token, Collection<String> marks, Integer pg) {
        return restTemplate.exchange(uriBuilderFactory.builder()
                        .path("/cises/publicInfo")
                        .queryParamIfPresent("pg", Optional.ofNullable(pg))
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(marks, evaluateHeaders(token)), new ParameterizedTypeReference<>() {
                });
    }

    @Deprecated
    public ResponseEntity<Map<String, Object>> cisesAggregatedList(String token, Set<String> aggregations) {
        return restTemplate.exchange("/cises/aggregated/list", HttpMethod.POST, new HttpEntity<>(aggregations, evaluateHeaders(token)), new ParameterizedTypeReference<>() {
        });
    }

    @Deprecated
    public ResponseEntity<List<CisInfoWrapper<PublicAggregatedList>>> cisesAggregatedPublicList(String token, Set<String> aggregations, Integer pg) {
        return restTemplate.exchange(uriBuilderFactory.builder()
                        .path("/cises/aggregated/publicList")
                        .queryParamIfPresent("pg", Optional.ofNullable(pg))
                        .toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(aggregations, evaluateHeaders(token)), new ParameterizedTypeReference<>() {
                });
    }

    public WithdrawResponseDTO withdraw(String token, TuronWithdrawRequestDTO withdrawRequestDTO) {
        return restTemplate.postForObject("/documents/withdraw", new HttpEntity<>(withdrawRequestDTO, evaluateHeaders(token)), WithdrawResponseDTO.class);
    }

    public HttpHeaders evaluateHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.isNotBlank(token)) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

}
