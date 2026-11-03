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
import uz.uzkassa.horeca.dto.factura.FacturaNewDTO;
import uz.uzkassa.horeca.dto.payload.SyncFacturaByIdPayload;
import uz.uzkassa.horeca.service.FacturaService;
import uz.uzkassa.horeca.service.FacturaWebhookService;

import static lombok.AccessLevel.PRIVATE;

@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMqFacturaConsumer implements MqConstants {

    FacturaService facturaService;
    FacturaWebhookService facturaWebhookService;

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = SYNC_FACTURA_BY_ID_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = SYNC_FACTURA_BY_ID_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void syncQueue(SyncFacturaByIdPayload payload) {
        try {
            facturaService.syncQueue(payload);
        } catch (Exception e) {
            log.error("Sync factura by id: " + payload.getId() + " queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Sync factura by id: " + payload.getId() + " queue error: " + e.getMessage());
        }
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = FACTURA_ON_UPDATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = FACTURA_ON_UPDATE_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void onUpdateQueue(FacturaNewDTO facturaNewDTO) {
        try {
            facturaWebhookService.onUpdateQueue(facturaNewDTO);
        } catch (Exception e) {
            log.error("On update factura queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("On update factura queue error: " + e.getMessage());
        }
    }
}
