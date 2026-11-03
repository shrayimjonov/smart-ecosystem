package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.CustomerPayload;
import uz.uzkassa.cts.dto.payload.Payload;

@Component
public class RabbitMqCustomerProducer extends RabbitMqBaseProducer {

    public void syncCustomerBank(CustomerPayload payload) {
        sendToQueue(payload, SYNC_CUSTOMER_BANK_QUEUE, SYNC_CUSTOMER_BANK_PAYLOAD, 1);
    }

    public void syncCustomerTin(CustomerPayload payload) {
        sendToQueue(payload, SYNC_CUSTOMER_QUEUE, SYNC_CUSTOMER_PAYLOAD, 1);
    }

    public void recalculation(Payload payload) {
        sendToQueue(payload, RECALCULATION_CUSTOMER_QUEUE, RECALCULATION_CUSTOMER_PAYLOAD, 1);
    }

    public void updateCustomerStatusByPayment(String tin) {
        sendToQueue(tin, UPDATE_CUSTOMER_STATUS_BY_PAYMENT_QUEUE, UPDATE_CUSTOMER_STATUS_BY_PAYMENT_PAYLOAD, 10);
    }
}
