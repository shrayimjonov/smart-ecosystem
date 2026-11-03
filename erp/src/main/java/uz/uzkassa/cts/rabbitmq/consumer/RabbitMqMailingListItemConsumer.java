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
import uz.uzkassa.cts.dto.payload.MailingListItemPayload;
import uz.uzkassa.cts.service.MailingListItemService;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqMailingListItemConsumer implements MqConstants {

    MailingListItemService mailingListItemService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = MAILING_LIST_ITEM_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = MAILING_LIST_ITEM_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void sendScheduledMessage(MailingListItemPayload payload) {
        try {
            mailingListItemService.create(payload.getMailingListId(), payload.getFileId(), payload.getEntityType());
        } catch (Exception e) {
            log.error("Send scheduled message queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Send scheduled message queue error: " + e.getMessage());
        }
    }
}
