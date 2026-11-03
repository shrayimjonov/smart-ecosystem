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
import uz.uzkassa.smartpossupply.dto.payload.ClickhouseSyncPayload;
import uz.uzkassa.smartpossupply.service.ClickhouseSyncService;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqClickhouseSyncConsumer implements MqConstants {

    ClickhouseSyncService clickhouseSyncService;

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = SYNC_CLICKHOUSE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = SYNC_CLICKHOUSE_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void sync(ClickhouseSyncPayload payload) {
        try {
            clickhouseSyncService.sync(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sync to clickhouse error: " + e.getMessage());
        }
    }
}
