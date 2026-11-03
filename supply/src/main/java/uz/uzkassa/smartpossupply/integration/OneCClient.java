package uz.uzkassa.smartpossupply.integration;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.smartpossupply.dto.stockin.onec.StockIn1CDTO;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 25.01.2024
 */
@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OneCClient extends BaseClient {

    public IntegrationType getIntegrationType() {
        return IntegrationType.ONE_C;
    }

    public boolean send(String url, String username, String password, StockIn1CDTO stockIn1CDTO) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            HttpHeaders headers = SecurityUtils.getHeader();
            headers.setBasicAuth(username, password);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
            HttpEntity<?> entity = new HttpEntity<>(stockIn1CDTO, headers);
            try {
                //log.info("Sending stockIn to 1C: {}", objectMapper.writeValueAsString(stockIn1CDTO));
                ResponseEntity<?> response = restTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.POST,
                        entity,
                        Object.class
                );
                return HttpStatus.OK == response.getStatusCode();
            } catch (Exception e) {
                log.error("Sending stockIn 1C error :{}", e.getMessage());
                return false;
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            return false;
        });
    }
}