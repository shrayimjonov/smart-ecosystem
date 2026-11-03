package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.service.MarketplaceApplicationService;

@Component
@Slf4j
public class MarketplaceApplicationConsumer implements MqConstants {

    private final MarketplaceApplicationService marketplaceApplicationService;

    public MarketplaceApplicationConsumer(MarketplaceApplicationService marketplaceApplicationService) {
        this.marketplaceApplicationService = marketplaceApplicationService;
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = MARKETPLACE_APPLICATION_SEND_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = MARKETPLACE_APPLICATION_SEND_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void send(Payload payload) {
        try {
            marketplaceApplicationService.sendQueue(payload.getId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Send marketplace application error: " + e.getMessage());
        }
    }
}
