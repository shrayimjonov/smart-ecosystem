package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.PaymentQuote;
import uz.uzkassa.cts.enums.PaymentQuoteStatus;

import java.util.List;

@Repository
public interface PaymentQuoteRepository extends BaseRepository<PaymentQuote, Long> {

    List<PaymentQuote> findAllByTinAndStatusAndPaymentContractsIsNull(String tin, PaymentQuoteStatus status);
}
