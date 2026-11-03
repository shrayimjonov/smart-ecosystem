package uz.uzkassa.horeca.rabbitmq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.constants.MqConstants;
import uz.uzkassa.horeca.dto.payload.OrderEventPayload;
import uz.uzkassa.horeca.service.FirebaseNotificationService;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMqOrderConsumer implements MqConstants {

    FirebaseNotificationService firebaseNotificationService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = ORDER_EVENT_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = ORDER_EVENT_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void orderCreated(OrderEventPayload orderEventPayload) {
        try {
            firebaseNotificationService.processingOrderEvents(orderEventPayload);
        } catch (Exception e) {
            log.error("New order created queue error", e);
            throw new AmqpRejectAndDontRequeueException("New order created queue error" + e.getMessage());
        }
    }
}
