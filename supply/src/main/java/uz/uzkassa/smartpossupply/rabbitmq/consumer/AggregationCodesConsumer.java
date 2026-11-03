package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.AggregationCodesPayload;
import uz.uzkassa.smartpossupply.service.WarehouseProductService;

@Component
@Slf4j
public class AggregationCodesConsumer implements MqConstants {

    private final WarehouseProductService warehouseProductService;

    public AggregationCodesConsumer(WarehouseProductService warehouseProductService) {
        this.warehouseProductService = warehouseProductService;
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = AGGREGATION_CODES_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = AGGREGATION_CODES_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void updateCodes(AggregationCodesPayload payload) {
        try {
            warehouseProductService.updateCodes(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Update aggregation codes error: " + e.getMessage());
        }
    }
}
