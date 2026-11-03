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
import uz.uzkassa.smartpossupply.dto.payload.CommonPayload;
import uz.uzkassa.smartpossupply.dto.payload.Payload;
import uz.uzkassa.smartpossupply.service.MaterialReportService;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqMaterialReportConsumer implements MqConstants {

    MaterialReportService materialReportService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = CREATE_MATERIAL_REPORT_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = CREATE_MATERIAL_REPORT_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void create(Payload payload) {
        try {
            materialReportService.create(payload.getId());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Create material report error: " + e.getMessage());
        }
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(durable = "false", value = RESYNC_MATERIAL_REPORT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = RESYNC_MATERIAL_REPORT_QUEUE
        ),
        containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void resync(CommonPayload payload) {
        try {
            materialReportService.resync(payload.getIds());
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Resync material report error: " + e.getMessage());
        }
    }
}
