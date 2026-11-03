package uz.uzkassa.smartpossupply.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.smartpossupply.dto.auth.AdminLoginDTO;
import uz.uzkassa.smartpossupply.dto.auth.TokenDTO;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceBranchSyncDTO;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceCompanyBankSyncDTO;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceCompanyBatchSyncDTO;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceCompanySyncDTO;
import uz.uzkassa.smartpossupply.dto.sync.marketplace.MarketplaceApplicationSyncDTO;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;


@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class MarketplaceClient extends BaseClient {

    static final String MASKED_VALUE = "***";

    public IntegrationType getIntegrationType() {
        return IntegrationType.MARKETPLACE;
    }

    /**
     * Builds a copy-pasteable single-line curl command without exposing credentials.
     * Keeping the command on one physical line prevents log collectors from splitting
     * and reordering its parts.
     */
    private String toCurl(HttpMethod method,
                          String url,
                          HttpHeaders headers,
                          Object body,
                          Map<String, ?> queryParams) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        if (queryParams != null) {
            queryParams.forEach((name, value) -> addQueryParam(uriBuilder, name, value));
        }

        // Parameters that were already present in the URL must be masked as well.
        new ArrayList<>(uriBuilder.build().getQueryParams().keySet()).stream()
                .filter(MarketplaceClient::isSensitive)
                .forEach(name -> uriBuilder.replaceQueryParam(name, MASKED_VALUE));

        List<String> parts = new ArrayList<>();
        parts.add("curl --request " + shellQuote(method.name()));
        parts.add("--url " + shellQuote(uriBuilder.build().toUriString()));

        if (headers != null) {
            headers.forEach((name, values) -> values.forEach(value -> {
                String safeValue = isSensitive(name) ? maskHeaderValue(name, value) : value;
                parts.add("--header " + shellQuote(name + ": " + safeValue));
            }));
        }

        if (body != null) {
            parts.add("--data-raw " + shellQuote(toSafeBody(body)));
        }

        return String.join(" ", parts);
    }

    private static void addQueryParam(UriComponentsBuilder uriBuilder, String name, Object value) {
        if (value == null) {
            uriBuilder.queryParam(name);
            return;
        }

        if (value instanceof Iterable<?> values) {
            values.forEach(item -> addQueryParam(uriBuilder, name, item));
            return;
        }

        if (value.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(value); i++) {
                addQueryParam(uriBuilder, name, Array.get(value, i));
            }
            return;
        }

        uriBuilder.queryParam(name, isSensitive(name) ? MASKED_VALUE : value);
    }

    private String toSafeBody(Object body) {
        try {
            JsonNode json;
            if (body instanceof String stringBody) {
                try {
                    json = objectMapper.readTree(stringBody);
                } catch (JsonProcessingException e) {
                    return stringBody;
                }
                if (json == null) {
                    return stringBody;
                }
            } else {
                json = objectMapper.valueToTree(body);
            }
            JsonNode safeJson = json.deepCopy();
            maskSensitiveFields(safeJson);
            return objectMapper.writeValueAsString(safeJson);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            return "<body serialization failed: " + e.getClass().getSimpleName() + ">";
        }
    }

    private static void maskSensitiveFields(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.properties().forEach(entry -> {
                if (isSensitive(entry.getKey())) {
                    objectNode.put(entry.getKey(), MASKED_VALUE);
                } else {
                    maskSensitiveFields(entry.getValue());
                }
            });
        } else if (node.isArray()) {
            node.forEach(MarketplaceClient::maskSensitiveFields);
        }
    }

    private static boolean isSensitive(String name) {
        String normalized = name.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
        return normalized.contains("authorization")
                || normalized.contains("token")
                || normalized.contains("apikey")
                || normalized.contains("password")
                || normalized.contains("secret")
                || normalized.contains("cookie");
    }

    private static String maskHeaderValue(String name, String value) {
        String normalized = name.toLowerCase(Locale.ROOT).replace("-", "");
        if (normalized.contains("authorization")) {
            int separator = value.indexOf(' ');
            if (separator > 0) {
                return value.substring(0, separator) + " " + MASKED_VALUE;
            }
        }
        return MASKED_VALUE;
    }

    private static String shellQuote(String value) {
        return "'" + value.replace("'", "'\"'\"'").replace("\r", " ").replace("\n", " ") + "'";
    }

    public String getToken() {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            AdminLoginDTO loginDTO = new AdminLoginDTO();
            loginDTO.setUsername(applicationProperties.getMarketplaceConfig().getUsername());
            loginDTO.setPassword(applicationProperties.getMarketplaceConfig().getPassword());
            loginDTO.setRememberMe(true);

            HttpHeaders headers = SecurityUtils.getHeader();
            HttpEntity<AdminLoginDTO> entity = new HttpEntity<>(loginDTO, headers);
            ResponseEntity<TokenDTO> response = restTemplate.exchange(
                    applicationProperties.getMarketplaceConfig().getAuthUrl(),
                    HttpMethod.POST,
                    entity,
                    TokenDTO.class
            );
            return Objects.requireNonNull(response.getBody()).getAccessToken();
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public Long syncCompanyBatch(MarketplaceCompanyBatchSyncDTO marketplaceCompanyBatchSyncDTO) {
        log.info("Sync company batch curl: {}",
                toCurl(HttpMethod.POST, applicationProperties.getMarketplaceConfig().getCompanyBatchSyncUrl(), SecurityUtils.getHeader(), marketplaceCompanyBatchSyncDTO, null));
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(applicationProperties.getMarketplaceConfig().getCompanyBatchSyncUrl());
            HttpEntity<?> entity = new HttpEntity<>(marketplaceCompanyBatchSyncDTO, headers);
            ResponseEntity<String> response;
            try {
                response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, String.class);
            } catch (Exception e) {
                throw new IntegrationException("Company sync error", e.getMessage());
            }
            if (response.getStatusCode().is2xxSuccessful()) {
                return objectMapper.convertValue(response.getBody(), Long.class);
            } else {
                throw new IntegrationException("Company sync error", response.getBody());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public Long syncCompany(MarketplaceCompanySyncDTO marketplaceCompanySyncDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(applicationProperties.getMarketplaceConfig().getCompanySyncUrl());
            HttpEntity<?> entity = new HttpEntity<>(marketplaceCompanySyncDTO, headers);
            ResponseEntity<Long> response;
            try {
                response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, Long.class);
            } catch (Exception e) {
                throw new IntegrationException("Company sync error", e.getMessage());
            }
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new IntegrationException("Company sync error", response.toString());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public Long updateCompany(MarketplaceCompanySyncDTO marketplaceCompanySyncDTO) {
        log.info("Update company {}, ", marketplaceCompanySyncDTO);
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(applicationProperties.getMarketplaceConfig().getCompanyUpdateUrl().replace("{id}", marketplaceCompanySyncDTO.getId().toString()));
            HttpEntity<?> entity = new HttpEntity<>(marketplaceCompanySyncDTO, headers);
            ResponseEntity<Long> response;
            try {
                response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.PUT, entity, Long.class);
            } catch (Exception e) {
                throw new IntegrationException("Company sync error", e.getMessage());
            }
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new IntegrationException("Company sync error", response.toString());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public Long syncCompanyBank(MarketplaceCompanyBankSyncDTO marketplaceCompanyBankSyncDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(applicationProperties.getMarketplaceConfig().getCompanyBankSyncUrl());
            HttpEntity<?> entity = new HttpEntity<>(marketplaceCompanyBankSyncDTO, headers);
            ResponseEntity<Long> response;
            try {
                response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, Long.class);
            } catch (Exception e) {
                throw new IntegrationException("Company bank sync error", e.getMessage());
            }
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new IntegrationException("Company bank sync error", response.toString());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public Long syncBranch(MarketplaceBranchSyncDTO marketplaceBranchSyncDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(applicationProperties.getMarketplaceConfig().getBranchSyncUrl());
            HttpEntity<?> entity = new HttpEntity<>(marketplaceBranchSyncDTO, headers);
            ResponseEntity<Long> response;
            try {
                response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, Long.class);
            } catch (Exception e) {
                throw new IntegrationException("Branch sync error", e.getMessage());
            }
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new IntegrationException("Branch sync error", response.toString());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public Long sendApplication(MarketplaceApplicationSyncDTO marketplaceApplicationSyncDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(applicationProperties.getMarketplaceConfig().getApplicationSyncUrl());
            HttpEntity<?> entity = new HttpEntity<>(marketplaceApplicationSyncDTO, headers);
            ResponseEntity<Long> response;
            try {
                response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, Long.class);
            } catch (Exception e) {
                log.error("Marketplace application sync response: {}", e.getMessage());
                throw new IntegrationException("Sending application to Marketplace response error : {}", e.getMessage());
            }
            return response.getBody();
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public void removeApplication(Long id) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(applicationProperties.getMarketplaceConfig().getApplicationRemoveUrl().replace("{id}", id.toString()));
            HttpEntity<?> entity = new HttpEntity<>(headers);
            try {
                restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.PUT, entity, Void.class);
            } catch (Exception e) {
                log.error("Marketplace application remove response: {}", e.getMessage());
                throw new IntegrationException("Removing application from Marketplace response error : {}", e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public void returnBackApplication(Long id) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(applicationProperties.getMarketplaceConfig().getApplicationReturnUrl().replace("{id}", id.toString()));
            HttpEntity<?> entity = new HttpEntity<>(headers);
            try {
                restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.PUT, entity, Void.class);
            } catch (Exception e) {
                log.error("Marketplace application return response: {}", e.getMessage());
                throw new IntegrationException("Return application to Marketplace response error : {}", e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }
}
