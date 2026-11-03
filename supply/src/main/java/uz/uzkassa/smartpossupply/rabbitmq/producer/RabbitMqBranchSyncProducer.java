package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.SyncPayload;

@Component
public class RabbitMqBranchSyncProducer extends RabbitMqBaseProducer {

    public void sync(SyncPayload payload) {
        sendToQueue(payload, BRANCH_SYNC_QUEUE, BRANCH_SYNC_PAYLOAD);
    }
}
