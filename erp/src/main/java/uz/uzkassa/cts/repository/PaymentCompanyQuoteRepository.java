package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.PaymentCompanyQuote;

import java.util.List;

@Repository
public interface PaymentCompanyQuoteRepository extends BaseRepository<PaymentCompanyQuote, Long> {

    List<PaymentCompanyQuote> findAllByCompany_TinAndPaymentCompanyContractsIsNull(String tin);
}
