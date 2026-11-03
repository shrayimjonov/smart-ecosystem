package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.HistoryPayload;

@Component
public class RabbitMqHistoryProducer extends RabbitMqBaseProducer {

    public void create(HistoryPayload payload) {
        sendToQueue(payload, HISTORY_QUEUE, HISTORY_PAYLOAD);
    }
}
