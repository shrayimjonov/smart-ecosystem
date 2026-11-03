package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.Payload;

@Component
public class RabbitMqCustomerGroupProducer extends RabbitMqBaseProducer {

    public void send(Payload payload) {
        sendToQueue(payload, CUSTOMER_GROUP_UPDATE_QUEUE, CUSTOMER_GROUP_UPDATE_PAYLOAD);
    }
}