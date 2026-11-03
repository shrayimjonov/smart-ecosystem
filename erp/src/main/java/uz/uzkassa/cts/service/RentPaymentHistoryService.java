package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.RentPaymentHistory;
import uz.uzkassa.cts.domain.RentPaymentHistoryId;
import uz.uzkassa.cts.dto.rentpaymenthistory.request.RentPaymentHistoryRequestDto;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;


public interface RentPaymentHistoryService extends BaseService<RentPaymentHistory, RentPaymentHistoryId> {
    void createHistoryByCustomerId(Long customerId);
    Page<RentPaymentHistoryRequestDto> getByCustomerId(BaseFilter filter);
    List<RentPaymentHistoryRequestDto> getByOfferId(Long offerId);
    List<RentPaymentHistoryRequestDto> getCurrentDetailsByOfferId(Long offerId);
    List<RentPaymentHistoryRequestDto> getByInvoiceId(Long invoiceId);

    List<RentPaymentHistory> getListForBonusCalculation();

    void createPaymentHistoryCron();

    void calculateBonusCron();
}
