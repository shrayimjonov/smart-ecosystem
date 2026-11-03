package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.ServicePaymentHistory;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.dto.servicepaymenthistory.ServicePaymentHistoryCronDto;

import java.time.LocalDate;
import java.util.List;

public interface ServicePaymentHistoryCronService extends BaseService<ServicePaymentHistory, Long> {
    void paymentHistoryCreate(String tin);

    void paymentHistoryCreateNew(String tin);

    void deviceStatusSwitch(String tin);

    void deviceStatusSyncWithLauncher(String tin);

    void recalculation(Payload payload);

    void recalculateHistory(String tin, String deviceSerialNumber, LocalDate stopDate);

    void createInvoice(String tin);

    List<ServicePaymentHistoryCronDto> getCustomerDevices(String tin);

    void createMonthInvoiceForTest(String tin);

    void createYearlyInvoiceForTest(String tin);
}
