package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.ServicePaymentHistory;
import uz.uzkassa.cts.dto.servicepaymenthistory.response.ServicePaymentHistoryListDto;

public interface ServicePaymentHistoryService extends BaseService<ServicePaymentHistory, Long> {
    ServicePaymentHistoryListDto getByCustomerId(Long id);

    ServicePaymentHistoryListDto getByInvoiceId(Long id);
}
