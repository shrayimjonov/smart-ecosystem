package uz.uzkassa.smartpossupply.rabbitmq.consumer;

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
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.domain.mailing.MailingListItem;
import uz.uzkassa.smartpossupply.dto.payload.ScheduleMessagePayload;
import uz.uzkassa.smartpossupply.dto.sms.SmsDTO;
import uz.uzkassa.smartpossupply.enums.MailingListItemStatus;
import uz.uzkassa.smartpossupply.repository.MailingListItemRepository;
import uz.uzkassa.smartpossupply.service.SmsService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class RabbitMqScheduledMessageConsumer implements MqConstants {

    SmsService smsService;
    MailingListItemRepository mailingListItemRepository;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
        value = SEND_SCHEDULED_MESSAGE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = SEND_SCHEDULED_MESSAGE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void send(ScheduleMessagePayload payload) {
        try {
            List<MailingListItem> mailingListItems = mailingListItemRepository.findAllByIds(payload.getMailingListItemIds()).stream().map(mailingListItem -> {
                String phone = mailingListItem.getCustomer().getPhone();
                if (phone != null) {
                    boolean sent = smsService.send(new SmsDTO(phone, payload.getMessage()));
                    mailingListItem.setStatus(sent ? MailingListItemStatus.SENT : MailingListItemStatus.FAILED);
                } else {
                    mailingListItem.setStatus(MailingListItemStatus.FAILED);
                }
                return mailingListItem;
            }).toList();
            mailingListItemRepository.saveAll(mailingListItems);
        } catch (Exception e) {
            log.error("Send scheduled message queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Send scheduled message queue error: " + e.getMessage());
        }
    }
}
