package uz.uzkassa.silen.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.config.httpclient.CustomRequest;
import uz.uzkassa.silen.config.httpclient.CustomResponse;
import uz.uzkassa.silen.dto.invoice.MxikListDTO;
import uz.uzkassa.silen.dto.invoice.MxikPackageResponseDTO;
import uz.uzkassa.silen.dto.invoice.MxikResponseDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Lazy
public class TasnifClient extends BaseIntegrationClient {

    public TasnifClient(final CustomRequest customRequest,
                        final ObjectMapper objectMapper,
                        @Value("${application.tasnifApiUrl}") final String startEndpoint) {
        super(customRequest, objectMapper, startEndpoint);
    }

    @Override
    protected String getBaseUrl() {
        return startEndpoint;
    }

    public MxikListDTO syncPackages(final Collection<String> mxikCodes) {
        final String url = getBaseUrl() + "/integration-mxik/check";
        final String requestBody = convertBodyToJsonString(mxikCodes);
        final CustomResponse response = customRequest.sendPostRequest(url, null, requestBody);
        return convertToResponse(response, MxikListDTO.class);
    }

    public MxikResponseDTO getCompanyCatalog(final String tin, final String search) {
        final String url = getBaseUrl() + "/integration-mxik/company/get/basket-products";

        final List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("lang", "ru"));
        params.add(new BasicNameValuePair("page", "0"));
        params.add(new BasicNameValuePair("size", "1000"));
        if (tin.length() == 9) {
            params.add(new BasicNameValuePair("tin", tin));
        } else {
            params.add(new BasicNameValuePair("pinfl", tin));
        }

        if (StringUtils.isNotEmpty(search)) {
            params.add(new BasicNameValuePair("text", search.toLowerCase()));
        }

        final CustomResponse response = customRequest.sendGetRequest(url, null, params);
        return convertToResponse(response, MxikResponseDTO.class);
    }

    public MxikPackageResponseDTO getPackages(final String mxik) {
        final String url = getBaseUrl() + "/integration-mxik/get/package?mxikCode=" + mxik;

        final CustomResponse response = customRequest.sendGetRequest(url, null);
        return convertToResponse(response, MxikPackageResponseDTO.class);
    }

    @Override
    protected Map<String, String> evaluateHeaders(String token) {
        return null;
    }
}
