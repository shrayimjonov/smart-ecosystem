package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.domain.ActOfTransmissionInvoice;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.response.ActOfTransmissionInvoiceListResponseDto;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.response.ActOfTransmissionInvoiceResponseDto;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;

import java.time.LocalDate;
import java.util.List;

public interface ActOfTransmissionInvoiceOldService extends BaseService<ActOfTransmissionInvoice, Long> {
    ActOfTransmissionInvoiceResponseDto getByActOfTransmissionId(Long id);

    Page<ActOfTransmissionInvoiceListResponseDto> getList(PaymentScheduleInvoiceFilter filter);

    String getTemplateById(Long id);

    String getTemplateByActId(Long id);

    ActOfTransmissionInvoiceResponseDto getById(Long id);

    List<ActOfTransmissionInvoice> findInvoicesToSendXfile();

    void createInvoiceForCron(ActOfTransmission act);

    void resendInvoiceToXfileWithNewDate(ResendToXfileRequest dto);

    String getInvoicesDownloadUrl(LocalDate from, LocalDate to, ContractStatus status);

    ActOfTransmissionInvoiceResponseDto checkInvoiceXfileStatus(Long id);

    void updateInvoiceXfileStatus();
}
