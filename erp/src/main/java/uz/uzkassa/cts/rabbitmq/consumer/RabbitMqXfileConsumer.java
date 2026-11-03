package uz.uzkassa.cts.rabbitmq.consumer;

import lombok.AccessLevel;
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
import uz.uzkassa.cts.dto.factura.FacturaDTO;
import uz.uzkassa.cts.service.ActOfTransmissionInvoiceOldService;
import uz.uzkassa.cts.service.FacturaService;
import uz.uzkassa.cts.service.OneTimeOrderInvoiceOldService;
import uz.uzkassa.cts.service.ServiceOrderInvoiceOldService;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RabbitMqXfileConsumer implements MqConstants {

   FacturaService facturaService;
   ActOfTransmissionInvoiceOldService actOfTransmissionInvoiceOldService;
   ServiceOrderInvoiceOldService serviceOrderInvoiceOldService;
   OneTimeOrderInvoiceOldService oneTimeOrderInvoiceOldService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = XFILE_SERVICE_ORDER_INVOICE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"), key = XFILE_SERVICE_ORDER_INVOICE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void serviceOrderInvoiceListener(FacturaDTO invoice) {
        try {
            facturaService.sendServiceOrderInvoice(invoice);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("ServiceOrderInvoice Customer tin: " + invoice.getBuyerTin());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = XFILE_ONE_TIME_ORDER_INVOICE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = XFILE_ONE_TIME_ORDER_INVOICE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void OneTimeOrderInvoiceListener(FacturaDTO invoice) {
        try {
            facturaService.sendOneTimeOrderInvoice(invoice);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("OneTimeOrderInvoice Customer tin: " + invoice.getBuyerTin());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = XFILE_ACT_OF_TRANSMISSION_INVOICE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = XFILE_ACT_OF_TRANSMISSION_INVOICE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void actOfTransmissionInvoiceListener(FacturaDTO invoice) {
        try {
            facturaService.sendActOfTransmissionInvoice(invoice);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Act of transmission Customer tin: " + invoice.getBuyerTin());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = XFILE_TRADE_IN_INVOICE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = XFILE_TRADE_IN_INVOICE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void tradeInInvoiceListener(FacturaDTO invoice) {
        try {
            facturaService.sendTradeInInvoice(invoice);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("trade in invoice Customer tin: " + invoice.getBuyerTin());
        }
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = XFILE_TRADEIN_INVOICE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = XFILE_TRADEIN_INVOICE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void updateTradeInInvoiceListener(Long invoiceId) {
        try {
            actOfTransmissionInvoiceOldService.checkInvoiceXfileStatus(invoiceId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("update TradeIn invoice status id: " + invoiceId);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = XFILE_SERVICEORDER_INVOICE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = XFILE_SERVICEORDER_INVOICE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void updateServiceOrderInvoiceListener(Long invoiceId) {
        try {
            serviceOrderInvoiceOldService.checkInvoiceXfileStatus(invoiceId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("update ServiceOrder invoice status id: " + invoiceId);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = XFILE_ONETIME_INVOICE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = XFILE_ONETIME_INVOICE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void updateOneTimeOrderInvoiceListener(Long invoiceId) {
        try {
            oneTimeOrderInvoiceOldService.checkInvoiceXfileStatus(invoiceId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("update OneTime invoice status id: " + invoiceId);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = XFILE_ACTOFTRANSMISSION_INVOICE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = XFILE_ACTOFTRANSMISSION_INVOICE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void UpdateActOfTransmissionInvoiceListener(Long invoiceId) {
        try {
            actOfTransmissionInvoiceOldService.checkInvoiceXfileStatus(invoiceId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("update ACtOFTransmission invoice status id: " + invoiceId);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = RENT_INVOICE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = RENT_INVOICE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void rentInvoiceListener(FacturaDTO invoice) {
        try {
            facturaService.sendRentInvoice(invoice);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("rent invoice Customer tin: " + invoice.getBuyerTin());
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(durable = "false", value = X_FILE_SALE_INVOICE_QUEUE),
        exchange = @Exchange(durable = "false", value = DELAY_EXCHANGE_NAME, delayed = "true"),
        key = X_FILE_SALE_INVOICE_QUEUE),
        containerFactory = "rabbitListenerContainerFactoryMax")
    public void saleInvoiceListener(FacturaDTO invoice) {
        try {
            facturaService.sendSaleInvoice(invoice);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AmqpRejectAndDontRequeueException("SALE INVOICE CUSTOMER TIN: " + invoice.getBuyerTin());
        }
    }
}
