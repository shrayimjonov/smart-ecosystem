package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.ImportReportPayload;

@Component
public class RabbitMqImportReportProducer extends RabbitMqBaseProducer {

    public void send(ImportReportPayload payload) {
        sendToQueue(payload, IMPORT_REPORT_QUEUE, IMPORT_REPORT_PAYLOAD);
    }
}
