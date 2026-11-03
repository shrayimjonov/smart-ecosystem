package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.SyncCustomerBranchPayload;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.06.2023
 */
@Component
public class RabbitMqCustomerBranchProducer extends RabbitMqBaseProducer {

    public void send(SyncCustomerBranchPayload payload) {
        sendToQueue(payload, SYNC_CUSTOMER_BRANCH_QUEUE, SYNC_CUSTOMER_BRANCH_PAYLOAD);
    }

    public void sendTradeSync(SyncCustomerBranchPayload payload) {
        sendToQueue(payload, SYNC_CUSTOMER_TRADE_BRANCH_QUEUE, SYNC_CUSTOMER_TRADE_BRANCH_PAYLOAD);
    }

    public void sendCreateDefaultBranch(SyncCustomerBranchPayload payload) {
        sendToQueue(payload, CREATE_DEFAULT_CUSTOMER_BRANCH_QUEUE, CREATE_DEFAULT_CUSTOMER_BRANCH_PAYLOAD);
    }

    public void sendCreateDefaultBranchItem(SyncCustomerBranchPayload payload) {
        sendToQueue(payload, CREATE_DEFAULT_CUSTOMER_BRANCH_ITEM_QUEUE, CREATE_DEFAULT_CUSTOMER_BRANCH_ITEM_PAYLOAD);
    }
}
