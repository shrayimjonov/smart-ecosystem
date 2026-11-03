package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.ListInvoice;
import uz.uzkassa.silen.dto.filter.InvoiceFilter;
import uz.uzkassa.silen.dto.invoice.DashboardInfoDTO;

public interface ListInvoiceRepositoryCustom {
    Page<ListInvoice> findAllByFilter(InvoiceFilter filter, boolean forExcel);

    DashboardInfoDTO getDashboardInfo(InvoiceFilter filter);
}
