package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.service.OrderService;


@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqOfflineOrderConsumer implements MqConstants {

    OrderService orderService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = OFFLINE_ORDER_CREATE_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = OFFLINE_ORDER_CREATE_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void createOrder(Payload payload) {
        try {
            orderService.create(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(" Offline order create  rabbit mq error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = OFFLINE_ORDER_UPDATE_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = OFFLINE_ORDER_UPDATE_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void updateOrder(Payload payload) {
        try {
            orderService.update(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(" Offline order update  rabbit mq error: " + e.getMessage());
        }
    }
}
