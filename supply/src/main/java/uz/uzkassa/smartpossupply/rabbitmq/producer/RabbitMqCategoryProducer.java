package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.CategoryTransferPayload;

@Component
public class RabbitMqCategoryProducer extends RabbitMqBaseProducer {

    public void transfer(CategoryTransferPayload payload) {
        sendToQueue(payload, CATEGORY_TRANSFER_QUEUE, CATEGORY_TRANSFER_PAYLOAD);
    }
}
