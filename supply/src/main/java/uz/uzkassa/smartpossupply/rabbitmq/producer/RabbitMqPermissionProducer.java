package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.UserRolePermissionPayload;

@Component
public class RabbitMqPermissionProducer extends RabbitMqBaseProducer {

    public void save(UserRolePermissionPayload payload) {
        sendToQueue(payload, USER_PERMISSION_CREATE_QUEUE, USER_PERMISSION_CREATE_PAYLOAD);
    }
}
