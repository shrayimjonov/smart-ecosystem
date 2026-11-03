package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.BillingInvoice;
import uz.uzkassa.cts.dto.billing.BillingInvoiceDTO;
import uz.uzkassa.cts.dto.billing.BillingInvoicePayloadDTO;

public interface BillingInvoiceService extends BaseService<BillingInvoice, Long> {

    void createPrimeInvoice(BillingInvoicePayloadDTO billingInvoicePayloadDTO);

    Long create(BillingInvoiceDTO billingInvoiceDTO);
}
