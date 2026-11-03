package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.ProductPhotoSyncPayload;
import uz.uzkassa.smartpossupply.service.ProductPhotosService;

@Component
@Slf4j
public class RabbitMqProductPhotoConsumer implements MqConstants {

    ProductPhotosService productPhotosService;

    public RabbitMqProductPhotoConsumer(ProductPhotosService productPhotosService) {
        this.productPhotosService = productPhotosService;
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = PRODUCT_PHOTO_SYNC_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = PRODUCT_PHOTO_SYNC_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void sync(ProductPhotoSyncPayload payload) {
        try {
            productPhotosService.syncProductPhotos(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Product transfer error: " + e.getMessage());
        }
    }

}
