package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.AggregationOrderPayload;

@Component
public class RabbitMqAggregationOrderProducer extends RabbitMqBaseProducer {

    public void order(AggregationOrderPayload payload) {
        sendToQueue(payload, AGGREGATION_ORDER_QUEUE, AGGREGATION_ORDER_PAYLOAD);
    }
}
