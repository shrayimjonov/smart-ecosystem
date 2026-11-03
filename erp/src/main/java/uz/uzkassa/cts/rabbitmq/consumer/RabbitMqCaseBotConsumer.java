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
import uz.uzkassa.cts.dto.payload.CaseBotPayload;
import uz.uzkassa.cts.service.CaseService;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqCaseBotConsumer implements MqConstants {

    CaseService caseService;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(durable = "false", value = CASE_TELEGRAM_BOT_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = CASE_TELEGRAM_BOT_QUEUE),
        containerFactory = "rabbitListenerContainerFactorySimple")
    public void sendToTelegramBot(CaseBotPayload caseBotPayload) {
        try {
            caseService.sendToTelegramBot(caseBotPayload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Send to telegram bot exception:" + e.getMessage());
        }
    }
}
