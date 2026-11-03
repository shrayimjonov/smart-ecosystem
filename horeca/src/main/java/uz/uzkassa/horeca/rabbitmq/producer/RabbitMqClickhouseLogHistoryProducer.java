package uz.uzkassa.horeca.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.payload.ClickhouseLogHistoryPayload;

@Component
public class RabbitMqClickhouseLogHistoryProducer extends RabbitMqBaseProducer {

    public void sendToQueue(ClickhouseLogHistoryPayload payload) {
        sendToQueue(payload, LOG_HISTORY_CLICKHOUSE_QUEUE, LOG_HISTORY_CLICKHOUSE_PAYLOAD, 3);
    }
}
