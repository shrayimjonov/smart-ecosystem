package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.payload.Payload;

@Component
public class RabbitMqBankTariffProducer extends RabbitMqBaseProducer {

    public void sync(Payload payload) {
        sendToQueue(payload, BANK_TARIFF_HISTORY_CREATE_QUEUE, BANK_TARIFF_HISTORY_CREATE_PAYLOAD);
    }
}
