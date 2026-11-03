package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.Payload;

@Component
public class RabbitMqLeaveAllowanceProducer extends RabbitMqBaseProducer {

    public void create(Payload payload) {
        sendToQueue(payload, LEAVE_ALLOWANCE_QUEUE, LEAVE_ALLOWANCE_PAYLOAD, 5);
    }
}
