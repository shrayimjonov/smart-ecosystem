package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.factura.FacturaNewDTO;
import uz.uzkassa.smartpossupply.dto.payload.GenerateFacturaGUIDPayload;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.dto.payload.SyncFacturaByIdPayload;

@Component
public class RabbitMqFacturaProducer extends RabbitMqBaseProducer {

    public void syncQueue(SyncFacturaByIdPayload payload) {
        sendToQueue(payload, SYNC_FACTURA_BY_ID_QUEUE, SYNC_FACTURA_BY_ID_PAYLOAD);
    }

    public void onUpdateQueue(FacturaNewDTO payload) {
        sendToQueue(payload, FACTURA_ON_UPDATE_QUEUE, FACTURA_ON_UPDATE_PAYLOAD);
    }

    public void syncVatRegistrationCode(Payload payload) {
        sendToQueue(payload, COMPANY_VAT_REGISTRATION_CODE_SYNC_QUEUE, COMPANY_REGISTRATION_CODE_SYNC_PAYLOAD);
    }

    public void generateFacturaGUID(GenerateFacturaGUIDPayload payload) {
        sendToQueue(payload, GENERATE_FACTURA_GUID_QUEUE, GENERATE_FACTURA_GUID_PAYLOAD);
    }
}
