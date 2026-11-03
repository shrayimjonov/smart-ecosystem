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
import uz.uzkassa.cts.dto.ofd.CustomerBranchSendDTO;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.service.OfdService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqCustomerBranchSendConsumer implements MqConstants {

    OfdService ofdService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = SEND_CUSTOMER_BRANCH_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = SEND_CUSTOMER_BRANCH_QUEUE),
        containerFactory = "rabbitListenerContainerFactorySimple")
    public void sendCustomerBranch(CustomerBranchSendDTO dto) {
        try {
            ofdService.sendCustomerBranch(dto);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("CustomerBranchSendDTO CustomerBranchId tin: " + dto.getCustomerBranchId());
        }
    }
}
