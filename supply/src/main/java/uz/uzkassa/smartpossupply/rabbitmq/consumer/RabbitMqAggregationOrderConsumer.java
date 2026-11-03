package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.AggregationOrderPayload;
import uz.uzkassa.smartpossupply.service.AggregationOrderService;


@Component
@Slf4j
public class RabbitMqAggregationOrderConsumer implements MqConstants {

    private final AggregationOrderService aggregationOrderService;

    public RabbitMqAggregationOrderConsumer(AggregationOrderService aggregationOrderService) {
        this.aggregationOrderService = aggregationOrderService;
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = AGGREGATION_ORDER_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = AGGREGATION_ORDER_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void order(AggregationOrderPayload aggregationOrderPayload) {
        try {
            aggregationOrderService.queueOrder(aggregationOrderPayload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Aggregation order rabbit mq error: " + e.getMessage());
        }
    }
}
