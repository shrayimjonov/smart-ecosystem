package uz.uzkassa.horeca.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.payload.OrderEventPayload;

@Component
public class RabbitMqOrderProducer extends RabbitMqBaseProducer {

    public void sendEvent(OrderEventPayload orderEventPayload) {
        sendToQueue(orderEventPayload, ORDER_EVENT_QUEUE, ORDER_EVENT_PAYLOAD, DEFAULT_DELAY);
    }
}
