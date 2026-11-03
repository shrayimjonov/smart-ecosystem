package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillWebhookDTO;


@Component
public class RabbitMqWaybillProducer extends RabbitMqBaseProducer {

    public void onUpdateQueue(WaybillWebhookDTO payload) {
        sendToQueue(payload, WAYBILL_ON_UPDATE_QUEUE, WAYBILL_ON_UPDATE_PAYLOAD);
    }
}
