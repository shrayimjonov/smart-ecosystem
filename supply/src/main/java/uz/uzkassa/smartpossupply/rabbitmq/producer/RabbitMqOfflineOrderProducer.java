package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.Payload;


@Component
public class RabbitMqOfflineOrderProducer extends RabbitMqBaseProducer {

    public void createOrder(Payload payload) {
        sendToQueue(payload, OFFLINE_ORDER_CREATE_QUEUE, OFFLINE_ORDER_CREATE_PAYLOAD);
    }

    public void updateOrder(Payload payload) {
        sendToQueue(payload, OFFLINE_ORDER_UPDATE_QUEUE, OFFLINE_ORDER_UPDATE_PAYLOAD);
    }
}
