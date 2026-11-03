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
import uz.uzkassa.cts.service.FiscalModuleReportService;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqFiscalModuleReportConsumer implements MqConstants {

    FiscalModuleReportService fiscalModuleReportService;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(durable = "false", value = FISCAL_MODULE_REPORT_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = FISCAL_MODULE_REPORT_QUEUE),
        containerFactory = "rabbitListenerContainerFactorySimple")
    public void send(Payload payload) {
        try {
            fiscalModuleReportService.create(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("RabbitMq FM report consumer exception:" + e.getMessage());
        }
    }
}
