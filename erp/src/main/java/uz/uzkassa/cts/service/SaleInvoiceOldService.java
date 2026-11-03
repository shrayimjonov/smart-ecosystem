package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.SaleAct;
import uz.uzkassa.cts.domain.SaleInvoice;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.sale.invoice.SaleInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;

import java.time.LocalDate;
import java.util.List;

public interface SaleInvoiceOldService extends BaseService<SaleInvoice, Long> {

    SaleInvoice createInvoice(SaleAct saleAct);

    Page<SaleInvoiceResponseDto> list(TradeInInvoiceFilter filter);

    SaleInvoiceResponseDto getById(Long id);

    String getHtmlTemplate(Long id);

    SaleInvoiceResponseDto getBySaleContractId(Long contractId);

    void resendToXfileWithNewDate(ResendToXfileRequest dto);

    SaleInvoiceResponseDto checkInvoiceXfileStatus(Long id);

    byte[] download(LocalDate from, LocalDate to, ContractStatus status);

    List<SaleInvoice> findInvoicesToSendXfile();
}
