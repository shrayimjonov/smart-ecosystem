package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.FilterPayload;
import uz.uzkassa.cts.dto.payload.Payload;

@Component
public class RabbitMqAPayProducer extends RabbitMqBaseProducer {

    public void sendCustomerBranches(FilterPayload filter) {
        sendToQueue(filter, A_PAY_LIST_CUSTOMER_BRANCH_QUEUE, A_PAY_LIST_CUSTOMER_BRANCH_PAYLOAD);
    }

    public void createCustomerBranch(Payload payload) {
        sendToQueue(payload, A_PAY_CREATE_CUSTOMER_BRANCH_QUEUE, A_PAY_CREATE_CUSTOMER_BRANCH_PAYLOAD);
    }

    public void updateCustomerBranch(Payload payload) {
        sendToQueue(payload, A_PAY_UPDATE_CUSTOMER_BRANCH_QUEUE, A_PAY_UPDATE_CUSTOMER_BRANCH_PAYLOAD);
    }

    public void deleteCustomerBranch(Payload payload) {
        sendToQueue(payload, A_PAY_DELETE_CUSTOMER_BRANCH_QUEUE, A_PAY_DELETE_CUSTOMER_BRANCH_PAYLOAD);
    }

    public void syncCategory(FilterPayload filter) {
        sendToQueue(filter, A_PAY_SYNC_CATEGORY_QUEUE, A_PAY_SYNC_CATEGORY_PAYLOAD);
    }
}
