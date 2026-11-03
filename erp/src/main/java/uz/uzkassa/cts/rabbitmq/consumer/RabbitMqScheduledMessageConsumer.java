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
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.Lead;
import uz.uzkassa.cts.domain.MailingListItem;
import uz.uzkassa.cts.dto.payload.ScheduleMessagePayload;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.MailingListItemStatus;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.repository.LeadRepository;
import uz.uzkassa.cts.repository.MailingListItemRepository;
import uz.uzkassa.cts.service.SmsService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class RabbitMqScheduledMessageConsumer implements MqConstants {

    SmsService smsService;
    MailingListItemRepository mailingListItemRepository;
    LeadRepository leadRepository;
    CustomerRepository customerRepository;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = SEND_SCHEDULED_MESSAGE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = SEND_SCHEDULED_MESSAGE_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void sendScheduledMessage(ScheduleMessagePayload payload) {
        try {
            List<MailingListItem> mailingListItems = mailingListItemRepository.findAllById(payload.getMailingListItemIds());
            for (MailingListItem mailingListItem : mailingListItems) {
                String phone = null;
                if (mailingListItem.getEntityId() != null) {
                    if (EntityTypeEnum.LEAD == mailingListItem.getEntityType()) {
                        phone = leadRepository.findById(mailingListItem.getEntityId()).map(Lead::getPhone).orElse(null);
                    } else if (EntityTypeEnum.CUSTOMER == mailingListItem.getEntityType()) {
                        phone = customerRepository.findById(mailingListItem.getEntityId()).map(Customer::getPhone).orElse(null);
                    }
                } else {
                    phone = mailingListItem.getPhone();
                }
                if (phone != null) {
                    EtcRequestDTO etcRequestDto = new EtcRequestDTO(phone, payload.getMessage());
                    boolean sent = smsService.send(etcRequestDto);
                    mailingListItem.setStatus(sent ? MailingListItemStatus.SENT : MailingListItemStatus.FAILED);
                } else {
                    mailingListItem.setStatus(MailingListItemStatus.FAILED);
                }
                mailingListItemRepository.save(mailingListItem);
            }
        } catch (Exception e) {
            log.error("Send scheduled message queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Send scheduled message queue error: " + e.getMessage());
        }
    }
}
