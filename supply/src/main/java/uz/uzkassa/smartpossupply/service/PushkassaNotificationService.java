package uz.uzkassa.smartpossupply.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.smartpossupply.dto.notification.PushNotificationDTO;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PushkassaNotificationService extends BaseService {

    @Qualifier("webSocketRestTemplate")
    private final RestTemplate webSocketRestTemplate;

    private final Gson gson;

    public PushkassaNotificationService(RestTemplate webSocketRestTemplate, Gson gson) {
        this.webSocketRestTemplate = webSocketRestTemplate;
        this.gson = gson;
    }

    public String sendExportReport(PushNotificationDTO notificationDTO) {
        return this.sendMessageToSession(notificationDTO, "supply", "exportReport", SecurityUtils.getCurrentUserId().toString());
    }

    public String sendMessageToSession(PushNotificationDTO notificationDTO, String serviceTag, String serviceKey, String sessionId) {
        try {
            String message;
            try {
                message = objectMapper.writeValueAsString(notificationDTO);
            } catch (JsonProcessingException e) {
                message = gson.toJson(notificationDTO);
            }
            PushMessageRequestTO body = this.getMessageToSession(message, serviceTag, serviceKey, sessionId);
            ResponseEntity<String> response =
                this.webSocketRestTemplate.postForEntity(applicationProperties.getPushkassaConfig().getHost(),
                    new HttpEntity<>(body, SecurityUtils.getHeader()),
                    String.class
                );
            log.info(
                "Sent message to Session - serviceTag: {}, serviceKey: {}, SessionId: {}, {}",
                serviceTag,
                serviceKey,
                sessionId,
                response
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("============= websocket Error {}", e.getMessage());
        }
        return null;
    }

    private PushMessageRequestTO getMessageToSession(String message, String serviceTag, String serviceKey, String sessionId) {
        return PushMessageRequestTO.builder()
            .message(message)
            .serviceTag(serviceTag)
            .serviceKey(serviceKey)
            .type("toSession")
            .sessionId(sessionId)
            .build();
    }

    @Data
    @Builder
    public static class PushMessageRequestTO {
        private String message;
        private String type;
        private String sessionId;
        private String serviceKey;
        private String serviceTag;
    }
}
