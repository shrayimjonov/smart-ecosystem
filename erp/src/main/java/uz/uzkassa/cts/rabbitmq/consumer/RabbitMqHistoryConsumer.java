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
import uz.uzkassa.cts.dto.payload.HistoryPayload;
import uz.uzkassa.cts.service.HistoryService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqHistoryConsumer implements MqConstants {

    HistoryService historyService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = HISTORY_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = HISTORY_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void updateCustomerQueue(HistoryPayload payload) {
        log.info("Received message to log history: {}", payload.getSerialNumber());
        try {
            historyService.create(payload);
        } catch (Exception e) {
            log.error("Received message to log history queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Received message to log history queue error:" + e.getMessage());
        }
    }
}
