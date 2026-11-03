package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.Payload;


@Component
public class RabbitMqMarketplaceApplicationProducer extends RabbitMqBaseProducer {

    public void send(Payload payload) {
        sendToQueue(payload, MARKETPLACE_APPLICATION_SEND_QUEUE, MARKETPLACE_APPLICATION_SEND_PAYLOAD, 0);
    }
}
