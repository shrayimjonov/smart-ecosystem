package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.UpdateCompanyEquipmentCountPayload;
import uz.uzkassa.smartpossupply.service.CompanyService;

@Component
@Slf4j
public class RabbitMqCompanyConsumer implements MqConstants {

    private final CompanyService companyService;

    public RabbitMqCompanyConsumer(CompanyService companyService) {
        this.companyService = companyService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = UPDATE_COMPANY_EQUIPMENT_COUNT_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = UPDATE_COMPANY_EQUIPMENT_COUNT_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void updateEquipmentCount(UpdateCompanyEquipmentCountPayload payload) {
        try {
            companyService.updateEquipmentCount(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Update company equipment count queue error: " + e.getMessage());
        }
    }
}
