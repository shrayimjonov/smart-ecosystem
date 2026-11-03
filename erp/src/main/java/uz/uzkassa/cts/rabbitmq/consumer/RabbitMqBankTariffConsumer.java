package uz.uzkassa.cts.rabbitmq.consumer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.service.BankTariffHistoryService;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqBankTariffConsumer implements MqConstants {

    BankTariffHistoryService bankTariffHistoryService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false",
            value = BANK_TARIFF_HISTORY_CREATE_QUEUE),
            exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
            key = BANK_TARIFF_HISTORY_CREATE_QUEUE),
            containerFactory = "rabbitListenerContainerFactorySimple")
    public void bankTariffHistoryListener(Payload payload) {
        try {
            bankTariffHistoryService.syncListener(payload);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("BankTariffHistoryListener ID: " + payload.getId());
        }
    }
}
