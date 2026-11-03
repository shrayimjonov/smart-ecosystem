package uz.uzkassa.smartpossupply.integration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActDTO;
import uz.uzkassa.smartpossupply.dto.auth.AdminLoginDTO;
import uz.uzkassa.smartpossupply.dto.auth.JWTToken;
import uz.uzkassa.smartpossupply.dto.branch.SmartposBranchListDTO;
import uz.uzkassa.smartpossupply.dto.order.SmartPosOrderWebHookDTO;
import uz.uzkassa.smartpossupply.dto.receipt.SmartposReceiptDTO;
import uz.uzkassa.smartpossupply.enums.AcceptanceTransferActStatus;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;
import uz.uzkassa.smartpossupply.web.rest.errors.SmartposIntegrationException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.11.2023
 */
@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class SmartposClient extends BaseClient {

    static LocalDateTime authenticateRefreshTime;
    static String tokenStore;

    public IntegrationType getIntegrationType() {
        return IntegrationType.SMARTPOS;
    }

    public String getToken() {
        if (tokenStore != null && authenticateRefreshTime != null && authenticateRefreshTime.isAfter(LocalDateTime.now())) {
            return tokenStore;
        }
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            AdminLoginDTO loginDTO = new AdminLoginDTO();
            loginDTO.setPassword(applicationProperties.getSmartposConfig().getPassword());
            loginDTO.setUsername(applicationProperties.getSmartposConfig().getUsername());
            loginDTO.setRememberMe(true);

            HttpHeaders headers = SecurityUtils.getHeader();
            HttpEntity<AdminLoginDTO> entity = new HttpEntity<>(loginDTO, headers);
            ResponseEntity<JWTToken> response = restTemplate.exchange(
                    applicationProperties.getSmartposConfig().getAuthUrl(),
                    HttpMethod.POST,
                    entity,
                    JWTToken.class
            );
            tokenStore = Objects.requireNonNull(response.getBody()).getIdToken();
            long tokenValidityInSecondsForRememberMe = 25200;
            authenticateRefreshTime = LocalDateTime.now().plusSeconds(tokenValidityInSecondsForRememberMe);
            return tokenStore;
        }, throwable -> {
            tokenStore = null;
            log.error("Get Smartpos token error: {}", throwable.getMessage(), throwable);
            throw new IntegrationException("Smartpos Trade authentication failed", throwable.getMessage());
        });
    }

    public void updateOrderStatus(Long orderId, SmartPosOrderWebHookDTO smartPosOrderWebHookDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());

            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString(applicationProperties.getSmartposConfig().getOrderUrl().concat("/").concat(orderId.toString()).concat("/status/webhook"));
            HttpEntity<?> entity = new HttpEntity<>(smartPosOrderWebHookDTO, headers);
            try {
                ResponseEntity<Object> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.PUT, entity, Object.class);
                response.getBody();
            } catch (Exception e) {
                log.error("Updating order status from Smartpos error :{}", e.getMessage());
                throw new IntegrationException("Updating order status from Smartpos error", e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public Long sendAcceptanceTransferAct(AcceptanceTransferActDTO dto) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBearerAuth(this.getToken());

            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString(applicationProperties.getSmartposConfig().getAcceptanceTransferActUrl());
            HttpEntity<?> entity = new HttpEntity<>(dto, headers);
            try {
                ResponseEntity<Long> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, Long.class);
                return response.getBody();
            } catch (Exception e) {
                log.error("Sending Acceptance transfer act from Smartpos error :{}", e.getMessage());
                throw new IntegrationException("Sending Acceptance transfer act from Smartpos error", e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public void updateAcceptanceTransferStatus(Long acceptanceTransferActSmartposId, AcceptanceTransferActStatus status) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            String token = this.getToken();
            headers.setBearerAuth(token);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString(applicationProperties.getSmartposConfig().getAcceptanceTransferActUrl().concat("/").concat(acceptanceTransferActSmartposId.toString()));
            HttpEntity<?> entity = new HttpEntity<>(status, headers);
            try {
                ResponseEntity<Object> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.PUT, entity, Object.class);
                response.getBody();
            } catch (Exception e) {
                log.error("Updating acceptance transfer act status from Smartpos error :{}", e.getMessage());
                throw new IntegrationException("Updating acceptance transfer act status from Smartpos error", e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public SmartposBranchListDTO getBranches(String tin) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            String token = this.getToken();
            headers.setBearerAuth(token);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString(applicationProperties.getSmartposConfig().getAdminBaseUrl().concat("supply/company/").concat(tin).concat("/branches"));
            HttpEntity<?> entity = new HttpEntity<>(headers);
            try {
                ResponseEntity<String> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, entity, String.class);
                return objectMapper.readValue(response.getBody(), SmartposBranchListDTO.class);
            } catch (Exception e) {
                log.error("Getting company branches from Smartpos error :{}", e.getMessage());
                throw new IntegrationException("Getting company branches from Smartpos error", e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public void sendReceipt(SmartposReceiptDTO receiptDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            String token = this.getToken();
            headers.setBearerAuth(token);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString(applicationProperties.getSmartposConfig().getAdminBaseUrl().concat("supply/receipt"));
            HttpEntity<?> entity = new HttpEntity<>(receiptDTO, headers);
            try {
                ResponseEntity<Object> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, Object.class);
                response.getBody();
            } catch (Exception e) {
                log.error("SEND RECEIPT TO SMARTPOS ERROR :{}", e.getMessage());
                throw new IntegrationException("SEND RECEIPT TO SMARTPOS ERROR", e.getMessage());
            }
            return null;
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public ResponseEntity<String> getReceiptByUid(String uid) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            String token = this.getToken();
            headers.setBearerAuth(token);
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString(applicationProperties.getSmartposConfig().getAdminBaseUrl().concat("supply/receipt/").concat(uid));
            HttpEntity<?> entity = new HttpEntity<>(headers);
            try {
                return restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, entity, String.class);
            } catch (Exception e) {
                throw new SmartposIntegrationException("Smartpos error", e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }
}
