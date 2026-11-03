package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.LgotaSyncPayload;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.07.2023
 */
@Component
public class RabbitMqLgotaSyncProducer extends RabbitMqBaseProducer {

    public void sendToQueue(LgotaSyncPayload payload) {
        sendToQueue(payload, LGOTA_SYNC_QUEUE, LGOTA_SYNC_PAYLOAD);
    }
}
