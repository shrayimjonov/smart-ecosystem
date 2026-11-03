package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.ActPaymentSchedule;
import uz.uzkassa.cts.domain.ContractForLeasing;
import uz.uzkassa.cts.domain.PaymentScheduleInvoice;
import uz.uzkassa.cts.dto.paymentscheduleinvoice.response.PaymentScheduleInvoiceResponseDto;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;

import java.util.List;

public interface PaymentScheduleInvoiceService extends BaseService<PaymentScheduleInvoice, Long> {
    PaymentScheduleInvoice create(ActPaymentSchedule schedule, ContractForLeasing contract);

    void createInvoiceAndChangeBalance(ActPaymentSchedule schedule);

    void changeActStatus();

    List<PaymentScheduleInvoiceResponseDto> getByActOfTransmissionId(Long id);

    Page<PaymentScheduleInvoiceResponseDto> getList(PaymentScheduleInvoiceFilter filter);

    String getTemplateById(Long id);

    PaymentScheduleInvoiceResponseDto getById(Long id);

    PaymentScheduleInvoiceResponseDto getByScheduleId(Long id);

    String getTemplateByScheduleId(Long id);
}
