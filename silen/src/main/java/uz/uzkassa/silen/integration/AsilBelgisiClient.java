package uz.uzkassa.silen.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.config.httpclient.CustomRequest;
import uz.uzkassa.silen.config.httpclient.CustomResponse;
import uz.uzkassa.silen.enumeration.ProductGroup;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.exceptions.TueApiErrorResponse;
import uz.uzkassa.silen.integration.asilbelgi.dto.*;
import uz.uzkassa.silen.service.LocalizationService;

import java.io.IOException;
import java.util.*;

import static uz.uzkassa.silen.exceptions.ErrorConstants.TURON_AGGREGATION_NOT_FOUND;

@Component
@Slf4j
@Lazy
public class AsilBelgisiClient extends BaseIntegrationClient {
    /**
     * limit per request for checking by cises/info
     */
    public static int cisInfoLimit = 1000;
    private final String trueApiReport;
    private final LocalizationService localizationService;

    public AsilBelgisiClient(CustomRequest customRequest, ObjectMapper objectMapper, @Value("${application.trueApiUrl}") String startEndpoint, @Value("${application.trueApiReport}") String trueApiReport, LocalizationService localizationService) {
        super(customRequest, objectMapper, startEndpoint);
        this.trueApiReport = trueApiReport;
        this.localizationService = localizationService;
    }

    @Override
    public <T> T convertToResponse(final CustomResponse customResponse, final Class<T> toDto) {
        switch (HttpStatus.valueOf(customResponse.getCode())) {
            case HttpStatus.OK -> {
                try {
                    return objectMapper.readValue(customResponse.getContent(), toDto);
                } catch (final IOException e) {
                    log.error("Billing response convert to JSON error: {}", e.getMessage());
                }
            }
            case BAD_GATEWAY, UNAUTHORIZED ->
                    throw new BadRequestException(localizationService.getMessage("true.api.token.expired"));
            default -> {
                try {
                    TueApiErrorResponse tueApiErrorResponse = objectMapper.readValue(customResponse.getContent(), TueApiErrorResponse.class);
                    final String errorMessage = Optional.ofNullable(tueApiErrorResponse).map(TueApiErrorResponse::getErrorMessage).orElse(customResponse.getErrorMessage());
                    throw new BadRequestException(errorMessage, TURON_AGGREGATION_NOT_FOUND);
                } catch (JsonProcessingException e) {
                    log.error("Error occured on convert response to error", e);
//                    throw new BadRequestException(e.getMessage());
                }
            }
        }
        return null;
    }

    public AuthKey getAuthKey() {
        final String url = getBaseUrl() + "auth/key";
        CustomResponse response = customRequest.sendGetRequest(url, null);
        return convertToResponse(response, AuthKey.class);
    }

    public String signIn(AuthKey key) {
        final String url = getBaseUrl() + "auth/simpleSignIn";
        CustomResponse response = customRequest.sendPostRequest(url, null, convertBodyToJsonString(key));
        AuthToken data = convertToResponse(response, AuthToken.class);
        return data.getToken();
    }

    public String refresh(String token) {
        final String url = getBaseUrl() + "auth/refresh";
        CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(token), null);
        AuthToken data = convertToResponse(response, AuthToken.class);
        return data.getToken();
    }

    /**
     * Массив кодов маркировки. Не более 1000 КМ в массиве
     *
     * @param token
     * @param codes
     * @return
     */
    public CisInfoResponse[] cisesInfo(final String token, final Collection<String> codes) {
        final String url = getBaseUrl() + "cises/info";
        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(token), convertBodyToJsonString(codes));
        return convertToResponse(response, CisInfoResponse[].class);
    }

    public Map<String, Map<String, List>> aggregatedList(String token, ProductGroup productGroup, Set<String> codes) {
        final String url = getBaseUrl() + "cises/aggregated/list?pg=" + productGroup.getProductGroup();
        final CustomResponse response = customRequest.sendPostRequest(url, evaluateHeaders(token), convertBodyToJsonString(codes));
        return convertToResponse(response, Map.class);
    }

    public ReportInfo getReportInfo(String token, String aggregationId) {
        final String url = trueApiReport + aggregationId;
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(token));
        return convertToResponse(response, ReportInfo.class);
    }

    public Object getCisesHistory(String token, String code) {
        final String url = getBaseUrl() + "cises/history?code=" + code;
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(token));
        return convertToResponse(response, Object.class);
    }

    public Object getProductGroupBalance(String token, int code) {
        final String url = getBaseUrl() + "elk/product-groups/balance?productGroupId=" + code;
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(token));
        return convertToResponse(response, Object.class);
    }

    public DocList getDocList(String token, String dateFrom) {
        final String url = getBaseUrl() + "doc/listV2";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("documentFormat", "MANUAL"));
        params.add(new BasicNameValuePair("documentType", "UZ_ESF"));
        params.add(new BasicNameValuePair("limit", "10000"));
        params.add(new BasicNameValuePair("dateFrom", dateFrom));
        params.add(new BasicNameValuePair("inputFormat", "false"));
       final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(token), params);
        return convertToResponse(response, DocList.class);
    }

    public Object getDocInfo(String token, String docId) {
        final String url = getBaseUrl() + "documents/" + docId + "/info";
        final CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(token));
        return convertToResponse(response, Object.class);
    }

    @Override
    protected Map<String, String> evaluateHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }

    @Override
    protected String getBaseUrl() {
        return startEndpoint;
    }
}
