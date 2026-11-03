package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.StockOptimizationPayload;
import uz.uzkassa.smartpossupply.service.StockOptimizationService;

@Component
@Slf4j
public class RabbitMqStockOptimizationConsumer implements MqConstants {

    private final StockOptimizationService stockOptimizationService;

    public RabbitMqStockOptimizationConsumer(StockOptimizationService stockOptimizationService) {
        this.stockOptimizationService = stockOptimizationService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = DELETE_AGGREGATION_CODES_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = DELETE_AGGREGATION_CODES_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void deleteCodes(StockOptimizationPayload payload) {
        try {
            stockOptimizationService.deleteCodes(payload);
        } catch (Exception e) {
            log.error("Update aggregation codes error: {} ", e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Update aggregation codes error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = DELETE_AGGREGATION_CODES_ITEM_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = DELETE_AGGREGATION_CODES_ITEM_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void deleteItems(StockOptimizationPayload payload) {
        try {
            stockOptimizationService.deleteItems(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Update aggregation codes error: " + e.getMessage());
        }
    }
}
