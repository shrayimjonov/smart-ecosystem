package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.ChangeLogPayload;

@Component
public class RabbitMqChangeLogProducer extends RabbitMqBaseProducer {

    public void send(ChangeLogPayload payload) {
        sendToQueue(payload, CHANGE_LOG_QUEUE, CHANGE_LOG_PAYLOAD);
    }
}
