package uz.uzkassa.smartpos.config.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

@Component
@Slf4j
public class CustomRequest {
    private final CloseableHttpClient httpClient;

    public CustomRequest(@Qualifier("defaultPooledHttpClient") CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CustomResponse sendGetRequest(final String url, final Map<String, String> headers, final NameValuePair... params) {
        if (url == null) {
            return null;
        }
        final CustomResponse response = new CustomResponse();
        try {
            final HttpGet getMethod = new HttpGet(url);
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(getMethod::addHeader);
            }
            if (params != null && params.length > 0) {
                java.net.URI uri = new URIBuilder(getMethod.getUri()).addParameters(Arrays.asList(params)).build();
                getMethod.setUri(uri);
            }
            return executeRequest(response, getMethod);
        } catch (final IOException e) {
            handleGeneralException(response, e);
        } catch (final URISyntaxException e) {
            log.error("URI syntax exception");
            log.error(e.getMessage(), e);
        }
        return response;
    }

    public CustomResponse sendPostRequest(final String url, final Map<String, String> headers, final String requestBody) {
        if (url == null) {
            return null;
        }
        final CustomResponse response = new CustomResponse();
        try {
            final HttpPost request = preparePost(url, headers);
            if (StringUtils.isNotBlank(requestBody)) {
                request.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
            }
            request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return executeRequest(response, request);
        } catch (SocketTimeoutException socketTimeoutException) {
            handleTimeoutException(response, socketTimeoutException);
        } catch (Exception e) {
            handleGeneralException(response, e);
        }
        return response;
    }

    public CustomResponse sendPutRequest(final String submitURL, final Map<String, String> headers, final String requestBody) {
        if (submitURL == null) {
            return null;
        }
        final CustomResponse response = new CustomResponse();
        try {
            final HttpPut request = preparePut(submitURL, headers);
            if (StringUtils.isNotBlank(requestBody)) {
                request.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
            }
            request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return executeRequest(response, request);
        } catch (final SocketTimeoutException socketTimeoutException) {
            handleTimeoutException(response, socketTimeoutException);
        } catch (final Exception e) {
            handleGeneralException(response, e);
        }
        return response;
    }

    public CustomResponse sendPlainPostRequest(final String url, final Map<String, String> headers, final String requestBody) {
        if (url == null) {
            return null;
        }
        final CustomResponse response = new CustomResponse();
        try {
            final HttpPost post = preparePost(url, headers);
            if (StringUtils.isNotBlank(requestBody)) {
                post.setEntity(new StringEntity(requestBody));
            }
            post.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            return executeRequest(response, post);
        } catch (final SocketTimeoutException socketTimeoutException) {
            handleTimeoutException(response, socketTimeoutException);
        } catch (final Exception e) {
            handleGeneralException(response, e);
        }
        return response;
    }

    private CustomResponse executeRequest(final CustomResponse response, final HttpUriRequestBase request)
        throws IOException {
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

    private static void handleGeneralException(final CustomResponse response, final Exception e) {
        log.error(e.getMessage(), e);
        response.setCode(HttpStatus.SC_BAD_REQUEST);
        response.setErrorMessage(e.getMessage());
    }

    private static void handleTimeoutException(final CustomResponse response, final SocketTimeoutException socketTimeoutException) {
        log.error(socketTimeoutException.getMessage(), socketTimeoutException);
        response.setCode(HttpStatus.SC_REQUEST_TIMEOUT);
        response.setErrorMessage(socketTimeoutException.getMessage());
    }

    private static HttpPost preparePost(final String url, final Map<String, String> headers) throws URISyntaxException {
        final HttpPost request = new HttpPost(url);
        final URIBuilder uriBuilder = new URIBuilder(request.getUri());
        request.setUri(uriBuilder.build());
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(request::addHeader);
        }
        return request;
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
}
