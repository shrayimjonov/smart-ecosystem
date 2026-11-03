package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.TradeInAct;
import uz.uzkassa.cts.domain.TradeInInvoice;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.tradeininvoice.response.TradeInInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;

import java.time.LocalDate;
import java.util.List;

public interface TradeInInvoiceOldService extends BaseService <TradeInInvoice, Long>{

    TradeInInvoice createInvoice(TradeInAct act);

    Page<TradeInInvoiceResponseDto> list(TradeInInvoiceFilter filter);

    TradeInInvoiceResponseDto getById(Long id);

    String getHtmlTemplate(Long id);

    TradeInInvoiceResponseDto getByContractId(Long contractId);

    void revertInvoice(Long id);

    void resendToXfileWithNewDate(ResendToXfileRequest dto);

    TradeInInvoiceResponseDto checkInvoiceXfileStatus(Long id);

    byte[] download(LocalDate from, LocalDate to, ContractStatus status);

    List<TradeInInvoice> findInvoicesToSendXfile();

    void updateInvoiceXfileStatuses();
}
