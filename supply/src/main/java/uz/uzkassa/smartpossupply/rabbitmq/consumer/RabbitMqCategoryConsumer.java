package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.CategoryTransferPayload;
import uz.uzkassa.smartpossupply.service.CategoryService;

@Component
@Slf4j
public class RabbitMqCategoryConsumer implements MqConstants {

    CategoryService categoryService;

    public RabbitMqCategoryConsumer(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = CATEGORY_TRANSFER_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = CATEGORY_TRANSFER_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void transfer(CategoryTransferPayload payload) {
        try {
            categoryService.transferQueue(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Category transfer error: " + e.getMessage());
        }
    }
}
