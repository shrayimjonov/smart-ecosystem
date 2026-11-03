package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.NotificationPayload;

@Component
public class RabbitMqNotificationProducer extends RabbitMqBaseProducer {

    public void sendToQueue(NotificationPayload payload) {
        sendToQueue(payload, NOTIFICATION_CREATE_QUEUE, NOTIFICATION_CREATE_PAYLOAD);
    }
}
