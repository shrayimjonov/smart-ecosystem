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
import uz.uzkassa.smartpossupply.dto.payload.LgotaSyncPayload;
import uz.uzkassa.smartpossupply.service.LgotaService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.07.2023
 */
@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqLgotaSyncConsumer implements MqConstants {

    LgotaService lgotaService;

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = LGOTA_SYNC_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = LGOTA_SYNC_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void sync(LgotaSyncPayload payload) {
        try {
            lgotaService.sync(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Lgota sync rabbitMq error: " + e.getMessage());
        }
    }
}
