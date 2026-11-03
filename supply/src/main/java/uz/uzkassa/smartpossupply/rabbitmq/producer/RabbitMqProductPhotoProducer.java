package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.ProductPhotoSyncPayload;

@Component
public class RabbitMqProductPhotoProducer extends RabbitMqBaseProducer {

    public void sync(ProductPhotoSyncPayload payload) {
        sendToQueue(payload, PRODUCT_PHOTO_SYNC_QUEUE, PRODUCT_PHOTO_SYNC_PAYLOAD);
    }
}
