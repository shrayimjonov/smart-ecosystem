package uz.uzkassa.smartpos.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.config.ApplicationProperties;
import uz.uzkassa.smartpos.config.httpclient.CustomRequest;
import uz.uzkassa.smartpos.config.httpclient.CustomResponse;
import uz.uzkassa.smartpos.dto.eimzo.Pkcs7Dto;
import uz.uzkassa.smartpos.exceptions.BadRequestException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Lazy
@RequiredArgsConstructor
public class EimzoClient {
    private final ApplicationProperties applicationProperties;
    private final CustomRequest customRequest;
    private final ObjectMapper objectMapper;

    public Pkcs7Dto frontTimestamp(final String sign) {
        try {
            final String url = applicationProperties.getEImzo().getHost() + "/frontend/timestamp/pkcs7";
            CustomResponse response = customRequest.sendPlainPostRequest(url, getHeaders(), sign);
            if (response.getCode() == 200) {
//                log.info("Front timestamp response: \n {}", response.getContent());
                return objectMapper.readValue(response.getContent(), Pkcs7Dto.class);
            } else {
                log.error("Front timestamp sign: \n {}", sign);
                log.error("Front timestamp response: {}", response);
                throw new BadRequestException(StringUtils.isNotEmpty(response.getErrorMessage()) ? response.getErrorMessage() : response.getContent());
            }
        } catch (IOException e) {
            log.error("Front timestamp convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public Pkcs7Dto backendVerifyAttached(final String sign) {
        try {
            final String url = applicationProperties.getEImzo().getHost() + "/backend/pkcs7/verify/attached";
            CustomResponse response = customRequest.sendPlainPostRequest(url, getHeaders(), sign);
            if (response.getCode() == 200) {
                return objectMapper.readValue(response.getContent(), Pkcs7Dto.class);
            } else {
                log.error("Backend verify attached sign: \n {}", sign);
                log.error("Backend verify attached response: {}", response);
                throw new BadRequestException(StringUtils.isNotEmpty(response.getErrorMessage()) ? response.getErrorMessage() : response.getContent());
            }
        } catch (IOException e) {
            log.error("Backend verify attached convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    public Pkcs7Dto backendAuth(final String sign) {
        try {
            final String url = applicationProperties.getEImzo().getHost() + "/backend/auth";
            CustomResponse response = customRequest.sendPlainPostRequest(url, getHeaders(), sign);
            if (response.getCode() == 200) {
                return objectMapper.readValue(response.getContent(), Pkcs7Dto.class);
            } else {
                log.error("Backend auth sign: \n {}", sign);
                log.error("Backend auth response: {}", response);
                throw new BadRequestException(StringUtils.isNotEmpty(response.getErrorMessage()) ? response.getErrorMessage() : response.getContent());
            }
        } catch (IOException e) {
            log.error("Backend auth convert to JSON error: {}", e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Real-IP", "127.0.0.1");
        headers.put("Host", "https://internal.example.com");
        return headers;
    }
}
