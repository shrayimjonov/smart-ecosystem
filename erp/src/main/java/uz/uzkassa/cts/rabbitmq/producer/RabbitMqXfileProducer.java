package uz.uzkassa.cts.rabbitmq.producer;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.factura.FacturaDTO;

@Component
public class RabbitMqXfileProducer extends RabbitMqBaseProducer {

    public void sendServiceOrderInvoice(FacturaDTO facturaDTO) {
        sendToQueue(facturaDTO, XFILE_SERVICE_ORDER_INVOICE_QUEUE, XFILE_SERVICE_ORDER_INVOICE_PAYLOAD);
    }

    public void sendOneTimeOrderInvoice(FacturaDTO facturaDTO) {
        sendToQueue(facturaDTO, XFILE_ONE_TIME_ORDER_INVOICE_QUEUE, XFILE_ONE_TIME_ORDER_INVOICE_PAYLOAD);
    }

    public void sendActOfTransmissionInvoice(FacturaDTO facturaDTO) {
        sendToQueue(facturaDTO, XFILE_ACT_OF_TRANSMISSION_INVOICE_QUEUE, XFILE_ACT_OF_TRANSMISSION_INVOICE_PAYLOAD);
    }

    public void sendTradeInInvoice(FacturaDTO facturaDTO) {
        sendToQueue(facturaDTO, XFILE_TRADE_IN_INVOICE_QUEUE, XFILE_TRADE_IN_INVOICE_PAYLOAD);
    }

    public void updateActOfTransmissionInvoice(Long id) {
        sendToQueue(id, XFILE_ACTOFTRANSMISSION_INVOICE_QUEUE, XFILE_ACTOFTRANSMISSION_INVOICE_PAYLOAD);
    }

    public void updateServiceOrderInvoice(Long invoiceId) {
        sendToQueue(invoiceId, XFILE_SERVICEORDER_INVOICE_QUEUE, XFILE_SERVICEORDER_INVOICE_PAYLOAD);
    }

    public void updateOneTimeInvoice(Long invoiceId) {
        sendToQueue(invoiceId, XFILE_ONETIME_INVOICE_QUEUE, XFILE_ONETIME_INVOICE_PAYLOAD);
    }

    public void updateTradeInInvoice(Long invoiceId) {
        sendToQueue(invoiceId, XFILE_TRADEIN_INVOICE_QUEUE, XFILE_TRADEIN_INVOICE_PAYLOAD);
    }

    public void sendRentInvoice(FacturaDTO facturaDTO) {
        sendToQueue(facturaDTO, RENT_INVOICE_QUEUE, RENT_INVOICE_PAYLOAD);
    }

    public void sendSaleInvoice(FacturaDTO facturaDTO) {
        sendToQueue(facturaDTO, X_FILE_SALE_INVOICE_QUEUE, X_FILE_SALE_INVOICE_PAYLOAD);
    }
}
