package uz.uzkassa.smartpos.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Slf4j
@Component
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        logRequest(httpRequest, bytes);
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        logResponse(response);

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("===========================Request begin================================================");
        log.info("Date        : {}", LocalDateTime.now());
        log.info("URI         : {}", request.getURI());
        log.info("Method      : {}", request.getMethod());
        log.info("Headers     : {}", request.getHeaders());
        log.info("Request body: {}", new String(body, Charset.defaultCharset()));
        log.info("==========================Request end================================================");
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.info("============================Response begin==========================================");
        log.info("Date         : {}", LocalDateTime.now());
        log.info("Status code  : {}", response.getStatusCode());
        log.info("Headers      : {}", response.getHeaders());
        log.info("=======================Response end=================================================");
    }
}
