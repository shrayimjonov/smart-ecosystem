package uz.uzkassa.silen.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.config.httpclient.CustomRequest;
import uz.uzkassa.silen.config.httpclient.CustomResponse;
import uz.uzkassa.silen.dto.marking.TuronOrderDTO;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.EntityNotFoundException;
import uz.uzkassa.silen.exceptions.TuronErrorResponse;
import uz.uzkassa.silen.integration.turon.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Lazy
public class TuronClient extends BaseIntegrationClient {
    public static int utilisationLimit = 30_000;
    public static int aggregationLimit = 11_000;

    public TuronClient(CustomRequest customRequest, ObjectMapper objectMapper, @Value("${application.turonApiUrl}") String startEndpoint) {
        super(customRequest, objectMapper, startEndpoint);
    }

    public Object ping(String extension, String clientToken, String omsId) {
        final String url = getBaseUrl() + extension + "/ping";
        List<NameValuePair> params = List.of(new BasicNameValuePair("omsId", omsId));
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(clientToken), params);
        return convertToResponse(response, Object.class);
    }

    public CreateOrderResponse createOrder(TuronOrderDTO turonOrderDTO, String extension, String clientToken, String omsId) {
        final String url = getBaseUrl() + extension + "/orders";
        List<NameValuePair> params = List.of(new BasicNameValuePair("omsId", omsId));
        final CustomResponse response = customRequest.sendPostRequest(url, params, evaluateHeaders(clientToken), convertBodyToJsonString(turonOrderDTO));
        return convertToResponse(response, CreateOrderResponse.class);
    }

    public CheckOrderStatusResponse checkStatus(String extension, String orderId, String gtin, String clientToken, String omsId) {
        String url = getBaseUrl() + extension + "/buffer/status";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("omsId", omsId));
        params.add(new BasicNameValuePair("orderId", orderId));
        params.add(new BasicNameValuePair("gtin", gtin));
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(clientToken), params);
        return convertToResponse(response, CheckOrderStatusResponse.class);
    }

    public GetMarksResponse getMarks(String extension, String orderId, String lastBlockId, String gtin, Integer qty, String clientToken, String omsId) {
        String url = getBaseUrl() + extension + "/codes";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("omsId", omsId));
        params.add(new BasicNameValuePair("orderId", orderId));
        params.add(new BasicNameValuePair("gtin", gtin));
        params.add(new BasicNameValuePair("quantity", String.valueOf(qty)));
        params.add(new BasicNameValuePair("lastBlockId", lastBlockId));
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(clientToken), params);
        return convertToResponse(response, GetMarksResponse.class);
    }

    public GetMarksResponse retry(String extension, String orderId, String gtin, String blockId, String clientToken, String omsId) {
        final String url = getBaseUrl() + extension + "/codes/retry";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("omsId", omsId));
        params.add(new BasicNameValuePair("orderId", orderId));
        params.add(new BasicNameValuePair("gtin", gtin));
        if (StringUtils.isNotBlank(blockId)) {
            params.add(new BasicNameValuePair("blockId", blockId));
        }
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(clientToken), params);
        return convertToResponse(response, GetMarksResponse.class);
    }

    public Object closeBuffer(String extension, String orderId, String gtin, String blockId, String clientToken, String omsId) {
        final String url = getBaseUrl() + extension + "/buffer/close";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("omsId", omsId));
        params.add(new BasicNameValuePair("orderId", orderId));
        params.add(new BasicNameValuePair("gtin", gtin));
        params.add(new BasicNameValuePair("lastBlockId", blockId));
        final CustomResponse response = customRequest.sendPostRequest(url, params, evaluateHeaders(clientToken), null);
        return convertToResponse(response, Object.class);
    }

    public CreateAggregationResponse createAggregation(CreateAggregationRequest request, String extension, String clientToken, String omsId) {
        String url = getBaseUrl() + extension + "/aggregation?omsId=" + omsId;
        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(clientToken), convertBodyToJsonString(request));
        return convertToResponse(response, CreateAggregationResponse.class);
    }

    public DropoutResponse dropout(DropoutRequest dropoutRequest, String extension, String clientToken, String omsId) {
        final String url = getBaseUrl() + extension + "/dropout";
        List<NameValuePair> params = List.of(new BasicNameValuePair("omsId", omsId));
        final CustomResponse response = customRequest.sendPostRequest(url, params, evaluateHeaders(clientToken), convertBodyToJsonString(dropoutRequest));
        return convertToResponse(response, DropoutResponse.class);
    }

    /**
     * Примечание. Передаваемые коды маркировки в качестве параметров ‹sntins› должны включать полный код маркировки.
     * Количество КМ в отчёте об использовании не должно превышать 30 000 кодов.
     * @param clientToken
     * @param extension
     * @param omsId
     * @param request
     * @return
     */
    public UtilisationResponse apply(String clientToken, String extension, String omsId, UtilisationRequest request) {
        final String url = getBaseUrl() + extension + "/utilisation";

        List<NameValuePair> params = List.of(new BasicNameValuePair("omsId", omsId));
        final CustomResponse response = customRequest.sendPostRequest(url, params, evaluateHeaders(clientToken), convertBodyToJsonString(request));
        return convertToResponse(response, UtilisationResponse.class);
    }

    public ReportInfoResponse reportInfo(String clientToken, String extension, String omsId, String reportId) {
        final String url = getBaseUrl() + extension + "/report/info";

        List<NameValuePair> params = List.of(
                new BasicNameValuePair("omsId", omsId),
                new BasicNameValuePair("reportId", reportId));
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(clientToken), params);
        return convertToResponse(response, ReportInfoResponse.class);
    }

    @Override
    protected Map<String, String> evaluateHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("clientToken", token);
        return headers;
    }

    @Override
    protected String getBaseUrl() {
        return startEndpoint;
    }

    public <T> T convertToResponse(final CustomResponse customResponse, final Class<T> toDto) {
        final String errorMessage = StringUtils.isNotEmpty(customResponse.getErrorMessage()) ? customResponse.getErrorMessage() : customResponse.getContent();
        switch (HttpStatus.valueOf(customResponse.getCode())) {
            case HttpStatus.OK -> {
                try {
                    return objectMapper.readValue(customResponse.getContent(), toDto);
                } catch (final IOException e) {
                    log.error("BaseIntegrationClient response convert to JSON: {}", customResponse);
                }
            }
            case HttpStatus.CREATED -> {
                if (customResponse.getContent() == null) return null;
                return objectMapper.convertValue(customResponse.getContent(), toDto);
            }
            case NOT_FOUND -> throw new EntityNotFoundException(errorMessage);
            case BAD_REQUEST, INTERNAL_SERVER_ERROR ->
                    throw new BadRequestException(parseTuronException(customResponse));
            case SERVICE_UNAVAILABLE -> throw new BadRequestException("Сервис 'Турон' временно недоступен", 503);
            default -> {
                log.error("GET response convert by TIN response: {}", customResponse);
                throw new BadRequestException(parseTuronException(customResponse));
            }
        }
        return null;
    }

    private String parseTuronException(final CustomResponse response) {
        if (StringUtils.isBlank(response.getContent())) {
            return response.getErrorMessage();
        }
        try {
            final TuronErrorResponse error = objectMapper.readValue(response.getContent(), TuronErrorResponse.class);
            return error.toString();
        } catch (JsonProcessingException e) {
            return "Error parse failed";
        }
    }
}
