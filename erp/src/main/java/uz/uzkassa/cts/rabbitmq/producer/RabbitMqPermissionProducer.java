package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.UserRolePermissionPayload;

@Component
public class RabbitMqPermissionProducer extends RabbitMqBaseProducer {

    public void sendToQueue(UserRolePermissionPayload payload) {
        sendToQueue(payload, USER_PERMISSION_CREATE_QUEUE, USER_PERMISSION_CREATE_PAYLOAD);
    }
}
