package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.service.StockInService;


@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqStockInByPartyConsumer implements MqConstants {

    StockInService stockInService;

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = STOCK_IN_BY_MARK_ORDER_PARTY_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = STOCK_IN_BY_MARK_ORDER_PARTY_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void createStockInByPartyQueue(Payload payload) {
        try {
            stockInService.createByMarkOrderParty(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Stock in by mark order party error: " + e.getMessage());
        }
    }
}
