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
import uz.uzkassa.cts.dto.payload.AttendancePayload;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.service.AttendanceService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqAttendanceConsumer implements MqConstants {

    AttendanceService attendanceService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = ATTENDANCE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = ATTENDANCE_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void create(AttendancePayload payload) {
        log.info("Received message to create attendance: {}", payload);
        try {
            attendanceService.create(payload.getEmployeeId(), payload.getHolidayIds());
        } catch (Exception e) {
            log.error("Received message to create attendance queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Received message to create attendance queue error: " + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = ATTENDANCE_ITEM_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = ATTENDANCE_ITEM_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void createItem(Payload payload) {
        log.info("Payload to create attendance item: {}", payload);
        try {
            attendanceService.updateItem(payload.getId());
        } catch (Exception e) {
            log.error("Error in create attendance item queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Error in create attendance item queue error: " + e.getMessage());
        }
    }
}
