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
import uz.uzkassa.cts.dto.payload.OfdSyncInactiveFiscalModulePayload;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.service.InactiveFiscalModuleService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqFiscalModuleConsumer implements MqConstants {

    InactiveFiscalModuleService inactiveFiscalModuleService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = INACTIVE_FISCAL_MODULE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = INACTIVE_FISCAL_MODULE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void syncInactiveFiscalModules(OfdSyncInactiveFiscalModulePayload ofdSyncInactiveFiscalModulePayload) {
        try {
            inactiveFiscalModuleService.syncInactiveFiscalModules(ofdSyncInactiveFiscalModulePayload);
        } catch (Exception e) {
            log.error("Sync inactive list from OFD exception", e);
            throw new AmqpRejectAndDontRequeueException("Sync inactive list from OFD exception:" + e.getMessage());
        }
    }
}
