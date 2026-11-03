package uz.uzkassa.silen.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.silen.config.ApplicationProperties;
import uz.uzkassa.silen.dto.partyaggregation.AggregationStatusChangedPayload;

@Slf4j
@Service
public class PushNotificationService {
    private final Gson gson;
    private final ObjectMapper objectMapper;
    private final RestTemplate socketRestTemplate;
    private final ApplicationProperties applicationProperties;

    public PushNotificationService(Gson gson, ObjectMapper objectMapper, @Qualifier("socketRestTemplate") RestTemplate socketRestTemplate, ApplicationProperties applicationProperties) {
        this.gson = gson;
        this.objectMapper = objectMapper;
        this.socketRestTemplate = socketRestTemplate;
        this.applicationProperties = applicationProperties;
    }

    public <T> void sendNotification(T payload, String serviceTag, String serviceKey, String sessionId) {
        if (payload != null) {
            String message;
            try {
                message = objectMapper.writeValueAsString(payload);
            } catch (JsonProcessingException e) {
                message = gson.toJson(payload);
            }
            log.info("Socket {} message JSON: {}", payload.getClass().getSimpleName(), message);
            final PushMessageRequestTO to = PushMessageRequestTO.builder()
                    .message(message)
                    .serviceTag(serviceTag)
                    .serviceKey(serviceKey)
                    .type("toSession")
                    .sessionId(sessionId)
                    .build();

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            final HttpEntity<PushMessageRequestTO> entity = new HttpEntity<>(to, headers);
            socketRestTemplate.postForEntity(
                    applicationProperties.getPushUrl(),
                    entity,
                    String.class
            );
            log.info("Sent message to Session - /topic/session-{}-{}-{}", serviceTag, serviceKey, sessionId);
        }
    }

    public void sendPartyAggregation(AggregationStatusChangedPayload payload, String sessionId) {
        sendNotification(payload, "silen", "party", sessionId);
    }

    @Data
    @Builder
    public static class PushMessageRequestTO {
        private String message;
        private String serviceKey;
        private String serviceTag;
        private String type;
        private String sessionId;
    }
}
