package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportItemPayload;
import uz.uzkassa.smartpossupply.dto.payload.ImportReportPayload;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 29.03.2023 14:46
 */

@Component
public class RabbitMqImportReportProducer extends RabbitMqBaseProducer {

    public void send(ImportReportPayload payload) {
        sendToQueue(payload, IMPORT_REPORT_QUEUE, IMPORT_REPORT_PAYLOAD);
    }

    public void sendItems(ImportReportItemPayload payload) {
        sendToQueue(payload, IMPORT_REPORT_ITEM_QUEUE, IMPORT_REPORT_ITEM_PAYLOAD);
    }
}
