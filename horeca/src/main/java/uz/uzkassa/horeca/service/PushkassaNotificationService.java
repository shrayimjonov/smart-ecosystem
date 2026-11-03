package uz.uzkassa.horeca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.horeca.config.ApplicationProperties;
import uz.uzkassa.horeca.utils.HeaderUtils;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PushkassaNotificationService {
    Gson gson;
    ObjectMapper objectMapper;
    RestTemplate socketRestTemplate;
    ApplicationProperties applicationProperties;


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

            HttpEntity<PushMessageRequestTO> entity = new HttpEntity<>(to, HeaderUtils.getHeader());
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
