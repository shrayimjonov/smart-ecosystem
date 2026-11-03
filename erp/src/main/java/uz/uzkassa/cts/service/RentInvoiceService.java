package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.RentInvoice;
import uz.uzkassa.cts.domain.RentPaymentHistory;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.rentinvoice.response.RentInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;

import java.time.LocalDate;
import java.util.List;

public interface RentInvoiceService extends BaseService<RentInvoice, Long> {

    void createInvoiceCron();

    void createInvoice(Long offerId, List<RentPaymentHistory> histories);

    Page<RentInvoiceResponseDto> list(TradeInInvoiceFilter filter);

    RentInvoiceResponseDto getById(Long id);

    String getHtmlTemplate(Long id);

    Page<RentInvoiceResponseDto> getByOfferId(Long offerId, TradeInInvoiceFilter filter);

    void revertInvoice(Long id);

    void resendToXfileWithNewDate(ResendToXfileRequest dto);

    byte[] download(LocalDate from, LocalDate to, ContractStatus status);

    RentInvoiceResponseDto checkInvoiceXfileStatus(Long id);
}
