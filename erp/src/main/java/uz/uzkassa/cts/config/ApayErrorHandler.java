package uz.uzkassa.cts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import uz.uzkassa.cts.validator.LocalizationMessage;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApayErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper;
    private final LocalizationMessage localizationMessage;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return !response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String apayError = new String(response.getBody().readAllBytes());
        log.error("APAY ERROR : {}", apayError);
        Map<String, Object> body = new HashMap<>();
        try {
            body = objectMapper.readValue(apayError, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        String title = body.containsKey("title") ? (String) body.get("title") : null;
        if (response.getStatusCode().is4xxClientError()) {
            if (body.containsKey("code")) {
                if (body.get("code").equals(81)) {
                    throw new BadRequestException(localizationMessage.localize("payment.error"),
                            localizationMessage.localize("payment.error.solution"));
                }
            }
            throw new BadRequestException(title != null ? title : localizationMessage.localize("bad.request"));
        } else {
            throw new RuntimeException(apayError);
        }
    }
}
