package uz.uzkassa.smartpossupply.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.config.httpclient.CustomRequest;
import uz.uzkassa.smartpossupply.config.httpclient.CustomResponse;
import uz.uzkassa.smartpossupply.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;
import uz.uzkassa.smartpossupply.web.rest.errors.NotFoundException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Lazy
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EimzoClient {
    ApplicationProperties applicationProperties;
    CustomRequest customRequest;
    ObjectMapper objectMapper;

    public Pkcs7Dto frontTimestamp(String sign) {
        String url = applicationProperties.getEimzoConfig().getHost().concat("/frontend/timestamp/pkcs7");
        CustomResponse response = customRequest.sendPostRequest(url, getHeaders(), sign);
        return convertToResponse(response, Pkcs7Dto.class);
    }

    public Pkcs7Dto backendVerifyAttached(String sign) {
        String url = applicationProperties.getEimzoConfig().getHost().concat("/backend/pkcs7/verify/attached");
        CustomResponse response = customRequest.sendPostRequest(url, getHeaders(), sign);
        return convertToResponse(response, Pkcs7Dto.class);
    }

    public String backendVerifyAttachedString(String sign) {
        String url = applicationProperties.getEimzoConfig().getHost().concat("/backend/pkcs7/verify/attached");
        CustomResponse response = customRequest.sendPostRequest(url, getHeaders(), sign);
        log.info("Response: {}", response.toString());
        return response.getContent();
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Real-IP", "127.0.0.1");
        headers.put("Host", "https://internal.example.com");
        return headers;
    }


    private <T> T convertToResponse(CustomResponse customResponse, Class<T> toDto) {
        final String errorMessage = StringUtils.isNotEmpty(customResponse.getErrorMessage()) ? customResponse.getErrorMessage() : customResponse.getContent();
        switch (HttpStatus.valueOf(customResponse.getCode())) {
            case OK -> {
                try {
                    return objectMapper.readValue(customResponse.getContent(), toDto);
                } catch (IOException e) {
                    log.debug("BaseIntegrationClient response convert to JSON error: {}", e.getMessage());
                }
            }
            case CREATED -> {
                if (customResponse.getContent() == null) return null;
                return objectMapper.convertValue(customResponse.getContent(), toDto);
            }
            case NOT_FOUND -> throw new NotFoundException(errorMessage);
            case BAD_REQUEST, INTERNAL_SERVER_ERROR -> throw new BadRequestException(errorMessage);
            default -> log.debug("Response convert error: {}", customResponse);
        }
        return null;
    }
}
