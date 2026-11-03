package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.ServiceOrderInvoice;

import java.time.LocalDate;

public interface CronInvoiceService extends BaseService<ServiceOrderInvoice, Long> {
    void generateInvoice(LocalDate localDate);
}
