package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.OneTimeOrderInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.OneTimeOrderInvoiceFilter;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OneTimeOrderInvoiceRepositoryCustom {

    List<OneTimeOrderInvoice> getInvoicesForXfile(LocalDate from, LocalDate to, ContractStatus status);

    List<OneTimeOrderInvoice> updateInvoiceXfileStatuses();

    List<ServiceOrderInvoiceListDto> getInvoices(LocalDate from, LocalDate to, ContractStatus status);

    ResultList<OneTimeOrderInvoice> getResultList(OneTimeOrderInvoiceFilter filter);
}
