package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.OfdApplicationSendPayload;
import uz.uzkassa.cts.dto.payload.OfdNeighborhoodSyncPayload;
import uz.uzkassa.cts.dto.payload.Payload;

@Component
public class RabbitMqOfdProducer extends RabbitMqBaseProducer {

    public void batchSyncApplication(Payload payload) {
        sendToQueue(payload, OFD_APPLICATION_BATCH_SYNC_QUEUE, OFD_APPLICATION_BATCH_SYNC_PAYLOAD);
    }

    public void sendApplications(OfdApplicationSendPayload payload) {
        sendToQueue(payload, OFD_APPLICATION_SEND_QUEUE, OFD_APPLICATION_SEND_PAYLOAD);
    }

    public void syncNeighborhood(OfdNeighborhoodSyncPayload payload) {
        sendToQueue(payload, OFD_NEIGHBORHOOD_SYNC_QUEUE, OFD_NEIGHBORHOOD_SYNC_PAYLOAD);
    }
}
