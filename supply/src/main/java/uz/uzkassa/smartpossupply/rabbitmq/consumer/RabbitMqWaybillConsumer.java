package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillWebhookDTO;
import uz.uzkassa.smartpossupply.service.WaybillWebhookService;

@Component
@Slf4j
public class RabbitMqWaybillConsumer implements MqConstants {

    private final WaybillWebhookService waybillWebhookService;

    public RabbitMqWaybillConsumer(WaybillWebhookService waybillWebhookService) {
        this.waybillWebhookService = waybillWebhookService;
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = WAYBILL_ON_UPDATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = WAYBILL_ON_UPDATE_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void syncQueue(WaybillWebhookDTO payload) {
        try {
            waybillWebhookService.onUpdateQueue(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sync waybill by localId: " + payload.getWaybillLocalId() + " queue error: " + e.getMessage());
        }
    }
}
