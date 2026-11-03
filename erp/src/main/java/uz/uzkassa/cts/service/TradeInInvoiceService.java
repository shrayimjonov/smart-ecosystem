package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.TradeInInvoice;
import uz.uzkassa.cts.dto.tradeininvoice.admin.TradeInInvoiceListAdminDTO;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;

public interface TradeInInvoiceService extends BaseService<TradeInInvoice, Long> {

    Page<TradeInInvoiceListAdminDTO> getListForAdmin(TradeInInvoiceFilter filter);
}
