package uz.uzkassa.horeca.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.payload.UserRolePermissionPayload;

@Component
public class RabbitMqPermissionProducer extends RabbitMqBaseProducer {

    public void sendToQueue(UserRolePermissionPayload payload) {
        sendToQueue(payload, USER_PERMISSION_CREATE_QUEUE, USER_PERMISSION_CREATE_PAYLOAD);
    }
}
