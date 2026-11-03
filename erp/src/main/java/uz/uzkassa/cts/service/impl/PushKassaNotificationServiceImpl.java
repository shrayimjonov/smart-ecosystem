package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.dto.notification.PushKassaNotificationDTO;
import uz.uzkassa.cts.enums.PushKassaNotificationType;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.PushKassaNotificationService;

/**
 * Created date time: 24.01.2025 15:46
 */

@Transactional(readOnly = true)
@Service
@Slf4j
public class PushKassaNotificationServiceImpl implements PushKassaNotificationService {

    private final ApplicationProperties applicationProperties;
    private final RestTemplate socketRestTemplate;

    public PushKassaNotificationServiceImpl(ApplicationProperties applicationProperties, @Qualifier("socketRestTemplate") RestTemplate socketRestTemplate) {
        this.applicationProperties = applicationProperties;
        this.socketRestTemplate = socketRestTemplate;
    }

    @Override
    public void sendNotificationToSession(String message, String serviceTag, String serviceKey, String sessionId) {
        if (message == null) {
            return;
        }
        PushKassaNotificationDTO dto = PushKassaNotificationDTO.builder()
                .message(message)
                .serviceTag(serviceTag)
                .serviceKey(serviceKey)
                .sessionId(sessionId)
                .type(PushKassaNotificationType.toSession)
                .build();
        try {
            HttpEntity<PushKassaNotificationDTO> entity = new HttpEntity<>(dto, SecurityUtils.getHeader());
            socketRestTemplate.postForEntity(applicationProperties.getWebSocketProperty().getHost(), entity, String.class);
            log.info("Sent message to Session - serviceTag: {}, serviceKey: {}, sessionId: {}", serviceTag, serviceKey, sessionId);
        } catch (Exception e) {
            log.error("An unexpected error has occurred while sending Session notification to Push Kassa. Error: {}", e.getMessage());
        }
    }

    @Override
    public void sendNotificationToPublic(String message, String serviceTag, String serviceKey) {
        if (message == null) {
            return;
        }
        PushKassaNotificationDTO dto = PushKassaNotificationDTO.builder()
                .message(message)
                .serviceTag(serviceTag)
                .serviceKey(serviceKey)
                .type(PushKassaNotificationType.toPublic)
                .build();
        try {
            HttpEntity<PushKassaNotificationDTO> entity = new HttpEntity<>(dto, SecurityUtils.getHeader());
            socketRestTemplate.postForEntity(applicationProperties.getWebSocketProperty().getHost(), entity, String.class);
            log.info("Sent message to Public - serviceTag: {}, serviceKey: {}", serviceTag, serviceKey);
        } catch (Exception e) {
            log.error("An unexpected error has occurred while sending Public notification to Push Kassa. Error: {}", e.getMessage());
        }
    }
}
