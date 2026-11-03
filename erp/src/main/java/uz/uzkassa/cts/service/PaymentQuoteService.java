package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.dto.paymentQuote.PaymentQuoteDTO;
import uz.uzkassa.cts.dto.paymentQuote.PaymentQuoteDetailDTO;
import uz.uzkassa.cts.enums.PaymentQuote;
import uz.uzkassa.cts.enums.PaymentQuoteStatus;
import uz.uzkassa.cts.enums.PaymentQuoteType;
import uz.uzkassa.cts.filter.PaymentQuoteFilter;

import java.util.List;

public interface PaymentQuoteService extends BaseService<uz.uzkassa.cts.domain.PaymentQuote, Long> {

    Long create(PaymentQuoteDTO paymentQuoteDTO);

    Long update(Long id, Long modelId);

    PaymentQuoteDetailDTO get(Long id);

    Page<PaymentQuoteDetailDTO> list(PaymentQuoteFilter filter);

    String generateHtmlTemplate(Long id);

    List<PaymentQuoteDetailDTO> paymentQuoteListByTin(String tin);

    void updateStatus(Long id, PaymentQuoteStatus status);

    void updatePaymentStatus(Long id, PaymentQuote paymentStatus);

    void updatePaymentType(Long id, PaymentQuoteType paymentType);
}
