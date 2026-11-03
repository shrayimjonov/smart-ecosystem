package uz.uzkassa.smartpossupply.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


@Slf4j
@Component
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        logRequest(request, new String(body, StandardCharsets.UTF_8));
        ClientHttpResponse response;
        try {
            response = execution.execute(request, body);
            logResponse(response);
        } catch (Exception e) {
            log.error("Request failed. Message: {}", e.getMessage());
            log.info("\nREQUEST CURL: {}", generateCurl(request, body));
            throw e;
        }

        return response;
    }

    private void logRequest(HttpRequest request, String body) {
        log.info("Date        : {}", LocalDateTime.now());
        log.info("URI         : {}", request.getURI());
        log.info("Method      : {}", request.getMethod());
        //log.info("Header      : {}", request.getHeaders());
        //log.info("Body      : {}", body);
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.info("ResponseDate  : {}", LocalDateTime.now());
        log.info("Status code   : {}", response.getStatusCode());
        //log.info("Response body   : {}", response.getBody());
    }

    private String generateCurl(HttpRequest request, byte[] body) {
        StringBuilder curl = new StringBuilder("curl -X " + request.getMethod());
        curl.append(" '").append(request.getURI()).append("'");

        request.getHeaders().forEach((key, value) ->
                curl.append(" -H '").append(key).append(": ").append(String.join(",", value)).append("'"));

        if (body != null && body.length > 0) {
            String bodyString = new String(body, StandardCharsets.UTF_8);
            curl.append(" -d '").append(bodyString.replace("'", "'\\''")).append("'");
        }
        log.info("cURL Command: {}", curl);
        return curl.toString();
    }
}