package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.Payload;

@Component
public class RabbitMqPlanRouteProducer extends RabbitMqBaseProducer {

    public void create(Payload payload) {
        sendToQueue(payload, PLAN_ROUTE_CREATE_QUEUE, PLAN_ROUTE_CREATE_PAYLOAD, 10);
    }
}
