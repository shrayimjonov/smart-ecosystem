package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.SaleInvoice;
import uz.uzkassa.cts.dto.sale.invoice.admin.SaleInvoiceListAdminDTO;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;

public interface SaleInvoiceService extends BaseService<SaleInvoice, Long> {

    Page<SaleInvoiceListAdminDTO> getListForAdmin(TradeInInvoiceFilter filter);
}
