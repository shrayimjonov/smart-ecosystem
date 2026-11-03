package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.Payload;

@Component
public class RabbitMqStockInByPartyProducer extends RabbitMqBaseProducer {

    public void sendParty(Payload payload) {
        sendToQueue(payload, STOCK_IN_BY_MARK_ORDER_PARTY_QUEUE, STOCK_IN_BY_PARTY_PAYLOAD);
    }
}
