package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.AggregationCodesPayload;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.06.2024
 */
@Component
public class RabbitMqAggregationCodesProducer extends RabbitMqBaseProducer {

    public void update(AggregationCodesPayload payload) {
        sendToQueue(payload, AGGREGATION_CODES_QUEUE, AGGREGATION_CODES_PAYLOAD, 1);
    }
}
