package uz.uzkassa.smartpossupply.interceptor;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import uz.uzkassa.smartpossupply.constants.LoggingConstants;

import java.io.IOException;

public final class RestTemplateCorrelationIdInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String traceId = MDC.get(LoggingConstants.TRACE_ID_MDC_KEY);
        HttpHeaders headers = request.getHeaders();
        if (traceId != null && !traceId.isBlank() && !headers.containsKey(LoggingConstants.TRACE_ID_HEADER)) {
            headers.set(LoggingConstants.TRACE_ID_HEADER, traceId);
        }
        return execution.execute(request, body);
    }
}
