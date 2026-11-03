package uz.uzkassa.cts.repository.custom;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ActOfTransmissionInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.serviceorderinvoice.response.ServiceOrderInvoiceListDto;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ActOfTransmissionInvoiceRepositoryCustom {

    Specification<ActOfTransmissionInvoice> getSpecification(PaymentScheduleInvoiceFilter filter);

    List<ActOfTransmissionInvoice> updateInvoiceXfileStatus();

    List<ServiceOrderInvoiceListDto> getInvoices(LocalDate from, LocalDate to, ContractStatus status);

    ResultList<ActOfTransmissionInvoice> getResultList(PaymentScheduleInvoiceFilter filter);
}
