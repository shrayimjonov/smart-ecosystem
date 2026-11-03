package uz.uzkassa.smartpos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.service.LocalizationService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApayErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper;
    private final LocalizationService localizationService;

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
                    throw new BadRequestException(localizationService.getMessage("payment.error"),
                            400,
                            localizationService.getMessage("payment.error.solution"));
                }
            }
            throw new BadRequestException(title != null ? title : localizationService.getMessage("bad.request"));
        } else {
            throw new RuntimeException(apayError);
        }
    }
}