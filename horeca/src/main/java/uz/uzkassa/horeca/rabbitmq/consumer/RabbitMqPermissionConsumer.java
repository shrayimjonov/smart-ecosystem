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
import uz.uzkassa.horeca.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.horeca.service.PermissionService;

import static lombok.AccessLevel.PRIVATE;


@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMqPermissionConsumer implements MqConstants {

    PermissionService permissionService;

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
            permissionService.createUserPermission(userRolePermissionPayload);
        } catch (Exception e) {
            log.error("Create permission by role rabbit mq error: ", e);
            throw new AmqpRejectAndDontRequeueException("Create permission by role rabbit mq error: " + e.getMessage());
        }
    }
}
