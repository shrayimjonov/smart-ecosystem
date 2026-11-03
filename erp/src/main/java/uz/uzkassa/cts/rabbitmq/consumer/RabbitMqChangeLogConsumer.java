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
import uz.uzkassa.cts.dto.payload.ChangeLogPayload;
import uz.uzkassa.cts.service.ChangeLogService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqChangeLogConsumer implements MqConstants {

    ChangeLogService changeLogService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = CHANGE_LOG_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = CHANGE_LOG_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void changeLogHistory(ChangeLogPayload payload) {
        log.info("Received message to change history. Entity: {}, Field: {}, Old Value: {}, New Value: {} ", payload.getEntityType(), payload.getField(), payload.getOldValue(), payload.getNewValue());
        try {
            changeLogService.create(payload);
        } catch (
                Exception e) {
            log.error("Received message to change history queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Received message to change history queue error: " + e.getMessage());
        }
    }
}
