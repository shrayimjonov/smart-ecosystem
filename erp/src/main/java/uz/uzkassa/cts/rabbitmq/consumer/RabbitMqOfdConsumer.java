package uz.uzkassa.cts.rabbitmq.consumer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.dto.payload.*;
import uz.uzkassa.cts.service.NeighborhoodService;
import uz.uzkassa.cts.service.OfdApplicationService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqOfdConsumer implements MqConstants {

    OfdApplicationService ofdApplicationService;
    NeighborhoodService neighborhoodService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = OFD_APPLICATION_BATCH_SYNC_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = OFD_APPLICATION_BATCH_SYNC_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void ofdApplicationBatchSyncQueue(Payload payload) {
        try {
            ofdApplicationService.batchSyncApplication(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("OFD applications batch sync queue error: " + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = OFD_APPLICATION_RESYNC_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = OFD_APPLICATION_RESYNC_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void ofdApplicationReSyncQueue(OfdApplicationPayload ofdApplicationPayload) {
        try {
            ofdApplicationService.syncApplication(ofdApplicationPayload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("OFD applications resync queue error:" + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = OFD_APPLICATION_SEND_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = OFD_APPLICATION_SEND_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void sendApplicationQueue(OfdApplicationSendPayload ofdApplicationSendPayload) {
        try {
            ofdApplicationService.sendApplication(ofdApplicationSendPayload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("OFD applications send queue error: " + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = OFD_NEIGHBORHOOD_SYNC_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = OFD_NEIGHBORHOOD_SYNC_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void syncNeighborhood(OfdNeighborhoodSyncPayload ofdNeighborhoodSyncPayload) {
        try {
            neighborhoodService.syncFromOfd(ofdNeighborhoodSyncPayload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("OFD neighborhoods sync error :" + e.getMessage());
        }
    }
}
