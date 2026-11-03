package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.ClickhouseSyncPayload;

@Component
public class RabbitMqClickhouseSyncProducer extends RabbitMqBaseProducer {

    public void sync(ClickhouseSyncPayload payload) {
        sendToQueue(payload, SYNC_CLICKHOUSE_QUEUE, SYNC_CLICKHOUSE_PAYLOAD, 3);
    }
}
