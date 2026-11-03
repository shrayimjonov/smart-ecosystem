package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.SyncPayload;
import uz.uzkassa.smartpossupply.service.BranchSyncService;

@Component
public class RabbitMqBranchSyncConsumer implements MqConstants {

    BranchSyncService branchSyncService;

    public RabbitMqBranchSyncConsumer(BranchSyncService branchSyncService) {
        this.branchSyncService = branchSyncService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = BRANCH_SYNC_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = BRANCH_SYNC_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void sync(SyncPayload payload) {
        try {
            branchSyncService.syncQueue(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Branch sync to " + payload.getAppType().name() + " error: " + e.getMessage());
        }
    }
}
