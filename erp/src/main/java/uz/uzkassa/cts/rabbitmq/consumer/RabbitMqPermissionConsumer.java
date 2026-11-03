package uz.uzkassa.cts.rabbitmq.consumer;

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
import uz.uzkassa.cts.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.cts.service.UserPermissionService;


import static lombok.AccessLevel.PRIVATE;


@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMqPermissionConsumer implements MqConstants {

    UserPermissionService userPermissionService;

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = USER_PERMISSION_CREATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = USER_PERMISSION_CREATE_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void create(UserRolePermissionPayload userRolePermissionPayload) {
        try {
            userPermissionService.create(userRolePermissionPayload);
        } catch (Exception e) {
            log.error("Create permission by role rabbit mq error: ", e);
            throw new AmqpRejectAndDontRequeueException("Create permission by role rabbit mq error: " + e.getMessage());
        }
    }
}
