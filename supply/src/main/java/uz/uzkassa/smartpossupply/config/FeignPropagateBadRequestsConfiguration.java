package uz.uzkassa.smartpossupply.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class FeignPropagateBadRequestsConfiguration extends FeignConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            int status = response.status();
            Map<String, String> body = getResponseBodyAsString(response.body());
            HttpHeaders httpHeaders = new HttpHeaders();
            response.headers().forEach((k, v) -> httpHeaders.add("feign-" + k, StringUtils.join(v, ",")));
            return new ResponseStatusException(HttpStatus.valueOf(status), String.valueOf(body.get("detail")));
        };
    }

    private Map getResponseBodyAsString(final Response.Body body) {
        Map result = new HashMap();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = IOUtils.toString(body.asReader(StandardCharsets.UTF_8));
            result = mapper.readValue(jsonStr, Map.class);
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }
}
