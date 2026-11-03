package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;

@Component
public class RabbitMqRentProducer extends RabbitMqBaseProducer {

    public void createRentPaymentHistory(Long customerId) {
        sendToQueue(customerId, RENT_HISTORY_CREATE_QUEUE, RENT_HISTORY_CREATE_PAYLOAD);
    }

}
