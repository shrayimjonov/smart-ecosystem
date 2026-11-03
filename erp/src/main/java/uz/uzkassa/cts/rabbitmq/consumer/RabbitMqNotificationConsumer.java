package uz.uzkassa.cts.rabbitmq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.dto.notification.NotificationDTO;
import uz.uzkassa.cts.dto.payload.NotificationPayload;
import uz.uzkassa.cts.security.jwt.TokenProvider;
import uz.uzkassa.cts.service.NotificationService;

import static lombok.AccessLevel.PRIVATE;


@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMqNotificationConsumer implements MqConstants {

    NotificationService notificationService;
    TokenProvider tokenProvider;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = NOTIFICATION_CREATE_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = NOTIFICATION_CREATE_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactorySimple"
    )
    public void create(NotificationPayload payload, @Header("Authorization") String token) {
        SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(token));
        try {
            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setCompanyId(payload.getCompanyId());
            notificationDTO.setReceiverId(payload.getReceiverId());
            notificationDTO.setEntityId(payload.getEntityId());
            notificationDTO.setEntityType(payload.getEntityType());
            notificationDTO.setMessageRu(payload.getMessageRu());
            notificationDTO.setMessageUzLatin(payload.getMessageUzLatin());
            notificationDTO.setMessageUzCyrillic(payload.getMessageUzCyrillic());
            notificationService.create(notificationDTO);
        } catch (Exception e) {
            log.error("Create notification rabbit mq error: ", e);
            throw new AmqpRejectAndDontRequeueException("Create notification rabbit mq error: " + e.getMessage());
        }
    }
}
