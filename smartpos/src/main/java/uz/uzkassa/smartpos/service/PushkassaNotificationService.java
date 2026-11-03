package uz.uzkassa.smartpos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.smartpos.config.ApplicationProperties;

@Slf4j
@Service
public class PushkassaNotificationService {
    private final ObjectMapper objectMapper;
    private final RestTemplate socketRestTemplate;
    private final ApplicationProperties applicationProperties;

    public PushkassaNotificationService(ObjectMapper objectMapper, @Qualifier("socketRestTemplate") RestTemplate socketRestTemplate, ApplicationProperties applicationProperties) {
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
                throw new RuntimeException(e);
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
                    applicationProperties.getWebSocketUrl(),
                    entity,
                    String.class
            );
            log.info("Sent message to Public - /topic/session-{}-{}-{}", serviceTag, serviceKey, sessionId);
        }
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
