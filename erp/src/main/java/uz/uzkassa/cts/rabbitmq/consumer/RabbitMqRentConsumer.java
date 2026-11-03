package uz.uzkassa.cts.rabbitmq.consumer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.service.RentPaymentHistoryService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqRentConsumer implements MqConstants {

    RentPaymentHistoryService rentPaymentHistoryService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = RENT_HISTORY_CREATE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = RENT_HISTORY_CREATE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void createRentPaymentHistoryListener(Payload payload) {
        try {
            rentPaymentHistoryService.createHistoryByCustomerId(payload.getId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("createRentPaymentHistoryListener CustomerId: " + payload.getId());
        }
    }
}
