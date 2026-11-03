package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.ServiceOrderInvoice;
import uz.uzkassa.cts.dto.invoice.request.ChangeInvoiceOwnerRequestDto;
import uz.uzkassa.cts.dto.invoice.request.InvoiceCalculateDto;
import uz.uzkassa.cts.dto.invoice.request.ResendToXfileRequest;
import uz.uzkassa.cts.dto.serviceorderinvoice.request.RevertInvoiceRequestDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.request.ServiceInvoiceRequestDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceResponseDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.ServiceOrderInvoiceFilter;

import java.time.LocalDate;
import java.util.List;

public interface ServiceOrderInvoiceOldService extends BaseService<ServiceOrderInvoice, Long> {
    InvoiceCalculateDto calculate(Long specificationId);

    ServiceOrderInvoiceResponseDto create(ServiceInvoiceRequestDto dto);

    void recalculate(Long invoiceId);

    ServiceOrderInvoiceResponseDto getById(Long id);

    ServiceOrderInvoiceResponseDto uploadFile(Long id, MultipartFile file);

    String getInvoiceTemplate(Long id);

    void deleteByIdIfNotAccepted(Long id);

    Page<ServiceOrderInvoiceResponseDto> getByContractId(Long contractId, BaseFilter filter);

    List<ServiceOrderInvoiceListDto> getInvoices(LocalDate from, LocalDate to, ContractStatus status);

    List<ServiceOrderInvoice> getInvoicesForXfile(LocalDate from, LocalDate to, ContractStatus status);

    List<ServiceOrderInvoice> resendInvoicesToXfile(LocalDate from, LocalDate to, ContractStatus status);

    List<ServiceOrderInvoice> getInvoicesForXfileByTin(String tin, LocalDate from, LocalDate to, ContractStatus status);

    List<ServiceOrderInvoice> resendInvoicesForXfileByTin(String tin, LocalDate from, LocalDate to, ContractStatus status);

    void revertInvoice(Long id);

    void revertInvoiceList(RevertInvoiceRequestDto revertInvoiceRequestDto);

    String getInvoicesDownloadUrl(LocalDate from, LocalDate to, ContractStatus status);

    Page<ServiceOrderInvoiceListDto> getInvoicesList(ServiceOrderInvoiceFilter filter);

    void fixInvoices();

    void resendInvoiceToXfileWithNewDate(ResendToXfileRequest dto);

    ServiceOrderInvoiceResponseDto changeInvoiceOwnerAndChangeBalance(ChangeInvoiceOwnerRequestDto dto);

    ServiceOrderInvoiceResponseDto checkInvoiceXfileStatus(Long id);

    Long getDetailsCount(ServiceOrderInvoiceFilter filter);

    void updateInvoiceXfileStatuses(LocalDate from, LocalDate to);
}
