package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.SaleInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;

@Repository
public interface SaleInvoiceRepositoryCustom {

    ResultList<SaleInvoice> getResultList(TradeInInvoiceFilter filter);
}
