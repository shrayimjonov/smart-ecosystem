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
import uz.uzkassa.cts.dto.payload.CustomerPayload;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.service.CustomerBankService;
import uz.uzkassa.cts.service.CustomerOldService;
import uz.uzkassa.cts.service.ServicePaymentHistoryCronService;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqCustomerConsumer implements MqConstants {

    CustomerBankService customerBankService;
    CustomerOldService customerOldService;
    ServicePaymentHistoryCronService servicePaymentHistoryCronService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(durable = "false", value = SYNC_CUSTOMER_BANK_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = SYNC_CUSTOMER_BANK_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void syncBank(CustomerPayload payload) {
        try {
            customerBankService.syncQueue(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sync customer bank with tax:" + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(durable = "false", value = SYNC_CUSTOMER_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = SYNC_CUSTOMER_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void syncCustomerWithTax(CustomerPayload payload) {
        try {
            customerOldService.syncTax(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sync customer with tax:" + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(durable = "false", value = RECALCULATION_CUSTOMER_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = RECALCULATION_CUSTOMER_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void recalculation(Payload payload) {
        try {
            servicePaymentHistoryCronService.recalculation(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sync customer with tax:" + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(durable = "false", value = UPDATE_CUSTOMER_STATUS_BY_PAYMENT_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = UPDATE_CUSTOMER_STATUS_BY_PAYMENT_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void updateCustomerStatusByPayment(String tin) {
        try {
            customerOldService.updateCustomerStatusByPayment(tin);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Update customer status by payment: " + e.getMessage());
        }
    }
}
