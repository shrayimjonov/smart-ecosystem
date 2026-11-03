package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.stock.CheckAslBelgiDTO;

@Component
public class RabbitMqAslBelgiProducer extends RabbitMqBaseProducer {

    public void check(CheckAslBelgiDTO payload) {
        sendToQueue(payload, CHECK_ASL_BELGI_QUEUE, CHECK_ASL_BELGI_PAYLOAD, 1);
    }

    public void checkChild(CheckAslBelgiDTO payload) {
        sendToQueue(payload, CHECK_CODE_ASL_BELGI_QUEUE, CHECK_CODE_ASL_BELGI_PAYLOAD, 1);
    }
}
