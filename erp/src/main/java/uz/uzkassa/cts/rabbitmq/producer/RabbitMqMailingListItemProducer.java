package uz.uzkassa.cts.rabbitmq.producer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.dto.payload.MailingListItemPayload;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class RabbitMqMailingListItemProducer extends RabbitMqBaseProducer  {

    public void send(MailingListItemPayload payload) {
        sendToQueue(payload, MAILING_LIST_ITEM_QUEUE, MAILING_LIST_ITEM_PAYLOAD);
    }
}
