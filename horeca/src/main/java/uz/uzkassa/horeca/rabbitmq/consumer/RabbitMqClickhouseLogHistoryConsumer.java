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
import uz.uzkassa.horeca.dto.payload.ClickhouseLogHistoryPayload;
import uz.uzkassa.horeca.service.ClickhouseLogHistoryService;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
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
        log.info("Clickhouse log history rabbitMq");
        try {
            clickhouseLogHistoryService.addLogHistory(payload);
        } catch (Exception e) {
            log.error("Clickhouse log history rabbitMq error: {}", payload);
            throw new AmqpRejectAndDontRequeueException("Add to clickhouse error: " + e.getMessage());
        }
    }
}
