package uz.uzkassa.silen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.config.httpclient.CustomRequest;
import uz.uzkassa.silen.config.httpclient.CustomResponse;
import uz.uzkassa.silen.dto.acceptancetransferact.AcceptanceTransferActDTO;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.billing.dto.TokenResponseDto;
import uz.uzkassa.silen.integration.supplier.AccessToken;
import uz.uzkassa.silen.integration.supplier.CommittentTokenBatchDTO;
import uz.uzkassa.silen.integration.supplier.SupplierAuthPayload;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@Lazy
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierClient extends BaseIntegrationClient {

    String userName;
    String password;

    static LocalDateTime authenticateRefreshTime;
    static String tokenStore;

    public SupplierClient(CustomRequest customRequest, ObjectMapper objectMapper,
                          @Value("${application.supplier.url}") String startEndpoint,
                          @Value("${application.supplier.username}") String userName,
                          @Value("${application.supplier.password}") String password) {
        super(customRequest, objectMapper, startEndpoint);
        this.userName = userName;
        this.password = password;
    }

    public void sendAcceptanceTransferAct(AcceptanceTransferActDTO acceptanceTransferActDTO) {
        final String url = getBaseUrl() + "acceptance-transfer-acts/silen/webhook/created";
        customRequest.sendPostRequest(url, evaluateHeaders(null), convertBodyToJsonString(acceptanceTransferActDTO));
    }

    public Object getCustomerBranches(String customerTin) {
        final String url = getBaseUrl() + "branches/lookup?tin=" + customerTin;
        CustomResponse response = customRequest.sendGetRequest(url, evaluateHeaders(null));
        return convertToResponse(response, Object.class);
    }

    public void send(CommittentTokenBatchDTO committentTokenBatchDTO) {
        final String url = getBaseUrl() + "companies/committent-token";
        customRequest.sendPostRequest(url, evaluateHeaders(null), convertBodyToJsonString(committentTokenBatchDTO));
    }

    public AccessToken authorize() {
        final String url = getBaseUrl() + "account/login";
        final CustomResponse response = customRequest.sendPostRequest(url, null, convertBodyToJsonString(new SupplierAuthPayload(userName, password)));
        return convertToResponse(response, AccessToken.class);
    }

    @Override
    protected Map<String, String> evaluateHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        AccessToken newToken = authorize();
        String nullableToken = Optional.ofNullable(newToken.getAccess_token()).orElse("");
        headers.put("Authorization", "Bearer " + nullableToken);
        return headers;
    }

    @Override
    protected String getBaseUrl() {
        return startEndpoint;
    }

    public String getToken() {
        if (tokenStore != null && authenticateRefreshTime != null && authenticateRefreshTime.isAfter(LocalDateTime.now())) {
            return tokenStore;
        }

        try {
            AccessToken response = authorize();
            if (response.getAccess_token() != null) {
                tokenStore = Objects.requireNonNull(response.getAccess_token());
                authenticateRefreshTime = LocalDateTime.now().plusSeconds(2592000);
                return tokenStore;
            } else {
                tokenStore = null;
                throw new BadRequestException(response.toString());
            }
        } catch (Exception e) {
            tokenStore = null;
            throw new BadRequestException(e.getMessage());
        }
    }
}
