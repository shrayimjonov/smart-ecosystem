package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.stock.CheckAslBelgiDTO;
import uz.uzkassa.smartpossupply.service.InvalidAggregationService;

@Component
@Slf4j
public class RabbitMqAslBelgiConsumer implements MqConstants {

    private final InvalidAggregationService invalidAggregationService;

    public RabbitMqAslBelgiConsumer(InvalidAggregationService invalidAggregationService) {
        this.invalidAggregationService = invalidAggregationService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = CHECK_ASL_BELGI_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = CHECK_ASL_BELGI_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void check(CheckAslBelgiDTO checkAslBelgiDTO) {
        try {
            invalidAggregationService.checkQueue(checkAslBelgiDTO);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Check aggregation codes from Asl Belgi queue error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = CHECK_CODE_ASL_BELGI_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = CHECK_CODE_ASL_BELGI_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void checkItem(CheckAslBelgiDTO checkAslBelgiDTO) {
        try {
            invalidAggregationService.checkItem(checkAslBelgiDTO);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Check aggregation codes from Asl Belgi queue error: " + e.getMessage());
        }
    }
}
