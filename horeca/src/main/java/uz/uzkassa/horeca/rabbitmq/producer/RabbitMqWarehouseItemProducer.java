package uz.uzkassa.horeca.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.payload.WarehouseItemPayload;

@Component
public class RabbitMqWarehouseItemProducer extends RabbitMqBaseProducer {

    public void out(WarehouseItemPayload warehouseItemPayload) {
        sendToQueue(warehouseItemPayload, WAREHOUSE_ITEM_QUEUE, WAREHOUSE_ITEM_PAYLOAD);
    }
}
