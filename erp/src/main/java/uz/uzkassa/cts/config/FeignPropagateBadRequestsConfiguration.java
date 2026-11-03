package uz.uzkassa.cts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import uz.uzkassa.cts.web.rest.errors.AppGlobalException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class FeignPropagateBadRequestsConfiguration extends FeignConfiguration {
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            int status = response.status();
            Map<String, Object> body = getResponseBodyAsString(response.body());
            return new AppGlobalException(String.valueOf(body.get("detail")), String.valueOf(body.get("detail")), HttpStatus.valueOf(status));
        };
    }

    private Map<String, Object> getResponseBodyAsString(final Response.Body body) {
        Map<String, Object> result = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = IOUtils.toString(body.asReader(StandardCharsets.UTF_8));
            System.out.println(jsonStr);
            result = mapper.readValue(jsonStr, Map.class);
        } catch (final IOException e) {
            System.out.println("ERROR: exception on parsing feign response");
        }
        return result;
    }
}
