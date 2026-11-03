package uz.uzkassa.smartpos.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import uz.uzkassa.smartpos.config.httpclient.HttpClientBeanConfig;
import uz.uzkassa.smartpos.exceptions.BadRequestException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Import(HttpClientBeanConfig.class)
@Configuration(proxyBeanMethods = false)
public class FeignPropagateBadRequestsConfiguration extends FeignConfiguration {
    private final ObjectMapper objectMapper;

    @Bean
    public HttpResponseInterceptor httpResponseInterceptor() {
        return (httpResponse, entityDetails, httpContext) -> httpResponse.removeHeaders(HttpHeaders.TRANSFER_ENCODING);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            Map<String, Object> body = getResponseBodyAsString(response.body(), objectMapper);
            String title = body.containsKey("title") ? (String) body.get("title") : null;
            String detail = body.containsKey("detail") ? (String) body.get("detail") : null;
            String solution = body.containsKey("solution") ? (String) body.get("solution") : null;
            if (solution != null) {
                return new BadRequestException(Optional.ofNullable(title).orElse(detail), 400, solution);
            } else {
                return new BadRequestException(Optional.ofNullable(title).orElse(detail), 400);
            }
        };
    }

    private Map<String, Object> getResponseBodyAsString(final Response.Body body, final ObjectMapper objectMapper) {
        Map<String, Object> result = new HashMap<>();
        try {
            String jsonStr = IOUtils.toString(body.asReader(StandardCharsets.UTF_8));
            log.warn(jsonStr);
            result = objectMapper.readValue(jsonStr, new TypeReference<>() {
            });
        } catch (final IOException e) {
            System.out.println("ERROR: exception on parsing feign response");
        }
        return result;
    }

}
