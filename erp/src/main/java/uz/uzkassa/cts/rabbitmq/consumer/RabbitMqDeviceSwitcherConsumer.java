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
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.service.NotificationOldService;
import uz.uzkassa.cts.service.ServicePaymentHistoryCronService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqDeviceSwitcherConsumer implements MqConstants {

    ServicePaymentHistoryCronService servicePaymentHistoryService;
    NotificationOldService notificationOldService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = PAYMENT_HISTORY_CREATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = PAYMENT_HISTORY_CREATE_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void paymentHistoryCreateListener(String tin) {
        try {
            servicePaymentHistoryService.paymentHistoryCreate(tin);
        } catch (Exception e) {
            log.error("payment history create exception: tin={} {}", tin, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("payment history create exception: tin=" + tin + " " + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = DEVICE_SWITCH_DELAY_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = DEVICE_SWITCH_DELAY_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void deviceStatusSwitchListener(String tin) {
        try {
            servicePaymentHistoryService.deviceStatusSwitch(tin);
        } catch (Exception e) {
            log.trace("device status switch exception: tin={} {}", tin, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("device status switch exception: tin=" + tin + " " + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SERVICE_INVOICE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SERVICE_INVOICE_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void createServiceInvoice(String tin) {
        try {
            servicePaymentHistoryService.createInvoice(tin);
        } catch (Exception e) {
            log.error("create service invoice exception: tin={} {}", tin, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("create service invoice exception: tin=" + tin + " " + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = NOTIFY_MONTHLY_FEE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = NOTIFY_MONTHLY_FEE_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void notifyAboutMonthlyServiceFeeListener(String tin) {
        try {
            notificationOldService.notifyAboutMonthlyServiceFee(tin);
        } catch (Exception e) {
            log.error("notify about monthly fee exception tin={} {}", tin, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("notify exception customer tin: " + tin);
        }
    }


    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(durable = "false", value = NOTIFY_MONTHLY_FEE_BY_SMS_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = NOTIFY_MONTHLY_FEE_BY_SMS_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void notifyAboutMonthlyServiceFeeBySmsListener(String tin) {
        try {
            notificationOldService.notifyAboutMonthlyServiceFeeBySms(tin);
        } catch (Exception e) {
            log.error("notify about monthly fee by sms exception tin: {} {}", tin, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("notify exception customer tin: " + tin);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(durable = "false", value = NOTIFY_BY_SMS_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = NOTIFY_BY_SMS_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void notifySmsListener(String phone) {
        try {
            notificationOldService.notifyBySms(phone);
        } catch (Exception e) {
            log.error("notify sms exception tin: {} {}", phone, e.getMessage());
            throw new AmqpRejectAndDontRequeueException("notify exception phone: " + phone);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(value = @Queue(durable = "false", value = NOTIFY_ALL_BY_SMS_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = NOTIFY_ALL_BY_SMS_QUEUE),
            containerFactory = "rabbitListenerContainerFactoryMax")
    public void notifyAllSmsListener(EtcRequestDTO dto) {
        try {
            notificationOldService.notifyBySms(dto);
        } catch (Exception e) {
            log.error("notify sms exception tin: {} {}", dto.getPhone(), e.getMessage());
            throw new AmqpRejectAndDontRequeueException("notify exception phone: " + dto.getPhone());
        }
    }
}
