package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.smartpossupply.service.PermissionService;


@Component
@Slf4j
public class RabbitMqPermissionConsumer implements MqConstants {

    private final PermissionService permissionService;

    public RabbitMqPermissionConsumer(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

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
            throw new AmqpRejectAndDontRequeueException("Create permission by role rabbit mq error: " + e.getMessage());
        }
    }
}
