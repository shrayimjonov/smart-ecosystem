package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.IncomingInvoice;
import uz.uzkassa.cts.dto.factura.GetIncomingInvoiceRequest;

public interface IncomingInvoiceService extends BaseService<IncomingInvoice, Long> {

    void saveInvoices(GetIncomingInvoiceRequest dto);

}
