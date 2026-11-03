package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.OneTimeOrderInvoice;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.OneTimeOrderInvoiceFilter;

import java.time.LocalDate;
import java.util.List;

public interface OneTimeOrderInvoiceOldService extends BaseService<OneTimeOrderInvoice, Long> {
    void createInvoice();

    Page<OneTimeOrderInvoiceResponseDto> list(OneTimeOrderInvoiceFilter filter);

    OneTimeOrderInvoiceResponseDto getById(Long id);

    OneTimeOrderInvoiceResponseDto uploadFile(Long id, MultipartFile file);

    String getSpecificationHtmlTemplate(Long id);

    Page<OneTimeOrderInvoiceResponseDto> getByContractId(Long contractId, BaseFilter filter);

    void revertInvoice(Long id);

    String getInvoicesDownloadUrl(LocalDate from, LocalDate to, ContractStatus status);

    List<OneTimeOrderInvoice> getInvoicesForXfile(LocalDate now, LocalDate now1, ContractStatus accepted);

    void resendInvoiceToXfileWithNewDate(ResendToXfileRequest dto);

    OneTimeOrderInvoiceResponseDto checkInvoiceXfileStatus(Long id);

    void updateInvoiceXfileStatuses();

    void recalculate(Long invoiceId);
}
