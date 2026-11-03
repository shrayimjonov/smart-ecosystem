package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.StockOptimizationPayload;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.06.2024
 */
@Component
public class RabbitMqStockOptimizationProducer extends RabbitMqBaseProducer {

    public void delete(StockOptimizationPayload payload) {
        sendToQueue(payload, DELETE_AGGREGATION_CODES_QUEUE, DELETE_AGGREGATION_CODES_PAYLOAD, 1);
    }

    public void deleteItems(StockOptimizationPayload payload) {
        sendToQueue(payload, DELETE_AGGREGATION_CODES_ITEM_QUEUE, DELETE_AGGREGATION_CODES_ITEM_PAYLOAD, 1);
    }
}
