package uz.uzkassa.smartpossupply.rabbitmq.consumer;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.constants.MqConstants;
import uz.uzkassa.smartpossupply.dto.payload.SyncPayload;
import uz.uzkassa.smartpossupply.service.CompanyBankService;

@Component
public class RabbitMqCompanyBankSyncConsumer implements MqConstants {

    CompanyBankService companyBankService;

    public RabbitMqCompanyBankSyncConsumer(CompanyBankService companyBankService) {
        this.companyBankService = companyBankService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = COMPANY_BANK_SYNC_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = COMPANY_BANK_SYNC_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactoryMin"
    )
    public void sync(SyncPayload payload) {
        try {
            companyBankService.syncQueue(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Company bank sync to " + payload.getAppType().getCode() + " error: " + e.getMessage());
        }
    }
}
