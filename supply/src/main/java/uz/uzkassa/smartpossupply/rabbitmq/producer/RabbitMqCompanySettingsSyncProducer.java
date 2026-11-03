package uz.uzkassa.smartpossupply.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.dto.payload.CompanySettingsSyncPayload;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 14.07.2023
 */
@Component
public class RabbitMqCompanySettingsSyncProducer extends RabbitMqBaseProducer {

    public void sendToQueue(CompanySettingsSyncPayload payload) {
        sendToQueue(payload, COMPANY_SETTINGS_SYNC_QUEUE, COMPANY_SETTINGS_SYNC_PAYLOAD, 1);
    }
}
