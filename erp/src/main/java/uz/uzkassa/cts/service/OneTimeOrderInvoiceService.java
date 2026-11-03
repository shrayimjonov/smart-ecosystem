package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.OneTimeOrderInvoice;
import uz.uzkassa.cts.dto.onetimeorderinvoice.admin.OneTimeOrderInvoiceListAdminDTO;
import uz.uzkassa.cts.filter.OneTimeOrderInvoiceFilter;

public interface OneTimeOrderInvoiceService extends BaseService<OneTimeOrderInvoice, Long> {

    Page<OneTimeOrderInvoiceListAdminDTO> getListForAdmin(OneTimeOrderInvoiceFilter filter);
}
