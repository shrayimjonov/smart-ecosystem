package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.KapitalLegalPayment;

public interface KapitalLegalPaymentService extends BaseService<KapitalLegalPayment, Long> {

    void checkForPayments();

    void syncByDate(String date);
}
