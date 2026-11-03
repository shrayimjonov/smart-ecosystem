package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.ScheduleMessagePayload;

@Component
public class RabbitMqScheduledMessageProducer extends RabbitMqBaseProducer {
    public void send(ScheduleMessagePayload payload) {
        sendToQueue(payload, SEND_SCHEDULED_MESSAGE_QUEUE, SEND_SCHEDULED_MESSAGE_PAYLOAD);
    }
}
