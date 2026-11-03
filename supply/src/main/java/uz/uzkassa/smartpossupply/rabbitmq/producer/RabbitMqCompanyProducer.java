package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.UpdateCompanyEquipmentCountPayload;

@Component
public class RabbitMqCompanyProducer extends RabbitMqBaseProducer {

    public void updateEquipmentCount(UpdateCompanyEquipmentCountPayload payload) {
        sendToQueue(payload, UPDATE_COMPANY_EQUIPMENT_COUNT_QUEUE, UPDATE_COMPANY_EQUIPMENT_COUNT_PAYLOAD);
    }
}
