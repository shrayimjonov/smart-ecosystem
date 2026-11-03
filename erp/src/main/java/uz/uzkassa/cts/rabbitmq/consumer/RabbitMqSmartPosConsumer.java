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
import uz.uzkassa.cts.dto.fm.FiscalizationSyncDTO;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.dto.payload.SmartPosRemoveDevicePayload;
import uz.uzkassa.cts.service.FiscalizationService;
import uz.uzkassa.cts.service.SmartposService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqSmartPosConsumer implements MqConstants {

    SmartposService smartposService;
    FiscalizationService fiscalizationService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SMARTPOS_UPDATE_CUSTOMER_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SMARTPOS_UPDATE_CUSTOMER_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void updateCustomerQueue(Payload payload) {
        try {
            smartposService.updateCustomer(payload.getId());
        } catch (Exception e) {
            log.error("Update customer on smartPos queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Update customer on smartPos queue error:" + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SMARTPOS_REMOVE_DEVICE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = SMARTPOS_REMOVE_DEVICE_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void removeDevice(SmartPosRemoveDevicePayload smartPosRemoveDevicePayload) {
        try {
            smartposService.removeDevice(smartPosRemoveDevicePayload);
        } catch (Exception e) {
            log.error("Remove Device from SmartPos Error: ", e);
            throw new AmqpRejectAndDontRequeueException("Remove Device from SmartPos Error:" + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = SMARTPOS_SYNC_CUSTOMER_BRANCH_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = SMARTPOS_SYNC_CUSTOMER_BRANCH_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void syncCustomerBranchQueue(Payload payload) {
        try {
            smartposService.syncCustomerBranch(payload.getId());
        } catch (Exception e) {
            log.error("Sync customer branch to smartPos queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Sync customer branch to smartPos queue error: " + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = SYNC_CUSTOMER_SMARTPOS_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = SYNC_CUSTOMER_SMARTPOS_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void syncCustomerQueue(FiscalizationSyncDTO fiscalizationSyncDTO) {
        try {
            fiscalizationService.syncWithSmartpos(fiscalizationSyncDTO);
        } catch (Exception e) {
            log.error("Sync customer to smartPos queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Sync customer to smartPos queue error: " + e.getMessage());
        }
    }
}
