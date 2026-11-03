package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.ClickhouseLogHistoryPayload;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.07.2023
 */
@Component
public class RabbitMqClickhouseLogHistoryProducer extends RabbitMqBaseProducer {

    public void sendToQueue(ClickhouseLogHistoryPayload payload) {
        sendToQueue(payload, LOG_HISTORY_CLICKHOUSE_QUEUE, LOG_HISTORY_CLICKHOUSE_PAYLOAD, 3);
    }
}
