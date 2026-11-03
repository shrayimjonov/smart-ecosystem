package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.ActOfTransmissionInvoice;
import uz.uzkassa.cts.dto.actoftransmissioninvoice.admin.ActOfTransmissionInvoiceListAdminDTO;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;

public interface ActOfTransmissionInvoiceService extends BaseService<ActOfTransmissionInvoice, Long> {

    Page<ActOfTransmissionInvoiceListAdminDTO> getListForAdmin(PaymentScheduleInvoiceFilter filter);
}
