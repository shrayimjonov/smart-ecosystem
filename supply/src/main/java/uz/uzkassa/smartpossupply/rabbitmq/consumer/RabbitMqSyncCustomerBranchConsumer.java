package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.SyncCustomerBranchPayload;
import uz.uzkassa.smartpossupply.service.CustomerBranchService;

@Component
@Slf4j
public class RabbitMqSyncCustomerBranchConsumer implements MqConstants {

    private final CustomerBranchService customerBranchService;

    public RabbitMqSyncCustomerBranchConsumer(CustomerBranchService customerBranchService) {
        this.customerBranchService = customerBranchService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = SYNC_CUSTOMER_BRANCH_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = SYNC_CUSTOMER_BRANCH_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void sync(SyncCustomerBranchPayload payload) {
        try {
            customerBranchService.syncQueue(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sync customer branch queue error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = SYNC_CUSTOMER_TRADE_BRANCH_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = SYNC_CUSTOMER_TRADE_BRANCH_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void syncTradeBranches(SyncCustomerBranchPayload payload) {
        try {
            customerBranchService.syncTradeQueue(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Sync customer branch queue error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = CREATE_DEFAULT_CUSTOMER_BRANCH_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = CREATE_DEFAULT_CUSTOMER_BRANCH_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void createDefaultBranch(SyncCustomerBranchPayload payload) {
        try {
            customerBranchService.createDefaultCustomerBranchDivideCompaniesList(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Create default customer branch queue error: " + e.getMessage());
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = CREATE_DEFAULT_CUSTOMER_BRANCH_ITEM_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = CREATE_DEFAULT_CUSTOMER_BRANCH_ITEM_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMax"
    )
    public void createDefaultBranchItem(SyncCustomerBranchPayload payload) {
        try {
            customerBranchService.createDefaultCustomerBranch(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Create default customer branch item queue error: " + e.getMessage());
        }
    }
}
