package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.ScheduleMessagePayload;

@Component
public class RabbitMqScheduledMessageProducer extends RabbitMqBaseProducer {
    public void sendScheduledMessage(ScheduleMessagePayload payload) {
        sendToQueue(payload, SEND_SCHEDULED_MESSAGE_QUEUE, SEND_SCHEDULED_MESSAGE_PAYLOAD);
    }
}
