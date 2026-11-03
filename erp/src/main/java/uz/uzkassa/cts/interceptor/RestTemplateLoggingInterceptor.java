package uz.uzkassa.cts.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

@Slf4j
@Component
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        logRequest(httpRequest, bytes);
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        logResponse(response, httpRequest.getURI());

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        String builder = System.lineSeparator() +
                "===========================Request begin================================================" +
                System.lineSeparator() +
                "Date        : " + LocalDateTime.now() +
                System.lineSeparator() +
                "URI         : " + request.getURI() +
                System.lineSeparator() +
                "==========================Request end================================================";
        log.info(builder);
    }

    private void logResponse(ClientHttpResponse response, URI uri) throws IOException {
        String builder = System.lineSeparator() +
                "============================Response begin==========================================" +
                System.lineSeparator() +
                "Date         : " + LocalDateTime.now() +
                System.lineSeparator() +
                "Status code  : " + response.getStatusCode() +
                System.lineSeparator() +
                "URI         : " + uri +
                System.lineSeparator() +
                "=======================Response end=================================================";
        log.info(builder);
    }
}
