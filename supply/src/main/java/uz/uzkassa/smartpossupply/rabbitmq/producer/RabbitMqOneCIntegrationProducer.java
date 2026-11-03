package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.OneCIntegrationPayload;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 25.01.2024
 */
@Component
public class RabbitMqOneCIntegrationProducer extends RabbitMqBaseProducer {

    public void sendToQueue(OneCIntegrationPayload payload) {
        sendToQueue(payload, STOCK_IN_ONE_C_INTEGRATION_QUEUE, STOCK_IN_ONE_C_INTEGRATION_PAYLOAD, 1);
    }
}
