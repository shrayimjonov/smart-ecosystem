package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.SyncPayload;

@Component
public class RabbitMqCompanyBankSyncProducer extends RabbitMqBaseProducer {

    public void sync(SyncPayload payload) {
        sendToQueue(payload, COMPANY_BANK_SYNC_QUEUE, COMPANY_BANK_SYNC_PAYLOAD);
    }
}
