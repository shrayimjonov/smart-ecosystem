package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.NotificationPayload;
import uz.uzkassa.smartpossupply.dto.payload.NotificationReadPayload;
import uz.uzkassa.smartpossupply.enums.TemplateType;
import uz.uzkassa.smartpossupply.enums.TemplateTypeAlias;

import java.util.Map;

@Component
public class RabbitMqNotificationProducer extends RabbitMqBaseProducer {

    public void send(TemplateType templateType, Long companyId, Map<TemplateTypeAlias, Object> params) {
        sendToQueue(new NotificationPayload(templateType, companyId, params), SEND_NOTIFICATION_QUEUE, SEND_NOTIFICATION_PAYLOAD);
    }

    public void read(Long notificationId, Long userId) {
        sendToQueue(new NotificationReadPayload(userId, notificationId), NOTIFICATION_READ_QUEUE, NOTIFICATION_READ_PAYLOAD);
    }
}
