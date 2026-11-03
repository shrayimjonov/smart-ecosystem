package uz.uzkassa.silen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.config.httpclient.CustomRequest;
import uz.uzkassa.silen.config.httpclient.CustomResponse;
import uz.uzkassa.silen.dto.eimzo.Pkcs7Dto;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Lazy
public class EimzoClient extends BaseIntegrationClient {
    public EimzoClient(CustomRequest customRequest,
                       ObjectMapper objectMapper,
                       @Value("${application.eimzo.url}") String startEndpoint) {
        super(customRequest, objectMapper, startEndpoint);
    }

    public Pkcs7Dto frontTimestamp(final String sign) {
        final String url = getBaseUrl() + "/frontend/timestamp/pkcs7";
        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), sign);
        return convertToResponse(response, Pkcs7Dto.class);
    }

    public Pkcs7Dto backendVerifyAttached(final String sign) {
        final String url = getBaseUrl() + "/backend/pkcs7/verify/attached";
        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), sign);
        return convertToResponse(response, Pkcs7Dto.class);
    }

    public Pkcs7Dto backendAuth(final String sign) {
        final String url = getBaseUrl() + "/backend/auth";
        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(null), sign);
        return convertToResponse(response, Pkcs7Dto.class);
    }

    @Override
    protected Map<String, String> evaluateHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Real-IP", "127.0.0.1");
        headers.put("Host", "https://cabinet.silen.uz");
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        return headers;
    }

    @Override
    protected String getBaseUrl() {
        return startEndpoint;
    }
}
