package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.ReminderPayload;

@Component
public class RabbitMqReminderProducer extends RabbitMqBaseProducer {
    public void sendReminder(ReminderPayload payload) {
        sendToQueue(payload, SEND_REMINDER_QUEUE, SEND_REMINDER_PAYLOAD);
    }
}
