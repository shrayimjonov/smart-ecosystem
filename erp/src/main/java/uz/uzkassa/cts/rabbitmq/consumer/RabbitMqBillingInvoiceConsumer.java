package uz.uzkassa.cts.rabbitmq.consumer;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.constants.MqConstants;
import uz.uzkassa.cts.dto.billing.BillingInvoicePayloadDTO;
import uz.uzkassa.cts.service.BillingInvoiceService;

import static lombok.AccessLevel.PRIVATE;


@Component
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RabbitMqBillingInvoiceConsumer implements MqConstants {

    BillingInvoiceService billingInvoiceService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(durable = "false", value = BILLING_INVOICE_CREATE_QUEUE),
                    exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
                    key = BILLING_INVOICE_CREATE_QUEUE
            ),
            containerFactory = "rabbitListenerContainerFactorySimple"
    )
    public void create(BillingInvoicePayloadDTO payload) {
        try {
            billingInvoiceService.createPrimeInvoice(payload);
        } catch (Exception e) {
            log.error("Create billing invoice rabbit mq error: ", e);
            throw new AmqpRejectAndDontRequeueException("Create billing invoice rabbit mq error: " + e.getMessage());
        }
    }
}
