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
import uz.uzkassa.smartpossupply.dto.payload.ClickhouseLogHistoryPayload;
import uz.uzkassa.smartpossupply.service.ClickhouseLogHistoryService;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.07.2023
 */
@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqClickhouseLogHistoryConsumer implements MqConstants {

    ClickhouseLogHistoryService clickhouseLogHistoryService;

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = LOG_HISTORY_CLICKHOUSE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = LOG_HISTORY_CLICKHOUSE_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void addLogHistory(ClickhouseLogHistoryPayload payload) {
        try {
            clickhouseLogHistoryService.addLogHistory(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Add to clickhouse error: " + e.getMessage());
        }
    }
}
