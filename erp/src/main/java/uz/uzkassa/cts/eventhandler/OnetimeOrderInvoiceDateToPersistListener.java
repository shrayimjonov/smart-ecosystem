package uz.uzkassa.cts.eventhandler;

import uz.uzkassa.cts.domain.OneTimeOrderInvoice;

import jakarta.persistence.PrePersist;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class OnetimeOrderInvoiceDateToPersistListener {

    @PrePersist
//    @PreUpdate
    public void invoiceDateBeforeSave(final OneTimeOrderInvoice invoice) {
        LocalDateTime todayAt3 = LocalDate.now().atTime(15, 1);
        if (invoice.getInvoiceDate().isAfter(todayAt3))
            invoice.setInvoiceDate(invoice.getInvoiceDate().plusHours(9));
    }
}
