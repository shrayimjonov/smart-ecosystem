package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.ServiceOrderInvoice;
import uz.uzkassa.cts.dto.serviceorderinvoice.admin.ServiceOrderInvoiceListAdminDTO;
import uz.uzkassa.cts.filter.ServiceOrderInvoiceFilter;

public interface ServiceOrderInvoiceService extends BaseService<ServiceOrderInvoice, Long> {

    Page<ServiceOrderInvoiceListAdminDTO> getListForAdmin(ServiceOrderInvoiceFilter filter);
}
