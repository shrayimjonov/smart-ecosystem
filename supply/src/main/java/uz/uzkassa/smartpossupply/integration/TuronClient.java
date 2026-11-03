package uz.uzkassa.smartpossupply.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.config.httpclient.CustomResponse;
import uz.uzkassa.smartpossupply.dto.integration.turon.aggregation.TuronCreateAggregationRequest;
import uz.uzkassa.smartpossupply.dto.integration.turon.aggregation.TuronCreateAggregationResponse;
import uz.uzkassa.smartpossupply.dto.integration.turon.mark.*;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;
import uz.uzkassa.smartpossupply.web.rest.errors.TuronErrorResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
@Slf4j
@Transactional(readOnly = true)
public class TuronClient extends BaseClient {

    public IntegrationType getIntegrationType() {
        return IntegrationType.TURON;
    }

    public TuronMarkOrderResponseDTO createMarkOrder(TuronMarkOrderDTO turonMarkOrderDTO, String extension, String token, String omsId) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            try {
                String url = applicationProperties.getTuronConfig().getHost() + "api/v2/" + extension + "/orders";
                NameValuePair[] params = new NameValuePair[1];
                params[0] = new BasicNameValuePair("omsId", omsId);
                String json = objectMapper.writeValueAsString(turonMarkOrderDTO);
                CustomResponse response = customRequest.sendPostRequest(url, Arrays.stream(params).toList(), getClientHeaders(token), json);
                writeLog("Create Mark Order", url, response);
                if (HttpStatus.OK.value() == response.getCode()) {
                    return objectMapper.readValue(response.getContent(), TuronMarkOrderResponseDTO.class);
                } else {
                    TuronErrorResponse errorFromTuron = objectMapper.readValue(response.getContent(), TuronErrorResponse.class);
                    StringBuilder message = new StringBuilder();
                    message.append("REQUEST: \n");
                    message.append(url).append("\n");
                    message.append("TOKEN: ").append(token).append("\n");
                    message.append("OMS ID: ").append(omsId).append("\n");
                    message.append("STATUS: ").append(response.getCode()).append("\n");
                    message.append("MESSAGE: ").append(errorFromTuron).append("\n");
                    message.append("RESPONSE: \n");
                    message.append(response.getContent());
                    throw new IntegrationException(errorFromTuron.toString());
                }
            } catch (Exception e) {
                log.error("Turon order convert to JSON error: {}", e.getMessage());
                throw new IntegrationException(e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public TuronGetMarksResponseDTO getMarks(
            String extension,
            String orderId,
            String lastBlockId,
            String gtin,
            Integer qty,
            String clientToken,
            String omsId
    ) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            try {
                String url = applicationProperties.getTuronConfig().getHost() + "api/v2/" + extension + "/codes";
                NameValuePair[] params = new NameValuePair[5];
                params[0] = new BasicNameValuePair("omsId", omsId);
                params[1] = new BasicNameValuePair("orderId", orderId);
                params[2] = new BasicNameValuePair("gtin", gtin);
                params[3] = new BasicNameValuePair("quantity", String.valueOf(qty));
                params[4] = new BasicNameValuePair("lastBlockId", lastBlockId);
                CustomResponse response = customRequest.sendGetRequest(url, getClientHeaders(clientToken), Arrays.stream(params).toList());
                writeLog("Get Marks", url, response);
                if (HttpStatus.OK.value() == response.getCode()) {
                    return objectMapper.readValue(response.getContent(), TuronGetMarksResponseDTO.class);
                } else {
                    log.error("Error from turon url {}, {}", url, response);
                    TuronErrorResponse errorFromTuron = objectMapper.readValue(response.getContent(), TuronErrorResponse.class);
                    StringBuilder message = new StringBuilder();
                    message.append("REQUEST: \n");
                    message.append(url).append("\n");
                    message.append("TOKEN: ").append(clientToken).append("\n");
                    message.append("OMS ID: ").append(omsId).append("\n");
                    message.append("ORDER ID: ").append(orderId).append("\n");
                    message.append("GTIN: ").append(gtin).append("\n");
                    message.append("QUANTITY: ").append(qty).append("\n");
                    message.append("STATUS: ").append(response.getCode()).append("\n");
                    message.append("MESSAGE: ").append(errorFromTuron).append("\n");
                    message.append("RESPONSE: \n");
                    message.append(response.getContent());
                    throw new IntegrationException(message.toString());
                }
            } catch (Exception e) {
                log.error("Turon order convert to JSON error: {}", e.getMessage());
                throw new IntegrationException(e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public TuronUtilisationReportResponseDTO apply(String clientToken, String extension, String omsId, TuronUtilisationReportRequestDTO request) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            try {
                String json = objectMapper.writeValueAsString(request);
                String url = applicationProperties.getTuronConfig().getHost() + "api/v2/" + extension + "/utilisation";
                NameValuePair[] params = new NameValuePair[1];
                params[0] = new BasicNameValuePair("omsId", omsId);
                CustomResponse response = customRequest.sendPostRequest(url, Arrays.stream(params).toList(), getClientHeaders(clientToken), json);
                writeLog("Part utilisation", url, response);
                StringBuilder message = new StringBuilder();
                message.append("REQUEST: \n");
                message.append(url).append("\n");
                message.append("TOKEN: ").append(clientToken).append("\n");
                message.append("OMS ID: ").append(omsId).append("\n");
                if (HttpStatus.OK.value() == response.getCode()) {
                    return objectMapper.readValue(response.getContent(), TuronUtilisationReportResponseDTO.class);
                }
                if (HttpStatus.NOT_FOUND.value() == response.getCode()) {
                    message.append("STATUS: ").append(response.getCode()).append("\n");
                    message.append("MESSAGE: ").append(response.getErrorMessage()).append("\n");
                    message.append("RESPONSE: \n").append(response.getContent());
                    log.error(message.toString());
                    return null;
                } else {
                    message.append("STATUS: ").append(response.getCode()).append("\n");
                    message.append("MESSAGE: ").append(response.getErrorMessage()).append("\n");
                    message.append("RESPONSE: \n").append(response.getContent());

                    TuronErrorResponse errorFromTuron = objectMapper.readValue(response.getContent(), TuronErrorResponse.class);
                    throw new IntegrationException(errorFromTuron.toString());
                }
            } catch (Exception e) {
                log.error("Turon order convert to JSON error: {}", e.getMessage());
                throw new IntegrationException(e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public TuronMarkOrderCheckStatusResponseDTO checkStatus(
            String extension,
            String orderId,
            String gtin,
            String clientToken,
            String omsId
    ) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            try {
                String url = applicationProperties.getTuronConfig().getHost() + "api/v2/" + extension + "/buffer/status";
                NameValuePair[] params = new NameValuePair[3];
                params[0] = new BasicNameValuePair("omsId", omsId);
                params[1] = new BasicNameValuePair("orderId", orderId);
                params[2] = new BasicNameValuePair("gtin", gtin);
                CustomResponse response = customRequest.sendGetRequest(url, getClientHeaders(clientToken), Arrays.stream(params).toList());
                writeLog("Check Status", url, response);
                StringBuilder message = new StringBuilder();
                message.append("REQUEST: \n");
                message.append(url).append("\n");
                message.append("TOKEN: ").append(clientToken).append("\n");
                message.append("OMS ID: ").append(omsId).append("\n");
                message.append("ORDER ID: ").append(orderId).append("\n");
                message.append("GTIN: ").append(gtin).append("\n");
                if (HttpStatus.OK.value() == response.getCode()) {
                    return objectMapper.readValue(response.getContent(), TuronMarkOrderCheckStatusResponseDTO.class);
                }
                if (HttpStatus.NOT_FOUND.value() == response.getCode()) {
                    message.append("STATUS: ").append(response.getCode()).append("\n");
                    message.append("MESSAGE: ").append(response.getErrorMessage()).append("\n");
                    message.append("RESPONSE: \n").append(response.getContent());
                    log.error(message.toString());
                    return null;
                } else {
                    message.append("STATUS: ").append(response.getCode()).append("\n");
                    message.append("MESSAGE: ").append(response.getErrorMessage()).append("\n");
                    message.append("RESPONSE: \n").append(response.getContent());

                    TuronErrorResponse errorFromTuron = objectMapper.readValue(response.getContent(), TuronErrorResponse.class);
                    throw new IntegrationException(errorFromTuron.toString());
                }
            } catch (JsonProcessingException e) {
                log.error("Turon order convert to JSON error: {}", e.getMessage());
                throw new IntegrationException(e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public TuronCreateAggregationResponse createAggregation(
            TuronCreateAggregationRequest request,
            String extension,
            String clientToken,
            String omsId
    ) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            try {
                String json = objectMapper.writeValueAsString(request);
                String url = applicationProperties.getTuronConfig().getHost() + "api/v2/" + extension + "/aggregation?omsId=" + omsId;
                CustomResponse response = customRequest.sendPostRequest(url, getClientHeaders(clientToken), json);
                writeLog("Check Status", url, response);
                if (HttpStatus.OK.value() == response.getCode()) {
                    return objectMapper.readValue(response.getContent(), TuronCreateAggregationResponse.class);
                } else {
                    TuronErrorResponse errorFromTuron = objectMapper.readValue(response.getContent(), TuronErrorResponse.class);

                    StringBuilder message = new StringBuilder();
                    message.append("REQUEST: \n");
                    message.append(url).append("\n");
                    message.append("TOKEN: ").append(clientToken).append("\n");
                    message.append("OMS ID: ").append(omsId).append("\n");
                    message.append("STATUS: ").append(response.getCode()).append("\n");
                    message.append("MESSAGE: ").append(errorFromTuron).append("\n");
                    message.append("RESPONSE: \n");
                    message.append(response.getContent());
                    log.error(message.toString());
                    if (response.getCode() > HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                        return null;
                    } else {
                        throw new IntegrationException(errorFromTuron.toString());
                    }
                }
            } catch (JsonProcessingException e) {
                log.error("Turon aggregation convert to JSON error: {}", e.getMessage());
                throw new IntegrationException(e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    public TuronGetMarksResponseDTO retry(String extension, String orderId, String gtin, String blockId, String clientToken, String omsId) {
        CircuitBreaker cb = circuitBreakerFactory.create(getIntegrationType().name());
        return cb.run(() -> {
            String url = applicationProperties.getTuronConfig().getHost() + "api/v2/" + extension + "/codes/retry";
            try {
                NameValuePair[] params = new NameValuePair[4];
                params[0] = new BasicNameValuePair("omsId", omsId);
                params[1] = new BasicNameValuePair("orderId", orderId);
                params[2] = new BasicNameValuePair("gtin", gtin);
                params[3] = new BasicNameValuePair("blockId", blockId);

                CustomResponse response = customRequest.sendGetRequest(url, getClientHeaders(clientToken), Arrays.stream(params).toList());
                writeLog("Retry", url, response);

                if (HttpStatus.OK.value() == response.getCode()) {
                    return objectMapper.readValue(response.getContent(), TuronGetMarksResponseDTO.class);
                } else {
                    TuronErrorResponse errorFromTuron = objectMapper.readValue(response.getContent(), TuronErrorResponse.class);
                    throw new IntegrationException(errorFromTuron.toString());
                }
            } catch (Exception e) {
                throw new IntegrationException(e.getMessage());
            }
        }, throwable -> {
            log.error(throwable.getMessage(), throwable);
            throw new IntegrationException(throwable.getMessage());
        });
    }

    private void writeLog(String method, String api, CustomResponse response) {
        log.info("===================== ".concat(method.toUpperCase(Locale.ROOT)).concat(" ========================="));
        log.info("API:".concat(api));
        //log.info("RESPONSE: {}", response);
    }

    private Map<String, String> getClientHeaders(String clientToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("clientToken", clientToken);
        return headers;
    }
}
