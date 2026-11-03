package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.billing.BillingInvoicePayloadDTO;

@Component
public class RabbitMqBillingInvoiceProducer extends RabbitMqBaseProducer {

    public void sendToQueue(BillingInvoicePayloadDTO payload) {
        sendToQueue(payload, BILLING_INVOICE_CREATE_QUEUE, BILLING_INVOICE_CREATE_PAYLOAD, 3);
    }
}
