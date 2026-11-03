package uz.uzkassa.silen.config.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CustomRequest {
    private final CloseableHttpClient httpClient;

    public CustomRequest(@Qualifier("defaultPooledHttpClient") final CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private static CustomResponse handleTimeoutException(final SocketTimeoutException socketTimeoutException) {
        log.error(socketTimeoutException.getMessage(), socketTimeoutException);
        CustomResponse response = new CustomResponse();
        response.setCode(HttpStatus.SC_REQUEST_TIMEOUT);
        response.setErrorMessage(socketTimeoutException.getMessage());
        return response;
    }

    private static HttpPost preparePost(final String url, final Map<String, String> headers, List<NameValuePair> params) throws URISyntaxException {
        final HttpPost request = new HttpPost(url);
        final URIBuilder uriBuilder = new URIBuilder(request.getUri());
        if (CollectionUtils.isNotEmpty(params)) {
            uriBuilder.addParameters(params);
        }
        request.setUri(uriBuilder.build());
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(request::addHeader);
        }
        return request;
    }

    private static CustomResponse handleGeneralException(final Exception e) {
        log.error(e.getMessage(), e);
        CustomResponse response = new CustomResponse();
        response.setCode(HttpStatus.SC_BAD_REQUEST);
        response.setErrorMessage(e.getMessage());
        return response;
    }

    private static HttpPut preparePut(final String url, final Map<String, String> headers) throws URISyntaxException {
        final HttpPut request = new HttpPut(url);
        final URIBuilder uriBuilder = new URIBuilder(request.getUri());
        request.setUri(uriBuilder.build());
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(request::addHeader);
        }
        return request;
    }

    public CustomResponse sendPostRequest(final String url, final Map<String, String> headers, final String requestBody) {
        if (url == null) {
            return null;
        }
        try {
            final HttpPost request = preparePost(url, headers, null);
            if (StringUtils.isNotBlank(requestBody)) {
                request.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
            }
            request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return executeRequest(request);
        } catch (SocketTimeoutException socketTimeoutException) {
            return handleTimeoutException(socketTimeoutException);
        } catch (Exception e) {
            return handleGeneralException(e);
        }
    }

    public CustomResponse sendGetRequest(final String url, final Map<String, String> headers) {
        return sendGetRequest(url, headers, null);
    }

    public CustomResponse sendPutRequest(final String submitURL, final Map<String, String> headers, final String requestBody) {
        if (submitURL == null) {
            return null;
        }
        try {
            final HttpPut request = preparePut(submitURL, headers);
            if (StringUtils.isNotBlank(requestBody)) {
                request.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
            }
            request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return executeRequest(request);
        } catch (final SocketTimeoutException socketTimeoutException) {
            return handleTimeoutException(socketTimeoutException);
        } catch (final Exception e) {
            return handleGeneralException(e);
        }
    }

    public CustomResponse sendGetRequest(final String url, final Map<String, String> headers, final List<NameValuePair> params) {
        if (url == null) {
            return null;
        }
        try {
            final HttpGet getMethod = new HttpGet(url);
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(getMethod::addHeader);
            }
            if (CollectionUtils.isNotEmpty(params)) {
                java.net.URI uri = new URIBuilder(getMethod.getUri()).addParameters(params).build();
                getMethod.setUri(uri);
            }
            return executeRequest(getMethod);
        } catch (final IOException e) {
            return handleGeneralException(e);
        } catch (final URISyntaxException e) {
            log.error("URI syntax exception");
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private CustomResponse executeRequest(final HttpUriRequestBase request) throws IOException {
        final CustomResponse response = new CustomResponse();
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(request);
            if (httpResponse != null && httpResponse.getEntity() != null) {
                response.setCode(httpResponse.getCode());
                try {
                    String responseContent = EntityUtils.toString(httpResponse.getEntity());
                    response.setContent(responseContent);
                } catch (ParseException ignored) {
                }
            }
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (final IOException e) {
                    log.error(e.getMessage());
                }
            }
            request.reset();
        }
        return response;
    }

    public CustomResponse sendPostRequest(final String url, final List<NameValuePair> params, final Map<String, String> headers, final String requestBody) {
        if (url == null) {
            return null;
        }
        try {
            final HttpPost request = preparePost(url, headers, params);
            if (StringUtils.isNotBlank(requestBody)) {
                request.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
            }
            request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return executeRequest(request);
        } catch (SocketTimeoutException socketTimeoutException) {
            return handleTimeoutException(socketTimeoutException);
        } catch (Exception e) {
            return handleGeneralException(e);
        }
    }

    public CustomResponse uploadFile(final String url, final Map<String, String> headers, final MultipartFile file) {
        if (url == null) {
            return null;
        }
        try {
            final HttpPost request = new HttpPost(url);

            if (headers != null && !headers.isEmpty()) {
                headers.forEach(request::addHeader);
            }
            HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("file", file.getBytes(), ContentType.APPLICATION_OCTET_STREAM, file.getOriginalFilename()).build();
            request.setEntity(entity);

            return executeRequest(request);
        } catch (SocketTimeoutException socketTimeoutException) {
            return handleTimeoutException(socketTimeoutException);
        } catch (Exception e) {
            return handleGeneralException(e);
        }
    }


}
