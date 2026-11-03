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
import uz.uzkassa.smartpossupply.service.PlanRouteService;

@Component
@Slf4j
public class RabbitMqPlanRouteConsumer implements MqConstants {

    private final PlanRouteService planRouteService;

    public RabbitMqPlanRouteConsumer(PlanRouteService planRouteService) {
        this.planRouteService = planRouteService;
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = PLAN_ROUTE_CREATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = PLAN_ROUTE_CREATE_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void create(Payload payload) {
        try {
            planRouteService.createQueue(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Create plan route queue error: " + e.getMessage());
        }
    }
}
