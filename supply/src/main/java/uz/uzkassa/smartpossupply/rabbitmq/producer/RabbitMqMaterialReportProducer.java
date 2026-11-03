package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.CommonPayload;
import uz.uzkassa.smartpossupply.dto.payload.Payload;

@Component
public class RabbitMqMaterialReportProducer extends RabbitMqBaseProducer {

    public void create(Payload payload) {
        sendToQueue(payload, CREATE_MATERIAL_REPORT_QUEUE, CREATE_MATERIAL_REPORT_PAYLOAD);
    }

    public void resync(CommonPayload payload) {
        sendToQueue(payload, RESYNC_MATERIAL_REPORT_QUEUE, RESYNC_MATERIAL_REPORT_PAYLOAD);
    }
}
