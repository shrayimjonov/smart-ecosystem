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
import uz.uzkassa.smartpossupply.service.PriceListService;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class RabbitMqCustomerGroupConsumer implements MqConstants {

    PriceListService priceListService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = CUSTOMER_GROUP_UPDATE_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = CUSTOMER_GROUP_UPDATE_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void change(Payload payload) {
        try {
            priceListService.refreshCustomersByCustomerGroup(payload.getId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Customer group  price list refresh error: " + e.getMessage());
        }
    }
}