package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.TradeInInvoice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;

import java.util.List;

@Repository
public interface TradeInInvoiceRepositoryCustom {

    List<TradeInInvoice> updateInvoiceXfileStatuses();

    ResultList<TradeInInvoice> getResultList(TradeInInvoiceFilter filter);
}
