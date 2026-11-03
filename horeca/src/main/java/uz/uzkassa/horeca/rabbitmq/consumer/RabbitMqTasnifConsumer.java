package uz.uzkassa.horeca.rabbitmq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.constants.MqConstants;
import uz.uzkassa.horeca.dto.payload.TasnifBasketProductPayload;
import uz.uzkassa.horeca.service.TasnifBasketProductService;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMqTasnifConsumer implements MqConstants {

    TasnifBasketProductService tasnifBasketProductService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = SYNC_TASNIF_BASKET_PRODUCTS_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = SYNC_TASNIF_BASKET_PRODUCTS_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void syncBasketProducts(TasnifBasketProductPayload tasnifBasketProductPayload) {
        try {
            tasnifBasketProductService.syncQueue(tasnifBasketProductPayload);
        } catch (Exception e) {
            log.error("Sync tasnif basket product queue error", e);
            throw new AmqpRejectAndDontRequeueException("Sync tasnif basket product queue error:" + e.getMessage());
        }
    }
}
