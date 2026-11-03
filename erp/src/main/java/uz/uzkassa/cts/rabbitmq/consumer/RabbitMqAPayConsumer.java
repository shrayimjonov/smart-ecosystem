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
import uz.uzkassa.cts.dto.payload.FilterPayload;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.service.APayCategoryService;
import uz.uzkassa.cts.service.APayCustomerBranchService;
import uz.uzkassa.cts.service.APayService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqAPayConsumer implements MqConstants {

    APayCustomerBranchService aPayCustomerBranchService;
    APayService aPayService;
    APayCategoryService aPayCategoryService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = A_PAY_LIST_CUSTOMER_BRANCH_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = A_PAY_LIST_CUSTOMER_BRANCH_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void sendCustomerBranchQueue(FilterPayload filter) {
        try {
            aPayCustomerBranchService.sendCustomerBranches(filter);
        } catch (Exception e) {
            log.error("Send customer branch list to A-Pay queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Send customer branch list to A-Pay queue error:" + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = A_PAY_CREATE_CUSTOMER_BRANCH_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = A_PAY_CREATE_CUSTOMER_BRANCH_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void createCustomerBranchQueue(Payload payload) {
        try {
            aPayCustomerBranchService.createCustomerBranches(payload);
        } catch (Exception e) {
            log.error("Send customer branch create to A-Pay queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Send customer branch create to A-Pay queue error:" + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = A_PAY_UPDATE_CUSTOMER_BRANCH_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = A_PAY_UPDATE_CUSTOMER_BRANCH_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void updateCustomerBranchQueue(Payload payload) {
        try {
            aPayCustomerBranchService.updateCustomerBranches(payload);
        } catch (Exception e) {
            log.error("Send customer branch update to A-Pay queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Send customer branch update to A-Pay queue error:" + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = A_PAY_DELETE_CUSTOMER_BRANCH_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = A_PAY_DELETE_CUSTOMER_BRANCH_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void deleteCustomerBranchQueue(Payload payload) {
        try {
            aPayCustomerBranchService.deleteCustomerBranches(payload);
        } catch (Exception e) {
            log.error("Send customer branch delete to A-Pay queue error: ", e);
            throw new AmqpRejectAndDontRequeueException("Send customer branch delete to A-Pay queue error:" + e.getMessage());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = A_PAY_SYNC_CATEGORY_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = A_PAY_SYNC_CATEGORY_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void syncCategory(FilterPayload filter) {
        try {
            aPayCategoryService.syncQueue(filter);
        } catch (Exception e) {
            log.error("A-Pay category sync queue error:", e);
            throw new AmqpRejectAndDontRequeueException("A-Pay category sync queue error:" + e.getMessage());
        }
    }
}
