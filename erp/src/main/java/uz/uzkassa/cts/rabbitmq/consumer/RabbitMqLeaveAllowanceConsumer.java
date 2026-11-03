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
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.service.LeaveAllowanceService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqLeaveAllowanceConsumer implements MqConstants {

    LeaveAllowanceService leaveAllowanceService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = LEAVE_ALLOWANCE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = LEAVE_ALLOWANCE_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void create(Payload payload) {
        log.info("Received message to create leave allowance: {}", payload);
        try {
            leaveAllowanceService.create(payload.getId());
        } catch (Exception e) {
            log.error("Received message to create leave allowance queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Received message to create leave allowance queue error: " + e.getMessage());
        }
    }
}
