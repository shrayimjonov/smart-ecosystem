package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.uzcard.UzcardConnectionPayloadDTO;

@Component
public class RabbitMqUzcardConnectionProducer extends RabbitMqBaseProducer {

    public void checkUpdate(UzcardConnectionPayloadDTO payload) {
        sendToQueue(payload, UZ_CARD_CONNECTION_QUEUE, UZ_CARD_CONNECTION_PAYLOAD);
    }
}
