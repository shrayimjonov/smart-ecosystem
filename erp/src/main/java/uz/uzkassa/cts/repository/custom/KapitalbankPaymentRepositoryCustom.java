package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.KapitalbankPayment;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.KapitalBankFilter;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface KapitalbankPaymentRepositoryCustom {

    ResultList<KapitalbankPayment> getList(KapitalBankFilter filter);

    List<KapitalbankPayment> getDownloadUrl(KapitalBankFilter filter);

    BigDecimal getConfirmedAmount(KapitalBankFilter filter);

    ResultList<KapitalbankPayment> getList1c(KapitalBankFilter filter);
}
