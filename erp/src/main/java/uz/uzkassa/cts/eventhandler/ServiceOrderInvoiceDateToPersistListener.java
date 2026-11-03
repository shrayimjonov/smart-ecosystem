package uz.uzkassa.cts.eventhandler;

import uz.uzkassa.cts.domain.ServiceOrderInvoice;

import jakarta.persistence.PrePersist;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ServiceOrderInvoiceDateToPersistListener {
    @PrePersist
//    @PreUpdate
    public void invoiceDateBeforeSave(final ServiceOrderInvoice invoice) {
        LocalDateTime todayAt3 = LocalDate.now().atTime(15, 1);
        if (invoice.getInvoiceDate().isAfter(todayAt3))
            invoice.setInvoiceDate(invoice.getInvoiceDate().plusHours(9));
    }
}
