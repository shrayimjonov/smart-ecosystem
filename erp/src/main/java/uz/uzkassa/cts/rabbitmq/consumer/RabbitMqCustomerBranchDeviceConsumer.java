package uz.uzkassa.cts.rabbitmq.consumer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
import uz.uzkassa.cts.dto.customerbranchdevicechangetariff.CustomerBranchDeviceExcelDTO;
import uz.uzkassa.cts.service.CustomerBranchDeviceService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqCustomerBranchDeviceConsumer implements MqConstants {

    CustomerBranchDeviceService customerBranchDeviceService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
        value = SEND_CUSTOMER_BRANCH_DEVICE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = SEND_CUSTOMER_BRANCH_DEVICE_QUEUE),
        containerFactory = "rabbitListenerContainerFactorySimple")
    public void updatePaymentStatus(CustomerBranchDeviceExcelDTO data) {
        try {
            customerBranchDeviceService.updatePaymentStatus(data);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Update payment status of CustomerBranchDevice in rabbit MQ: {}" + data);
        }
    }
}
