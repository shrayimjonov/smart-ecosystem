package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.SyncPayload;
import uz.uzkassa.smartpossupply.service.CompanySyncService;

@Component
public class RabbitMqCompanySyncConsumer implements MqConstants {

    CompanySyncService companySyncService;

    public RabbitMqCompanySyncConsumer(CompanySyncService companySyncService) {
        this.companySyncService = companySyncService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = COMPANY_SYNC_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = COMPANY_SYNC_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void sync(SyncPayload payload) {
        try {
            companySyncService.syncQueue(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Company sync to " + payload.getAppType().name() + " error: " + e.getMessage());
        }
    }
}
