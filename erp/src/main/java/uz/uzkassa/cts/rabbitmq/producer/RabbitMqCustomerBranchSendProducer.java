package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.ofd.CustomerBranchSendDTO;

@Component
public class RabbitMqCustomerBranchSendProducer extends RabbitMqBaseProducer {

    public void sendServiceOrderInvoice(CustomerBranchSendDTO payload) {
        sendToQueue(payload, SEND_CUSTOMER_BRANCH_QUEUE, SEND_CUSTOMER_BRANCH_PAYLOAD);
    }

}
