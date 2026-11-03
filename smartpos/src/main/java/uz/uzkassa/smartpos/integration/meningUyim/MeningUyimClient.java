package uz.uzkassa.smartpos.integration.meningUyim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.config.httpclient.CustomRequest;
import uz.uzkassa.smartpos.config.httpclient.CustomResponse;
import uz.uzkassa.smartpos.exceptions.BadRequestException;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeningUyimClient {
    private final ApplicationProperties applicationProperties;
    private final CustomRequest customRequest;
    private final ObjectMapper objectMapper;

    public MeningUyimAccount findAccoount(final String subscriberCode) {
        try {
            final String url = applicationProperties.getMeningUyim().getHost() + "v2-find-account";
            final String json = objectMapper.writeValueAsString(MeningUyimRequest.builder().subscriberCode(subscriberCode).build());
            final CustomResponse customResponse = customRequest.sendPostRequest(url, getHeaders(), json);
            final MeningUyimResponse response = objectMapper.readValue(customResponse.getContent(), MeningUyimResponse.class);
            if (response.getCode() == 200) {
                return objectMapper.convertValue(response.getData(), MeningUyimAccount.class);
            } else {
                log.error("MeningUyim findAccoount response: {}", response);
                throw new BadRequestException(response.getMessage(), response.getCode());
            }
        } catch (final JsonProcessingException e) {
            log.error("MeningUyim convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public MeningUyimPayment prePayment(final String subscriberCode, final BigDecimal amount) {
        try {
            final String url = applicationProperties.getMeningUyim().getHost() + "v2-prepayment";
            final String json = objectMapper.writeValueAsString(MeningUyimRequest.builder()
                .subscriberCode(subscriberCode)
                .amount(amount)
                .build());
            CustomResponse customResponse = customRequest.sendPostRequest(url, getHeaders(), json);
            if (customResponse.getCode() == 200) {
                final MeningUyimResponse response = objectMapper.readValue(customResponse.getContent(), MeningUyimResponse.class);
                if (response.getCode() == 200) {
                    return objectMapper.convertValue(response.getData(), MeningUyimPayment.class);
                } else {
                    log.error("MeningUyim prePayment response: {}", response);
                    throw new BadRequestException(response.getMessage());
                }
            } else {
                log.error("MeningUyim prePayment response: {}", customResponse);
                throw new BadRequestException(customResponse.getContent());
            }
        } catch (final JsonProcessingException e) {
            log.error("MeningUyim convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public MeningUyimPayment confirmPayment(final MeningUyimRequest request) {
        try {
            final String url = applicationProperties.getMeningUyim().getHost() + "v2-confirmpayment";
            final String json = objectMapper.writeValueAsString(request);
            CustomResponse customResponse = customRequest.sendPostRequest(url, getHeaders(), json);
            if (customResponse.getCode() == 200) {
                final MeningUyimResponse response = objectMapper.readValue(customResponse.getContent(), MeningUyimResponse.class);
                if (response.getCode() == 200) {
                    return objectMapper.convertValue(response.getData(), MeningUyimPayment.class);
                } else {
                    log.error("MeningUyim confirmPayment response: {}", response);
                    throw new BadRequestException(response.getMessage());
                }
            } else {
                log.error("MeningUyim confirmPayment response: {}", customResponse);
                throw new BadRequestException(customResponse.getContent());
            }
        } catch (final JsonProcessingException e) {
            log.error("MeningUyim convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }


    public MeningUyimPayment checkPayment(final MeningUyimRequest request) {
        try {
            final String url = applicationProperties.getMeningUyim().getHost() + "v2-checkpayment";
            final String json = objectMapper.writeValueAsString(request);
            CustomResponse customResponse = customRequest.sendPostRequest(url, getHeaders(), json);
            if (customResponse.getCode() == 200) {
                final MeningUyimResponse response = objectMapper.readValue(customResponse.getContent(), MeningUyimResponse.class);
                if (response.getCode() == 200) {
                    return objectMapper.convertValue(response.getData(), MeningUyimPayment.class);
                } else {
                    log.error("MeningUyim confirmPayment response: {}", response);
                    throw new BadRequestException(response.getMessage());
                }
            } else {
                log.error("MeningUyim confirmPayment response: {}", customResponse);
                throw new BadRequestException(customResponse.getContent());
            }
        } catch (final JsonProcessingException e) {
            log.error("MeningUyim convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    private Map<String, String> getHeaders() {
        final Map<String, String> headers = new HashMap<>();
        final String credentials = Base64.getEncoder().encodeToString((applicationProperties.getMeningUyim().getUsername() + ":" + applicationProperties.getMeningUyim().getPassword()).getBytes());
        headers.put(HttpHeaders.AUTHORIZATION, "Basic " + credentials);
        headers.put("SecretKey", applicationProperties.getMeningUyim().getSecretKey());
        return headers;
    }
}
