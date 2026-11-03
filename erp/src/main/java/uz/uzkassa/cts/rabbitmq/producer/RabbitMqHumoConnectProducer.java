package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.humoconnect.request.HumoConnectLastReceiptRequestDTO;

@Component
public class RabbitMqHumoConnectProducer extends RabbitMqBaseProducer {

    public void updateLastReceipt(HumoConnectLastReceiptRequestDTO humoConnectLastReceiptRequestDTO) {
        sendToQueue(humoConnectLastReceiptRequestDTO, HUMO_CONNECT_UPDATE_LAST_RECEIPT_QUEUE, HUMO_CONNECT_UPDATE_LAST_RECEIPT_PAYLOAD);
    }
}
