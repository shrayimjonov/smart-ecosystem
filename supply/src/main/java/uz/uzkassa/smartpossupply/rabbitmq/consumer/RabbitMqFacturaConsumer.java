package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.factura.FacturaNewDTO;
import uz.uzkassa.smartpossupply.dto.payload.GenerateFacturaGUIDPayload;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.dto.payload.SyncFacturaByIdPayload;
import uz.uzkassa.smartpossupply.service.FacturaService;
import uz.uzkassa.smartpossupply.service.FacturaWebhookService;

@Component
@Slf4j
public class RabbitMqFacturaConsumer implements MqConstants {

    private final FacturaService facturaService;
    private final FacturaWebhookService facturaWebhookService;

    public RabbitMqFacturaConsumer(FacturaService facturaService, FacturaWebhookService facturaWebhookService) {
        this.facturaService = facturaService;
        this.facturaWebhookService = facturaWebhookService;
    }

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
            throw new AmqpRejectAndDontRequeueException("On update factura queue error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = COMPANY_VAT_REGISTRATION_CODE_SYNC_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = COMPANY_VAT_REGISTRATION_CODE_SYNC_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void syncVatRegistrationCode(Payload payload) {
        try {
            facturaService.syncVatRegistrationCode(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sync company vat reg code queue error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = GENERATE_FACTURA_GUID_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = GENERATE_FACTURA_GUID_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void generateFacturaGUID(GenerateFacturaGUIDPayload payload) {
        try {
            facturaService.syncFacturaGUID(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Generate Factura GUID queue error: " + e.getMessage());
        }
    }
}
