package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.Payload;

@Component
public class RabbitMqFMReportProducer extends RabbitMqBaseProducer {

    public void send(Payload payload) {
        sendToQueue(payload, FISCAL_MODULE_REPORT_QUEUE, FISCAL_MODULE_REPORT_PAYLOAD);
    }
}
