package uz.uzkassa.horeca.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.payload.TasnifBasketProductPayload;

@Component
public class RabbitMqTasnifProducer extends RabbitMqBaseProducer {

    public void sync(TasnifBasketProductPayload payload) {
        sendToQueue(payload, SYNC_TASNIF_BASKET_PRODUCTS_QUEUE, SYNC_TASNIF_BASKET_PRODUCTS_PAYLOAD);
    }
}
