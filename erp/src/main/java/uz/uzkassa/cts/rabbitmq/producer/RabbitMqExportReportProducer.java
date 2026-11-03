package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.ExportReportPayload;

@Component
public class RabbitMqExportReportProducer extends RabbitMqBaseProducer {

    public void send(ExportReportPayload payload) {
        sendToQueue(payload, EXPORT_REPORT_QUEUE, EXPORT_REPORT_PAYLOAD);
    }
}
