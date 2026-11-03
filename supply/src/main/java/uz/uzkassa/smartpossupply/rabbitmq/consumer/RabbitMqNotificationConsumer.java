package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.NotificationPayload;
import uz.uzkassa.smartpossupply.dto.payload.NotificationReadPayload;
import uz.uzkassa.smartpossupply.service.NotificationReadService;
import uz.uzkassa.smartpossupply.service.NotificationService;


@Component
@Slf4j
public class RabbitMqNotificationConsumer implements MqConstants {

    private final NotificationService notificationService;
    private final NotificationReadService notificationReadService;

    public RabbitMqNotificationConsumer(NotificationService notificationService, NotificationReadService notificationReadService) {
        this.notificationService = notificationService;
        this.notificationReadService = notificationReadService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = SEND_NOTIFICATION_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = SEND_NOTIFICATION_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void send(NotificationPayload payload) {
        try {
            notificationService.send(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Send notification rabbit mq error: " + e.getMessage());
        }
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = NOTIFICATION_READ_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = NOTIFICATION_READ_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void read(NotificationReadPayload payload) {
        try {
            notificationReadService.create(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Create notification read rabbit mq error: " + e.getMessage());
        }
    }

}
