package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.OneCIntegrationPayload;
import uz.uzkassa.smartpossupply.service.StockInService;

@Component
@Slf4j
public class RabbitMqOneCIntegrationConsumer implements MqConstants {

    private final StockInService stockInService;

    public RabbitMqOneCIntegrationConsumer(StockInService stockInService) {
        this.stockInService = stockInService;
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = STOCK_IN_ONE_C_INTEGRATION_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = STOCK_IN_ONE_C_INTEGRATION_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void send(OneCIntegrationPayload payload) {
        try {
            stockInService.sendTo1C(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sending StockIn to 1C queue error: " + e.getMessage());
        }
    }
}
