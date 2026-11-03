package uz.uzkassa.horeca.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.factura.FacturaNewDTO;
import uz.uzkassa.horeca.dto.payload.SyncFacturaByIdPayload;

@Component
public class RabbitMqFacturaProducer extends RabbitMqBaseProducer {

    public void syncQueue(SyncFacturaByIdPayload payload) {
        sendToQueue(payload, SYNC_FACTURA_BY_ID_QUEUE, SYNC_FACTURA_BY_ID_PAYLOAD);
    }

    public void onUpdateQueue(FacturaNewDTO payload) {
        sendToQueue(payload, FACTURA_ON_UPDATE_QUEUE, FACTURA_ON_UPDATE_PAYLOAD);
    }
}
