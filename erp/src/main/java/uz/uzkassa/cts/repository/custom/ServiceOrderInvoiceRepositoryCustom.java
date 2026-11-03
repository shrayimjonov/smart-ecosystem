package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ServiceOrderInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.ServiceOrderInvoiceFilter;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServiceOrderInvoiceRepositoryCustom {

    List<ServiceOrderInvoiceListDto> getInvoiceList(LocalDate from, LocalDate to, ContractStatus status);

    List<ServiceOrderInvoice> getInvoicesForXfile(LocalDate from, LocalDate to, ContractStatus status);

    List<ServiceOrderInvoice> resendInvoicesToXfile(LocalDate from, LocalDate to, ContractStatus status);

    List<ServiceOrderInvoice> getInvoicesForXfileByTin(String tin, LocalDate from, LocalDate to, ContractStatus status);

    List<ServiceOrderInvoice> resendInvoicesForXfileByTin(String tin, LocalDate from, LocalDate to, ContractStatus status);

    List<ServiceOrderInvoice> fixInvoices();

    List<ServiceOrderInvoice> updateInvoiceXfileStatuses(LocalDate from, LocalDate to);

    ResultList<ServiceOrderInvoiceListDto> getInvoiceList_Spec(ServiceOrderInvoiceFilter filter);

    ResultList<ServiceOrderInvoice> getResultList(ServiceOrderInvoiceFilter filter);
}
